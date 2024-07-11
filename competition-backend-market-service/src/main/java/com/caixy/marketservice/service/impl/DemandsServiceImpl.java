package com.caixy.marketservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.common.common.ErrorCode;
import com.caixy.common.constant.CommonConstant;
import com.caixy.common.constant.RedisConstant;
import com.caixy.common.exception.BusinessException;
import com.caixy.common.utils.RedisOperatorService;
import com.caixy.common.utils.SqlUtils;
import com.caixy.marketservice.mapper.DemandsMapper;
import com.caixy.marketservice.service.DemandTakesService;
import com.caixy.marketservice.service.DemandsService;
import com.caixy.model.dto.market.DemandQueryRequest;
import com.caixy.model.entity.DemandTakes;
import com.caixy.model.entity.Demands;
import com.caixy.model.entity.UserWallet;
import com.caixy.model.enums.market.DemandStatus;
import com.caixy.model.enums.market.TaskStatusEnum;
import com.caixy.model.vo.market.DemandVO;
import com.caixy.model.vo.user.UserWorkVO;
import com.caixy.serviceclient.service.UserFeignClient;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author CAIXYPROMISE
 * @description 针对表【demands(需求市场表)】的数据库操作Service实现
 * @createDate 2024-03-03 17:18:45
 */
