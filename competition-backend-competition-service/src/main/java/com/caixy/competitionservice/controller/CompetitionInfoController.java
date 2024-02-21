package com.caixy.competitionservice.controller;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.common.annotation.AuthCheck;
import com.caixy.common.common.BaseResponse;
import com.caixy.common.common.DeleteRequest;
import com.caixy.common.common.ErrorCode;
import com.caixy.common.common.ResultUtils;
import com.caixy.common.constant.FileConstant;
import com.caixy.common.constant.UserConstant;
import com.caixy.common.exception.BusinessException;
import com.caixy.common.exception.ThrowUtils;
import com.caixy.common.utils.InnerFileUtils;
import com.caixy.common.utils.JsonUtils;
import com.caixy.competitionservice.service.MatchInfoService;
import com.caixy.model.dto.department.DepartAndMajorValidationResponse;
import com.caixy.model.dto.feign.FileUploadInnerRequest;
import com.caixy.model.dto.match.MatchInfoAddRequest;
import com.caixy.model.dto.match.MatchInfoQueryRequest;
import com.caixy.model.dto.match.MatchInfoUpdateRequest;
import com.caixy.model.dto.match.properties.MatchPermission;
import com.caixy.model.entity.MatchInfo;
import com.caixy.model.entity.User;
import com.caixy.model.enums.FileUploadBizEnum;
import com.caixy.model.vo.match.MatchInfoQueryVO;
import com.caixy.serviceclient.service.FileFeignClient;
import com.caixy.serviceclient.service.UserFeignClient;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 比赛信息接口控制器
 *
 * @name: com.caixy.competitionservice.controller.CompetitionInfoController
 * @author: CAIXYPROMISE
 * @since: 2024-02-11 13:43
 **/

// todo: 根据是否有文件列表上传来生成JWT Token，以此来根据token来上传
@RestController
@RequestMapping("/Competition")
@Slf4j
public class CompetitionInfoController
{
    @Resource
    private MatchInfoService matchInfoService;

    @Resource
    private UserFeignClient userService;

    @Resource
    private FileFeignClient fileService;

    private static final int COPY_PROPERTIES_ADD = 1;

    private static final int COPY_PROPERTIES_UPDATE = 2;

    private static final int TYPE_LENGTH = 20;

    private static final int LEVEL_LENGTH = 20;

    // Logo图片限制最大大小：5mb
    private static final Long LOG_MAX_SIZE = 5 * 1024 * 1024L;

    private static final byte[] JWT_TOKEN_KEY = "CAIXYPROMISE".getBytes();


    // region 增删改查

