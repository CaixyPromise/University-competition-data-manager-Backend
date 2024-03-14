package com.caixy.marketservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.common.common.BaseResponse;
import com.caixy.common.common.DeleteRequest;
import com.caixy.common.common.ErrorCode;
import com.caixy.common.common.ResultUtils;
import com.caixy.common.exception.BusinessException;
import com.caixy.common.exception.ThrowUtils;
import com.caixy.marketservice.service.DemandTakesService;
import com.caixy.marketservice.service.DemandsService;
import com.caixy.model.dto.market.DemandAddRequest;
import com.caixy.model.dto.market.DemandQueryRequest;
import com.caixy.model.dto.market.DemandUpdateRequest;
import com.caixy.model.dto.market.UpdateDemandStatusRequest;
import com.caixy.model.entity.DemandTakes;
import com.caixy.model.entity.Demands;
import com.caixy.model.entity.User;
import com.caixy.model.entity.UserWallet;
import com.caixy.model.enums.market.DemandStatus;
import com.caixy.model.vo.market.DemandProfileVO;
import com.caixy.model.vo.market.DemandVO;
import com.caixy.model.vo.market.properties.TakerProfileVO;
import com.caixy.model.vo.user.UserWorkVO;
import com.caixy.serviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 需求市场接口控制器
 *
 * @name: com.caixy.marketservice.controller.MarketController
 * @author: CAIXYPROMISE
 * @since: 2024-03-03 17:57
 **/
@RestController
@RequestMapping("/")
@Slf4j
public class MarketController
{
    @Resource
    private DemandsService demandsService;
    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private DemandTakesService demandTakesService;

    @GetMapping("/health")
    public String getHealth()
    {
        return "Competition-backend-market service is running! I am ok!";
    }