@Service
public class DemandsServiceImpl extends ServiceImpl<DemandsMapper, Demands>
        implements DemandsService
{

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private DemandTakesService demandTakesService;

    @Resource
    private RedisOperatorService redisOperatorService;


    @Override
    public void validPost(Demands post)
    {
        String description = post.getDescription();
        if (description.isEmpty() || description.length() > 255)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述为空或长度超出255");
        }
        String title = post.getTitle();
        if (title.isEmpty() || title.length() > 30)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题为空或长度超出30");
        }
        boolean after = post.getDeadline().after(new Date());
        if (!after)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "截止日期不能早于当前时间");
        }
    }

    @Override
    public DemandVO getDemandVO(Demands post, UserWorkVO userWorkVO)
    {
        DemandVO demandVO = new DemandVO();
        BeanUtils.copyProperties(post, demandVO);
        demandVO.setCreator(userWorkVO);
        return demandVO;
    }

    @Override
    public boolean isTaker(Long demandId, Long userId)
    {
        QueryWrapper<DemandTakes> queryWrapper = new QueryWrapper<DemandTakes>()
                .eq("demandId", demandId)
                .eq("userId", userId)
                .eq("status", TaskStatusEnum.PENDING.getCode());
        return demandTakesService.count(queryWrapper) > 0;
    }


    @Override
    public Wrapper<Demands> getQueryWrapper(DemandQueryRequest postQueryRequest)
    {
        QueryWrapper<Demands> queryWrapper = new QueryWrapper<>();
        if (postQueryRequest == null)
        {
            return queryWrapper;
        }
        String searchText = postQueryRequest.getSearchText();
        String sortField = postQueryRequest.getSortField();
        String sortOrder = postQueryRequest.getSortOrder();
        Long id = postQueryRequest.getId();
        String title = postQueryRequest.getTitle();
        String content = postQueryRequest.getDescription();
        Long userId = postQueryRequest.getCreatorId();
        Long notId = postQueryRequest.getNotId();
        Integer status = postQueryRequest.getStatus();
        // 如果是-2的时候，是用户查看自己所有的需求的时候，
        if (status != -2)
        {
            queryWrapper.notIn("status", Arrays.asList(DemandStatus.CLOSED.getCode(),
                    DemandStatus.IN_PROGRESS.getCode()));
        }
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText))
        {
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);

        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "creatorId", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<DemandVO> getPostVOPage(Page<Demands> postPage, HttpServletRequest request)
    {
        List<Demands> postList = postPage.getRecords();
        Page<DemandVO> postVOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        if (CollUtil.isEmpty(postList))
        {
            return postVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = postList.stream().map(Demands::getCreatorId).collect(Collectors.toSet());
        Map<Long, UserWorkVO> userIdUserListMap = userFeignClient.getUserWorksByIds(new ArrayList<>(userIdSet))
                .stream()
                .collect(Collectors.toMap(UserWorkVO::getUserId, userWorkVOList -> userWorkVOList));

        // 2. 填充信息
        List<DemandVO> postVOList = postList.stream().map(post ->
                getDemandVO(post, userIdUserListMap.get(post.getCreatorId()))).collect(Collectors.toList());
        postVOPage.setRecords(postVOList);
        return postVOPage;
    }

    /**
     * 申请接单，使用分布式锁保证安全
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/4 01:40
     */
    @Override
    public boolean applyForDemand(Long demandId, Long userId)
    {
        try
        {
            // 尝试获取分布式锁
            boolean lockAcquired = redisOperatorService.tryGetDistributedLock(RedisConstant.DEMAND_LOCK,
                    String.valueOf(demandId),
                    "demand",
                    null);
            if (!lockAcquired)
            {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "系统繁忙，请稍后再试");
            }
            Demands demand = this.getById(demandId);
            if (demand == null)
            {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "需求不存在");
            }
            if (demand.getCreatorId().equals(userId))
            {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "不能承接自己发布的需求");
            }
            if (demand.getDeadline().before(new Date()))
            {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "需求已过截止日期，不可承接");
            }
            if (demand.getStatus() != DemandStatus.OPEN.getCode())
            {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "需求状态不允许承接");
            }

            // 检查是否已经申请过，并且申请状态不是被拒绝
            long appliedNotRejectedCount = demandTakesService.count(
                    new QueryWrapper<DemandTakes>()
                            .eq("demandId", demandId)
                            .eq("userId", userId)
                            .ne("status", TaskStatusEnum.REJECTED.getCode())
            );
            if (appliedNotRejectedCount > 0)
            {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "已经申请过该需求");
            }

            // 检查是否存在被拒绝的申请
            long rejectedCount = demandTakesService.count(
                    new QueryWrapper<DemandTakes>()
                            .eq("demandId", demandId)
                            .eq("userId", userId)
                            .eq("status", TaskStatusEnum.REJECTED.getCode())
            );
            if (rejectedCount > 0)
            {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "你的申请已被拒绝，不能再次申请");
            }

            DemandTakes demandTake = new DemandTakes();
            demandTake.setDemandId(demandId);
            demandTake.setUserId(userId);
            demandTake.setStatus(TaskStatusEnum.PENDING.getCode());
            return demandTakesService.save(demandTake);
        }
        finally
        {
            redisOperatorService.releaseDistributedLock(RedisConstant.DEMAND_LOCK,
                    String.valueOf(demandId),
                    "demand");
        }
    }


    /**
     * 接受任务
     *
     * @return
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/4 01:40
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean acceptDemandTake(Long demandId, Long loginUserId, Long targetUserId)
    {
        try
        {
            // 获取分布式锁，防止单主在接受的时候，有人申请
            boolean lockAcquired = redisOperatorService.tryGetDistributedLock(RedisConstant.DEMAND_LOCK,
                    String.valueOf(demandId),
                    "demand",
                    null);
            if (!lockAcquired)
            {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "系统繁忙，请稍后再试");
            }
            Demands demand = this.getById(demandId);
            if (demand == null || !demand.getCreatorId().equals(loginUserId))
            {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "操作不允许");
            }
            if (demand.getStatus() == DemandStatus.IN_PROGRESS.getCode() || demand.getStatus() == DemandStatus.CLOSED.getCode())
            {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "需求状态不允许接受申请");
            }

            demand.setStatus(DemandStatus.IN_PROGRESS.getCode());
            boolean updateDemand = this.updateById(demand);
            if (!updateDemand)
            {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新需求状态失败");
            }

            DemandTakes demandTake =
                    demandTakesService.getOne(new QueryWrapper<DemandTakes>().eq("demandId", demandId).eq(
                            "status",
                            TaskStatusEnum.PENDING.getCode()));
            if (demandTake != null)
            {
                demandTake.setStatus(TaskStatusEnum.ACCEPTED.getCode());
                return demandTakesService.updateById(demandTake);
            }
            else
            {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "任务不存在");
            }
        }
        finally
        {
            redisOperatorService.releaseDistributedLock(RedisConstant.DEMAND_LOCK,
                    String.valueOf(demandId),
                    "demand");
        }
    }

    /**
     * 完成任务
     *
     * @return
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/4 01:40
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeDemand(Long demandId, Long userId)
    {
        Demands currentDemands = this.getById(demandId);
        if (currentDemands == null)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "需求订单不存在");
        }
        DemandTakes demandTake = demandTakesService.getOne(new QueryWrapper<DemandTakes>().eq("demandId", demandId).eq(
                "userId",
                userId));
        if (demandTake == null || demandTake.getStatus() != TaskStatusEnum.ACCEPTED.getCode())
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "无法完成需求");
        }

        demandTake.setStatus(TaskStatusEnum.COMPLETED.getCode());
        boolean updateById = demandTakesService.updateById(demandTake);
        if (!updateById)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "无法更新完成需求的请求");
        }
        List<Long> userIds = new ArrayList<>();
        userIds.add(demandTake.getUserId());
        userIds.add(currentDemands.getCreatorId());
        Map<Long, UserWallet> bothUserWallet = userFeignClient.getBothUserWallet(userIds);
        UserWallet takerWallet = bothUserWallet.get(demandTake.getUserId());
        UserWallet creatorWallet = bothUserWallet.get(currentDemands.getCreatorId());
        if (takerWallet == null || creatorWallet == null)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户钱包不存在");
        }
        // 需求的费用
        BigDecimal demandPrice = currentDemands.getReward();

        // 检查发单人的冻结余额是否足够
        if (creatorWallet.getFrozenBalance().compareTo(demandPrice) < 0)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "发单人冻结余额不足");
        }

        // 更新发单人的钱包：扣减冻结余额
        creatorWallet.setFrozenBalance(creatorWallet.getFrozenBalance().subtract(demandPrice));

        // 更新接单人的钱包：增加余额
        takerWallet.setBalance(takerWallet.getBalance().add(demandPrice));
        userFeignClient.updateUserWallet(creatorWallet);
        userFeignClient.updateUserWallet(takerWallet);

        Demands demand = this.getById(demandId);
        demand.setStatus(DemandStatus.CLOSED.getCode());
        return this.updateById(demand);
    }

    /**
     * 拒绝接单
     *
     * @return
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/4 01:40
     */
    @Override
    public boolean rejectDemandTake(Long demandId, Long loginUserId, Long targetUserId)
    {
        Demands demand = this.getById(demandId);
        if (demand == null || !demand.getCreatorId().equals(loginUserId) || demand.getStatus() == DemandStatus.CLOSED.getCode())
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "操作不允许");
        }

        DemandTakes demandTake = demandTakesService.getOne(new QueryWrapper<DemandTakes>()
                .eq("demandId", demandId)
                .eq("status", TaskStatusEnum.PENDING.getCode())
                .eq("userId", targetUserId));
        if (demandTake != null)
        {
            demandTake.setStatus(TaskStatusEnum.REJECTED.getCode());
            return demandTakesService.updateById(demandTake);
        }
        else
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "无法更新完成需求的请求");
        }
    }
}




