package com.caixy.contentservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.common.annotation.AuthCheck;
import com.caixy.common.common.BaseResponse;
import com.caixy.common.common.DeleteRequest;
import com.caixy.common.common.ErrorCode;
import com.caixy.common.common.ResultUtils;
import com.caixy.common.constant.UserConstant;
import com.caixy.common.exception.BusinessException;
import com.caixy.common.exception.ThrowUtils;
import com.caixy.contentservice.service.AnnounceService;
import com.caixy.model.dto.annouce.AnnouncePageRequest;
import com.caixy.model.dto.annouce.CreateAnnounceRequest;
import com.caixy.model.dto.annouce.UpdateAnnounceRequest;
import com.caixy.model.entity.Announce;
import com.caixy.model.entity.User;
import com.caixy.model.vo.announce.AnnounceVO;
import com.caixy.model.vo.user.UserWorkVO;
import com.caixy.serviceclient.service.UserFeignClient;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 公告栏接口控制器
 * @name: com.caixy.contentservice.controller.AnnounceController
 * @author: CAIXYPROMISE
 * @since: 2024-03-02 01:39
 **/
@RestController
@RequestMapping("/announce")
public class AnnounceController
{

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private AnnounceService announceService;

    @PostMapping("/create")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> createAnnounceByRaceId(@RequestBody
                                                        @Valid
                                                        CreateAnnounceRequest payload,
                                                        HttpServletRequest request)
    {
        if (payload == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        if (payload.getContent().isEmpty() || payload.getContent().length() > 512)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容长度超过限制");
        }
        if (payload.getTitle().isEmpty() || payload.getTitle().length() > 30)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题长度超过限制");
        }
        User loginUser = userFeignClient.getLoginUser(request);
        // 需要管理员
        boolean isAdmin = userFeignClient.isAdmin(loginUser);
        if (!isAdmin)
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限操作");
        }
        return ResultUtils.success(announceService.createAnnounce(payload, loginUser));
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateAnnounceByAnnounceId(@RequestBody @Valid UpdateAnnounceRequest updateRequest,
                                                            HttpServletRequest request)
    {
        if (updateRequest == null || updateRequest.getId() == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 需要登录
        userFeignClient.getLoginUser(request);
        Announce announce = new Announce();
        BeanUtils.copyProperties(updateRequest, announce);
        boolean result = announceService.updateById(announce);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteAnnounce(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request)
    {
        if (deleteRequest == null || deleteRequest.getId() <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 需要登录
        userFeignClient.getLoginUser(request);
        boolean b = announceService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 分页获取公告信息
     *
     * @param pageRequest
     * @param request
     * @return
     */
//    @Deprecated
    @PostMapping("/list/page")
    public BaseResponse<Page<AnnounceVO>> listAnnounceByPage(@RequestBody
                                                       AnnouncePageRequest pageRequest,
                                                   HttpServletRequest request)
    {
        if (pageRequest == null || pageRequest.getRaceId() == null || pageRequest.getRaceId() <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "比赛信息不能为空");
        }
        long current = pageRequest.getCurrent();
        long size = pageRequest.getPageSize();
        // 第二页开始需要登录
        if (current > 2)
        {
            userFeignClient.getLoginUser(request);
        }
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        QueryWrapper<Announce> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("raceId", pageRequest.getRaceId());

        queryWrapper.orderBy(true, "ASC".equals(pageRequest.getSortOrder()), "createTime");
        Page<Announce> userPage = announceService.page(new Page<>(current, size),
                queryWrapper);
        // 需要返回的vo
        Page<AnnounceVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<Long> userIds = userPage.getRecords().stream().map(Announce::getCreateUserId).collect(Collectors.toList());
        if (!userIds.isEmpty())
        {
            List<UserWorkVO> userWorksByIds = userFeignClient.getUserWorksByIds(userIds);

            HashMap<Long, UserWorkVO> userWorkMap = new HashMap<>(userWorksByIds.stream().collect(Collectors.toMap(UserWorkVO::getUserId, userWorkVO -> userWorkVO)));
            List<AnnounceVO> announceVOS = userPage.getRecords().stream().map(item->
            {
                AnnounceVO vo = AnnounceVO.of(item);
                vo.setCreateUser(userWorkMap.get(item.getCreateUserId()));
                return vo;
            }).collect(Collectors.toList());

            userVOPage.setRecords(announceVOS);
            return ResultUtils.success(userVOPage);
        }else {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "暂无公告信息");
        }
    }


}