    @PostMapping("/add")
    public BaseResponse<Long> addDemands(@RequestBody DemandAddRequest demandAddRequest, HttpServletRequest request)
    {
        if (demandAddRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Demands post = new Demands();
        BeanUtils.copyProperties(demandAddRequest, post);
        demandsService.validPost(post);
        User loginUser = userFeignClient.getLoginUser(request);
        post.setCreatorId(loginUser.getId());
        BigDecimal reward = post.getReward();
        // 获取用户钱包信息
        UserWallet userWallet = userFeignClient.getUserWallet(loginUser.getId());
        if (userWallet != null)
        {
            BigDecimal balance = userWallet.getBalance();
            if (balance.compareTo(reward) < 0)
            {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "余额不足");
            }
            else
            {
                // 检查用户余额是否本次支付
                if (userWallet.getBalance().compareTo(reward) >= 0)
                {
                    // 减少余额，增加冻结金额
                    userWallet.setFrozenBalance(userWallet.getFrozenBalance().add(reward));
                    userWallet.setBalance(userWallet.getBalance().subtract(reward));
                }
                else {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "余额不足");
                }
            }
        }
        boolean result = demandsService.save(post);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newPostId = post.getId();
        return ResultUtils.success(newPostId);
    }

    /**
     * 根据id更新需求
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/3 19:05
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateDemand(@RequestBody DemandUpdateRequest postUpdateRequest,
                                              HttpServletRequest request)
    {
        if (postUpdateRequest == null || postUpdateRequest.getId() <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Demands post = new Demands();
        BeanUtils.copyProperties(postUpdateRequest, post);
        // 参数校验
        demandsService.validPost(post);
        long id = postUpdateRequest.getId();
        User loginUser = userFeignClient.getLoginUser(request);
        final Long userId = loginUser.getId();
        // 判断是否存在
        Demands oldPost = demandsService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        boolean isAuthor = oldPost.getCreatorId().equals(userId);
        if (!isAuthor && !userFeignClient.isAdmin(loginUser))
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限修改");
        }
        boolean result = demandsService.updateById(post);
        return ResultUtils.success(result);
    }

    /**
     * 根据id获取指定需求的详细信息
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/3 19:04
     */
    @GetMapping("/get/vo")
    public BaseResponse<DemandProfileVO> getDemandVOById(@RequestParam("id") long id, HttpServletRequest request)
    {
        if (id <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        Demands post = demandsService.getById(id);
        if (post == null)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Long creatorId = post.getCreatorId();
        UserWorkVO userWorkVO = userFeignClient.getUserWorkVO(creatorId);
        DemandVO demandVO = demandsService.getDemandVO(post, userWorkVO);
        DemandProfileVO profileVO = new DemandProfileVO();
        BeanUtils.copyProperties(demandVO, profileVO);
        // 判断是否是自己的需求信息
        if (loginUser.getId().equals(creatorId))
        {
            profileVO.setIsOwner(true);
            int status = post.getStatus();
            // 判断需求状态，如果是开放状态的，需要获取申请人信息
            if (status == DemandStatus.OPEN.getCode())
            {
                List<DemandTakes> takerInfo = demandTakesService.getDemandTakerIdsByDemandId(id);
                if (!takerInfo.isEmpty())
                {
                    Map<Long, UserWorkVO> userWorksByIdsMap =
                            userFeignClient.getUserWorksByIds(
                                            takerInfo.stream()
                                                    .map(DemandTakes::getUserId)
                                                    .collect(Collectors.toList()))
                                    .stream()
                                    .collect(Collectors.toMap(UserWorkVO::getUserId,
                                            item -> item));
                    if (!userWorksByIdsMap.isEmpty())
                    {
                        List<TakerProfileVO> takerVOStream = takerInfo.stream().map(item ->
                                TakerProfileVO.of(
                                        userWorksByIdsMap.get(item.getUserId()),
                                        item.getTakeTime()
                                )
                        ).collect(Collectors.toList());
                        profileVO.setUserList(takerVOStream);
                    }
                }
                else if (status == DemandStatus.IN_PROGRESS.getCode())
                {
                    DemandTakes demandTakerByDemandId = demandTakesService.getDemandTakerByDemandId(id);
                    UserWorkVO takerVo = userFeignClient.getUserWorkVO(demandTakerByDemandId.getUserId());
                    if (takerVo == null)
                    {
                        profileVO.setUserList(Collections.emptyList());
                    }
                    else
                    {
                        TakerProfileVO takerProfileVO =
                                TakerProfileVO.of(userWorkVO, demandTakerByDemandId.getTakeTime());
                        profileVO.setUserList(Collections.singletonList(takerProfileVO));
                    }
                }
            }
            profileVO.setIsApplied(false);
        }
        else
        {
            profileVO.setIsApplied(demandsService.isTaker(id, loginUser.getId()));
        }
        return ResultUtils.success(profileVO);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<DemandVO>> listDemandVOByPage(@RequestBody DemandQueryRequest postQueryRequest,
                                                           HttpServletRequest request)
    {
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        // -2在获取queryWrapper时，自动忽略正在进行、已关闭状态的数据
        postQueryRequest.setStatus(-2);
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Demands> postPage = demandsService.page(new Page<>(current, size),
                demandsService.getQueryWrapper(postQueryRequest));

        return ResultUtils.success(demandsService.getPostVOPage(postPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/my/vo")
    public BaseResponse<Page<DemandVO>> listMyDemandVOByPage(@RequestBody DemandQueryRequest postQueryRequest,
                                                             HttpServletRequest request)
    {
        if (postQueryRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        postQueryRequest.setCreatorId(loginUser.getId());
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Demands> postPage = demandsService.page(new Page<>(current, size),
                demandsService.getQueryWrapper(postQueryRequest));
        return ResultUtils.success(demandsService.getPostVOPage(postPage, request));
    }

    /**
     * 根据id删除需求
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/3 19:04
     */
    @PostMapping("/delete")
    @Transactional
    public BaseResponse<Boolean> deleteDemandsById(@RequestBody DeleteRequest deleteRequest,
                                                   HttpServletRequest request)
    {
        if (deleteRequest == null || deleteRequest.getId() <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 仅本人或管理员可删除
        User loginUser = userFeignClient.getLoginUser(request);
        final long userId = loginUser.getId();
        long demandId = deleteRequest.getId();
        Demands demand = demandsService.getById(demandId);
        if (demand == null)
        {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "需求不存在");
        }
        boolean isAuthor = demand.getCreatorId().equals(userId);
        if (!isAuthor && !userFeignClient.isAdmin(loginUser))
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限删除");
        }

        if (demand.getStatus() == DemandStatus.IN_PROGRESS.getCode())
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "需求正在进行中，无法删除");
        }

        boolean b = demandsService.removeById(demandId);
        demandTakesService.remove(new QueryWrapper<DemandTakes>().eq("demandId", demandId));
        return ResultUtils.success(b);
    }

    /**
     * 根据id申请承接需求
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/4 00:26
     */
    @PostMapping("/apply")
    public BaseResponse<Boolean> applyForDemand(@RequestBody UpdateDemandStatusRequest
                                                        updateStatusRequest,
                                                HttpServletRequest request)
    {
        if (updateStatusRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        final long userId = userFeignClient.getLoginUser(request).getId();
        return ResultUtils.success(demandsService.applyForDemand(updateStatusRequest.getId(), userId));
    }

    /**
     * 接受用户申请承接需求
     *
     * @param updateStatusRequest 包含需求ID的请求体
     * @param request             HttpServletRequest对象，用于获取当前用户信息
     * @return BaseResponse<Boolean> 操作结果
     */
    @PostMapping("/accept")
    public BaseResponse<Boolean> acceptDemandTake(@RequestBody UpdateDemandStatusRequest updateStatusRequest,
                                                  HttpServletRequest request)
    {
        if (updateStatusRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        final long userId = userFeignClient.getLoginUser(request).getId();
        return ResultUtils.success(demandsService.acceptDemandTake(updateStatusRequest.getId(),
                userId,
                updateStatusRequest.getTargetUser()));
    }

    /**
     * 完成需求任务
     *
     * @param updateStatusRequest 包含需求ID的请求体
     * @param request             HttpServletRequest对象，用于获取当前用户信息
     * @return BaseResponse<Boolean> 操作结果
     */
    @PostMapping("/complete")
    public BaseResponse<Boolean> completeDemand(@RequestBody UpdateDemandStatusRequest updateStatusRequest,
                                                HttpServletRequest request)
    {
        if (updateStatusRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        final long userId = userFeignClient.getLoginUser(request).getId();
        ;
        return ResultUtils.success(demandsService.completeDemand(updateStatusRequest.getId(), userId));
    }

    /**
     * 拒绝用户申请承接需求
     *
     * @param updateStatusRequest 包含需求ID的请求体
     * @param request             HttpServletRequest对象，用于获取当前用户信息
     * @return BaseResponse<Boolean> 操作结果
     */
    @PostMapping("/reject")
    public BaseResponse<Boolean> rejectDemandTake(@RequestBody UpdateDemandStatusRequest updateStatusRequest,
                                                  HttpServletRequest request)
    {
        if (updateStatusRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        final long userId = userFeignClient.getLoginUser(request).getId();
        return ResultUtils.success(demandsService.rejectDemandTake(
                updateStatusRequest.getId(),
                userId,
                updateStatusRequest.getTargetUser()));
    }

}
