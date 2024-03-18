package com.caixy.competitionservice.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.common.common.ErrorCode;
import com.caixy.common.constant.FileConstant;
import com.caixy.common.constant.RedisConstant;
import com.caixy.common.exception.BusinessException;
import com.caixy.common.exception.ThrowUtils;
import com.caixy.common.utils.InnerFileUtils;
import com.caixy.common.utils.JsonUtils;
import com.caixy.common.utils.RedisOperatorService;
import com.caixy.competitionservice.constants.MatchConstants;
import com.caixy.competitionservice.mapper.MatchInfoMapper;
import com.caixy.competitionservice.service.MatchInfoService;
import com.caixy.model.dto.department.DepartAndMajorValidationResponse;
import com.caixy.model.dto.feign.FileUploadInnerRequest;
import com.caixy.model.dto.match.MatchInfoAddRequest;
import com.caixy.model.dto.match.properties.MatchPermission;
import com.caixy.model.entity.MatchInfo;
import com.caixy.model.entity.User;
import com.caixy.model.enums.FileUploadBizEnum;
import com.caixy.model.vo.match.MatchInfoProfileVO;
import com.caixy.model.vo.match.MatchInfoQueryVO;
import com.caixy.model.vo.user.UserWorkVO;
import com.caixy.serviceclient.service.FileFeignClient;
import com.caixy.serviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author CAIXYPROMISE
* @description 针对表【match_info(比赛信息表)】的数据库操作Service实现
* @createDate 2024-02-06 23:22:54
*/
@Service
@Slf4j
public class MatchInfoServiceImpl extends ServiceImpl<MatchInfoMapper, MatchInfo>
    implements MatchInfoService
{
    @Resource
    private FileFeignClient fileService;
    @Resource
    private RedisOperatorService redisOperatorService;
    @Resource
    private UserFeignClient userService;

    @Override
    public String addMatchInfo(MatchInfoAddRequest postAddRequest, MultipartFile logoFile, User loginUser)
    {
        // 校验学院信息是否合法：判断是否存在对应的学院+专业
        HashMap<Long, List<Long>> processMatchPermissions =
                processMatchPermissions(postAddRequest.getMatchPermissionRule());
        log.info("processMatchPermissions: {}", processMatchPermissions);
        // 如果解析后的权限里有所有权限，则不需要校验学院+专业合法性
        if (isPermissionCollege(processMatchPermissions))
        {
            validateAndProcessPermissions(processMatchPermissions);
        }

        // DTO转Entity
        MatchInfo post = new MatchInfo();
        BeanUtils.copyProperties(postAddRequest, post);
        post.setMatchPermissionRule(JsonUtils.mapToString(processMatchPermissions));
        post.setMatchTags(JsonUtils.toJsonString(postAddRequest.getMatchTags()));
        post.setSignUpStartTime(postAddRequest.getSignupDate().get(0));
        post.setSignUpEndTime(postAddRequest.getSignupDate().get(1));
        post.setStartTime(postAddRequest.getMatchDate().get(0));
        post.setEndTime(postAddRequest.getMatchDate().get(1));
        post.setMatchAward(JsonUtils.toJsonString(postAddRequest.getMatchAward()));
        post.setCreatedUser(loginUser.getId());
        post.setMatchGroup(JsonUtils.toJsonString(postAddRequest.getGroupData()));
        // 校验人数合法性
        validateSizeList(postAddRequest.getTeacherSize());
        validateSizeList(postAddRequest.getTeamSize());
        post.setTeacherSize(JsonUtils.toJsonString(postAddRequest.getTeacherSize()));
        post.setTeamSize(JsonUtils.toJsonString(postAddRequest.getTeamSize()));

        // 校验图片
        File imageFile = null;
        try
        {
            // 文件名称
            String fileName =
                    DigestUtil.md5Hex(RandomUtil.randomNumbers(3) + "-" + loginUser.getId() + "-" + UUID.randomUUID());
            // 保存在对象存储的最终位置+名称
            String filePath = String.format("%s/%s", FileUploadBizEnum.COMPETITION_LOGO.getValue(), fileName);
            // 存在当前springboot工程里的资源文件夹内，用于存放临时文件
            String tmpFilePath = String.format("%s/static/%s", System.getProperty("user.dir"), fileName);
            // 判断是否为图片
            boolean isImage = InnerFileUtils.isImage(logoFile);
            ThrowUtils.throwIf(!isImage, ErrorCode.PARAMS_ERROR, "图片格式不正确");
            // 校验图片大小
            Long fileSize = InnerFileUtils.getFileSize(logoFile);
            ThrowUtils.throwIf(fileSize > MatchConstants.LOG_MAX_SIZE, ErrorCode.PARAMS_ERROR, "图片大小超过限制");
            // 保存图片至临时文件
            imageFile = InnerFileUtils.multipartFileToTempFile(logoFile, imageFile, tmpFilePath);
            // 上传图片
            FileUploadInnerRequest uploadInnerRequest = new FileUploadInnerRequest();
            // setKey是控制存放对象存储里的位置+文件名
            uploadInnerRequest.setKey(filePath);
            // 需要上传文件的位置: 绝对路径
            uploadInnerRequest.setLocalFilePath(imageFile.getAbsolutePath());
            // 远程调用，上传图片
            Boolean result = fileService.uploadFile(uploadInnerRequest);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "上传图片失败, 请稍后再试");
            post.setMatchPic(FileConstant.COS_HOST + filePath);
        }
        catch (Exception e)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片上传失败");
        }
        finally
        {
            // 删除临时文件，如果存在
            if (imageFile != null && imageFile.exists())
            {
                boolean delete = imageFile.delete();
                if (!delete)
                {
                    log.error("删除临时文件失败: {}", imageFile.getAbsolutePath());
                }
            }
        }

        log.info("entity: {}", post);
        boolean result = this.save(post);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newMatchInfoId = post.getId();
        // 如果需要上传附件，生成上传附件的token作为凭证给前端上传附件
        if (!postAddRequest.getFileList().isEmpty())
        {
            String nonce = RandomUtil.randomNumbers(6);
            Map<String, Object> tokenMap = new HashMap<>();
            DateTime now = DateTime.now();
            // 过期时间：5分钟
            DateTime expire = now.offsetNew(DateField.MINUTE, 5);

            // 设置时间
            tokenMap.put(JWTPayload.ISSUED_AT, now);
            tokenMap.put(JWTPayload.EXPIRES_AT, expire);
            tokenMap.put(JWTPayload.NOT_BEFORE, now);
            // 设置载荷
            tokenMap.put("matchId", newMatchInfoId);
            tokenMap.put("userId", loginUser.getId());
            tokenMap.put("nonce", nonce);
            tokenMap.put("action", "addFile");

            return JWTUtil.createToken(tokenMap, MatchConstants.JWT_TOKEN_KEY);
        }
        else    // 如果不需要上传附件，则直接返回新创建的比赛的id
        {
            return String.valueOf(newMatchInfoId);
        }
    }

    /**
     * 获取比赛详细信息接口，区分管理员与非管理员
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/27 17:23
     */
    @Override
    public MatchInfoProfileVO getMatchInfo(Long matchId, boolean canAdmin)
    {
        log.info("[getMatchInfo] 获取比赛详细信息接口，matchId: {}, canAdmin: {}", matchId, canAdmin);
        MatchInfo matchInfo = this.getById(matchId);
        if (matchInfo == null)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "比赛不存在");
        }
        return getMatchInfoProfileVO(matchInfo, canAdmin);
    }

    /**
     * 批量根据id获取比赛信息
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/28 02:04
     */
    @Override
    public List<MatchInfoProfileVO> getMatchInfoByIds(List<Long> matchIds, boolean canAdmin)
    {
        List<MatchInfo> matchInfoList = this.listByIds(matchIds);
        return matchInfoList.stream().map(matchInfo -> getMatchInfoProfileVO(matchInfo, canAdmin)).collect(Collectors.toList());
    }


    private MatchInfoProfileVO getMatchInfoProfileVO(MatchInfo matchInfo, boolean canAdmin)
    {
        MatchInfoQueryVO matchInfoQueryVO = null;
        // 没有管理员权限
        if (!canAdmin)
        {
            matchInfoQueryVO = MatchInfoQueryVO.convertToProfileVO(matchInfo);
        }
        else
        {   // 有管理员权限
            matchInfoQueryVO = MatchInfoQueryVO.convertToAdminVO(matchInfo);
        }
        // 获取创建人信息
        UserWorkVO userWorkVO = userService.getUserWorkVO(matchInfo.getCreatedUser());
        if (userWorkVO != null)
        {
            matchInfoQueryVO.setCreateUserInfo(userWorkVO);
        }
        log.info("matchInfoQueryVO:{}", matchInfoQueryVO);
        MatchInfoProfileVO profileVO = new MatchInfoProfileVO();
        BeanUtils.copyProperties(matchInfoQueryVO, profileVO);

        HashMap<Long, List<Long>> matchPermissionRule = matchInfoQueryVO.getMatchPermissionRule();
        HashMap<Long, HashMap<String, String>> finalMatchPermissionRule = new HashMap<>();
        if (isPermissionCollege(matchPermissionRule))
        {
            for (Map.Entry<Long, List<Long>> entry : matchPermissionRule.entrySet())
            {
                // 取出专业id列表
                List<Long> majorIds = entry.getValue();
                // 取出对应学院id下的redis缓存数据
                HashMap<String, String> departmentInfo = redisOperatorService.getHash(
                        RedisConstant.ACADEMY_MAJOR,
                        entry.getKey(),
                        String.class, String.class);
                HashMap<String, String> majorsMap = new HashMap<>();
                log.info("departmentInfo: {}", departmentInfo);
                // 循环majors
                for (Long majorId : majorIds)
                {
                    if (departmentInfo.containsKey(majorId.toString())
                            && departmentInfo.get(majorId.toString()) != null)
                    {
                        // 根据专业id取出专业名称，并且反序列化
                        majorsMap.put(majorId.toString(), departmentInfo.get(majorId.toString()).replaceAll("^\"|\"$", ""));
                    }
                }

                majorsMap.put("name", departmentInfo.get("_name").replaceAll("^\"|\"$", ""));
                finalMatchPermissionRule.put(entry.getKey(), majorsMap);
            }
        }
        profileVO.setMatchPermissionRule(finalMatchPermissionRule);
        return profileVO;
    }


    /**
     * 判断是否是全学院可以参加的权限
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/24 02:55
     */
    private static boolean isPermissionCollege(HashMap<Long, List<Long>> permissionMap)
    {
        return !permissionMap.containsKey(MatchConstants.ALL_COLLEGE_ID);
    }

    /**
     * 格式化权限配置，并且校验学院与专业的是否合法
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/27 17:39
     */
    private void validateAndProcessPermissions(HashMap<Long, List<Long>> permissions)
    {
        if (permissions == null || permissions.isEmpty())
        {
            permissions = new HashMap<Long, List<Long>>()
            {{
                put(MatchConstants.ALL_COLLEGE_ID, Collections.singletonList(-1L));
            }};
        }
        else
        {
            // 执行校验
            DepartAndMajorValidationResponse validated = userService.validateDepartmentsAndMajors(permissions);
            if (!validated.getIsValid())
            {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "学院ID或专业ID无效或不匹配");
            }
        }
    }

    public static HashMap<Long, List<Long>> processMatchPermissions(List<List<MatchPermission>> matchPermissionRule)
    {
        HashMap<Long, List<Long>> collegeMajorMap = new HashMap<>();
        if (matchPermissionRule == null || matchPermissionRule.isEmpty())
        {
            return collegeMajorMap;
        }
        for (List<MatchPermission> collegeList : matchPermissionRule)
        {
            for (MatchPermission college : collegeList)
            {
                String collegeValue = college.getValue();
                List<Long> majors = new ArrayList<>();

                if (college.getChildren() != null)
                {
                    for (MatchPermission major : college.getChildren())
                    {
                        majors.add(Long.valueOf(major.getValue()));
                    }
                }
                collegeMajorMap.put(Long.valueOf(collegeValue), majors);
            }
        }
        return collegeMajorMap;
    }

    /**
     * 验证团队人数、指导老师人数size参数值
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/26 01:08
     */
    private void validateSizeList(List<Integer> sizeList)
    {
        // 长度只能为2
        if (sizeList.size() != 2)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "size参数值不正确");
        }
        if (sizeList.get(0) > sizeList.get(1))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最小值不能大于最小值");
        }
        for (Integer size : sizeList)
        {
            if (size < 1 || size > 100)
            {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "size参数值不正确");
            }
        }
    }
}




