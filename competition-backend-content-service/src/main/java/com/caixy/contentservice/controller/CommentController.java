package com.caixy.contentservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caixy.common.common.BaseResponse;
import com.caixy.common.common.DeleteRequest;
import com.caixy.common.common.ErrorCode;
import com.caixy.common.common.ResultUtils;
import com.caixy.common.exception.BusinessException;
import com.caixy.contentservice.service.CommentInfoService;
import com.caixy.contentservice.service.ReplyInfoService;
import com.caixy.model.dto.comment.AddCommentRequest;
import com.caixy.model.dto.comment.GetReplyRequest;
import com.caixy.model.dto.comment.ListCommentByIdPage;
import com.caixy.model.dto.comment.ReplyCommentRequest;
import com.caixy.model.entity.CommentInfo;
import com.caixy.model.entity.ReplyInfo;
import com.caixy.model.entity.User;
import com.caixy.model.vo.comemt.CommentVO;
import com.caixy.model.vo.user.UserWorkVO;
import com.caixy.serviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 评论信息控制器
 *
 * @name: com.caixy.contentservice.controller.CommentController
 * @author: CAIXYPROMISE
 * @since: 2024-03-01 03:09
 **/
@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentController
{
    @Resource
    private CommentInfoService commentInfoService;

    @Resource
    private ReplyInfoService replyInfoService;

    @Resource
    private UserFeignClient userFeignClient;

    /**
     * 发送评论
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/1 04:27
     */
    @PostMapping("/post")
    public BaseResponse<Boolean> sendCommentByRaceId(@RequestBody @Valid AddCommentRequest addCommentRequest,
                                                     HttpServletRequest request)
    {
        if (addCommentRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        // 获取用户登录
        User loginUser = userFeignClient.getLoginUser(request);
        return ResultUtils.success(commentInfoService.addComment(addCommentRequest, loginUser));
    }

    /**
     * 发送回复
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/1 04:27
     */
    @PostMapping("/post/reply")
    public BaseResponse<Boolean> sendReplyByCommentId(@RequestBody @Valid ReplyCommentRequest replyCommentRequest,
                                                      HttpServletRequest request)
    {
        if (replyCommentRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        // 获取用户登录
        User loginUser = userFeignClient.getLoginUser(request);
        // 检查用户权限
        boolean isAdmin = userFeignClient.isAdmin(loginUser);
        if (!isAdmin)
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限回复");
        }
        return ResultUtils.success(commentInfoService.replyComment(replyCommentRequest, loginUser));
    }

    /**
     * 删除评论
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/1 04:31
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteComment(@RequestBody DeleteRequest deleteRequest)
    {
        if (deleteRequest == null || deleteRequest.getId() <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        boolean result = commentInfoService.deleteComment(deleteRequest.getId());
        return ResultUtils.success(result);
    }

    /**
     * 根据比赛id获取评论列表
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/1 04:27
     */
    @PostMapping("/list/main")
    public BaseResponse<Page<CommentVO>> listMainCommentByRaceId(@RequestBody ListCommentByIdPage listCommentByIdPage)
    {
        if (listCommentByIdPage == null || listCommentByIdPage.getRaceId() <= 0 ||
                listCommentByIdPage.getPageSize() <= 0 || listCommentByIdPage.getCurrent() <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        Page<CommentInfo> commentInfoPage = commentInfoService.selectMainCommentsByRaceId(listCommentByIdPage);
        // 获取评论信息
        Page<CommentVO> commentInfoPageNew = new Page<>(listCommentByIdPage.getCurrent(),
                listCommentByIdPage.getPageSize());

        // 获取用户信息并转vo
        List<CommentVO> commentVOList = convertToCommentVOList(commentInfoPage.getRecords(), CommentVO::of);

        // 设置评论信息
        commentInfoPageNew.setRecords(commentVOList);
        return ResultUtils.success(commentInfoPageNew);
    }

    @PostMapping("/get/reply")
    public BaseResponse<CommentVO> getReplyInfo(@RequestBody GetReplyRequest getReplyRequest)
    {
        if (getReplyRequest == null || getReplyRequest.getCommentId() <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        ReplyInfo replyInfo = replyInfoService.getReplyInfo(getReplyRequest.getCommentId());

        // 获取用户信息并转vo
        UserWorkVO userWorkVO = userFeignClient.getUserWorkVO(replyInfo.getUserId());
        CommentVO commentVO = CommentVO.of(replyInfo);
        commentVO.setHasReply(false);
        commentVO.setCreateUserInfo(userWorkVO);
        return ResultUtils.success(commentVO);
    }

    /**
     * 通用方法来转换评论或回复列表到CommentVO列表。
     *
     * @param sourceList 来源列表，可以是CommentInfo或ReplyInfo列表。
     * @param converter  将单个来源实体转换为CommentVO的函数。
     * @param <T>        来源实体类型。
     * @return 转换后的CommentVO列表。
     */
    private <T> List<CommentVO> convertToCommentVOList(
            List<T> sourceList,
            Function<T, CommentVO> converter)
    {
        // 获取用户ID列表
        List<Long> userIds = sourceList.stream()
                .map(item ->
                {
                    try
                    {
                        return (Long) item.getClass().getMethod("getUserId").invoke(item);
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException("获取userId失败", e);
                    }
                })
                .collect(Collectors.toList());

        // 获取用户信息
        List<UserWorkVO> userList = userFeignClient.getUserWorksByIds(userIds);
        Map<Long, UserWorkVO> userMap = userList.stream()
                .collect(Collectors.toMap(UserWorkVO::getUserId, Function.identity()));

        // 转换为CommentVO列表
        return sourceList.stream()
                .map(item ->
                {
                    CommentVO vo = converter.apply(item);
                    vo.setCreateUserInfo(userMap.get(vo.getUserId()));
                    return vo;
                })
                .collect(Collectors.toList());
    }
}