    /**
     * 创建
     *
     * @param postAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "添加信息", notes = "添加信息接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "data", value = "业务数据", required = true, dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "file", value = "文件", required = true, dataType = "__file", paramType = "form")
    })
    public BaseResponse<String> addMatchInfo(@RequestPart("file") MultipartFile logoFile,
                                             @RequestPart("data") @Valid MatchInfoAddRequest postAddRequest,
                                             HttpServletRequest request)
    {
        if (postAddRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        log.info("postAddRequest: {}", postAddRequest);
//        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 校验学院信息是否合法：判断是否存在对应的学院+专业
        HashMap<Long, List<Long>> processMatchPermissions =
                processMatchPermissions(postAddRequest.getMatchPermissionRule());
        log.info("processMatchPermissions: {}", processMatchPermissions);
        validateAndProcessPermissions(processMatchPermissions);

        // DTO转Entity
        MatchInfo post = new MatchInfo();
        BeanUtils.copyProperties(postAddRequest, post);
        post.setMatchPermissionRule(JsonUtils.mapToString(processMatchPermissions));
        post.setMatchTags(JsonUtils.objectToString(postAddRequest.getMatchTags()));
        post.setSignUpStartTime(postAddRequest.getSignupDate().get(0));
        post.setSignUpEndTime(postAddRequest.getSignupDate().get(1));
        post.setStartTime(postAddRequest.getMatchDate().get(0));
        post.setEndTime(postAddRequest.getMatchDate().get(1));
        post.setMatchAward(JsonUtils.objectToString(postAddRequest.getMatchAward()));
        post.setCreatedUser(loginUser.getId());

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
            ThrowUtils.throwIf(fileSize > LOG_MAX_SIZE, ErrorCode.PARAMS_ERROR, "图片大小超过限制");
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
//        File logoFile = null;
//        String matchLogo = "";
//        try
//        {
//            String fileName =
//                    DigestUtil.md5Hex(RandomUtil.randomNumbers(3) + "-" + loginUser.getId() + "-" + UUID.randomUUID());
//            String filePath = String.format("%s/%s", FileUploadBizEnum.COMPETITION_LOGO.getValue(), fileName);
//            // 存在当前springboot工程里的资源文件夹内
//            String tmpFilePath = String.format("%s/static/%s", System.getProperty("user.dir"), fileName);
//            byte[] image = InnerFileUtils.checkBase64Image(postAddRequest.getMatchLogo(), LOG_MAX_SIZE);
//            logoFile = InnerFileUtils.saveBase64ImageToFile(image, tmpFilePath);
//            log.info("logoFile: {}", logoFile.getAbsolutePath());
//            // 上传图片
//            FileUploadInnerRequest uploadInnerRequest = new FileUploadInnerRequest();
//            uploadInnerRequest.setKey(filePath);
//            uploadInnerRequest.setLocalFilePath(logoFile.getAbsolutePath());
//            Boolean result = fileService.uploadFile(uploadInnerRequest);
//            if (!result)
//            {
//                throw new BusinessException(ErrorCode.OPERATION_ERROR, "上传图片失败");
//            }
//            matchLogo = FileConstant.COS_HOST + filePath;
//        }
//        catch (Exception e)
//        {
//            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
//        }
//

//        post.setMatchPic(matchLogo);
        log.info("entity: {}", post);

//        entity.setMatchAward(JsonUtils.objectToString(postAddRequest.getMatchReward()));

//        MatchInfo post = copyProperties(postAddRequest, loginUser.getId(), COPY_PROPERTIES_ADD);

        boolean result = matchInfoService.save(post);
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

            String token = JWTUtil.createToken(tokenMap, JWT_TOKEN_KEY);
            return ResultUtils.success(token);
        }
        else
        {
            return ResultUtils.success(String.valueOf(newMatchInfoId));
        }
    }

    /**
     * 更新（仅管理员）
     *
     * @param postUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateMatchInfo(@RequestBody MatchInfoUpdateRequest postUpdateRequest)
    {
        if (postUpdateRequest == null || postUpdateRequest.getId() <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        MatchInfo post = new MatchInfo();
        MatchInfo post = copyProperties(postUpdateRequest, null, COPY_PROPERTIES_UPDATE);
        long id = postUpdateRequest.getId();
        // 判断是否存在
        MatchInfo oldMatchInfo = matchInfoService.getById(id);
        ThrowUtils.throwIf(oldMatchInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = matchInfoService.updateById(post);
        return ResultUtils.success(result);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteMatchInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request)
    {
        if (deleteRequest == null || deleteRequest.getId() <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        MatchInfo oldMatchInfo = matchInfoService.getById(id);
        ThrowUtils.throwIf(oldMatchInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldMatchInfo.getCreatedUser().equals(user.getId()) && !userService.isAdmin(user))
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = matchInfoService.removeById(id);
        return ResultUtils.success(b);
    }


    /**
     * 分页获取列表（仅管理员）
     *
     * @param postQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<MatchInfoQueryVO>> listMatchInfoByPage(@RequestBody MatchInfoQueryRequest postQueryRequest)
    {
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        Page<MatchInfo> postPage = matchInfoService.page(new Page<>(current, size));
        Page<MatchInfoQueryVO> voPage = new Page<>(current, size);
        voPage.setTotal(postPage.getTotal());
        List<MatchInfoQueryVO> voList =
                postPage.getRecords().stream().map(MatchInfoQueryVO::convertToAdminVO).collect(Collectors.toList());
        voPage.setRecords(voList);
        return ResultUtils.success(voPage);
    }

    /**
     * 分页获取列表（全部用户：首页列表）
     *
     * @param postQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<MatchInfoQueryVO>> listMatchInfoByVoPage(@RequestBody MatchInfoQueryRequest postQueryRequest)
    {
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        Page<MatchInfo> postPage = matchInfoService.page(new Page<>(current, size));
        Page<MatchInfoQueryVO> voPage = new Page<>(current, size);
        voPage.setTotal(postPage.getTotal());
        List<MatchInfoQueryVO> voList =
                postPage.getRecords().stream().map(MatchInfoQueryVO::convertToVO).collect(Collectors.toList());
        voPage.setRecords(voList);
        return ResultUtils.success(voPage);
    }

    // endregion


    // 使用泛型和反射优化属性复制方法
    private <T> MatchInfo copyProperties(T sourceItem, Long loginUserId, int type)
    {
        MatchInfo post = new MatchInfo();
        BeanUtils.copyProperties(sourceItem, post);

        // 通用处理，根据类型设置创建用户和处理JSON字段
        if (type == COPY_PROPERTIES_ADD && loginUserId != null)
        {
            post.setCreatedUser(loginUserId);
        }

        // 提取和验证权限规则
//        Map<Long, List<Long>> permissions;
//        if (sourceItem instanceof MatchInfoAddRequest)
//        {
//            permissions = ((MatchInfoAddRequest) sourceItem).getMatchPermissionRule() != null ?
//                          ((MatchInfoAddRequest) sourceItem).getMatchPermissionRule().getPermissions() : null;
//        }
//        else
//        {
//            permissions = ((MatchInfoUpdateRequest) sourceItem).getMatchPermissionRule() != null ?
//                          ((MatchInfoUpdateRequest) sourceItem).getMatchPermissionRule().getPermissions() : null;
//        }
//        validateAndProcessPermissions(permissions);
//
//        // 处理JSON格式的字段
//        processJsonFields(sourceItem, post);

        return post;
    }

    private void validateAndProcessPermissions(HashMap<Long, List<Long>> permissions)
    {
        if (permissions == null || permissions.isEmpty())
        {
            permissions = new HashMap<Long, List<Long>>()
            {{
                put(-1L, Collections.singletonList(-1L));
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


}
