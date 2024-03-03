package com.caixy.model.dto.message;

import com.caixy.model.enums.message.MessageUserRoleEnum;

import java.util.Date;

/**
 * 站内信模板文件
 *
 * @name: com.caixy.model.dto.message.MessageTemplate
 * @author: CAIXYPROMISE
 * @since: 2024-03-03 01:25
 **/
public class MessageTemplate
{
    public static SendMessageDTO joinTeam(String userName,
                                          String raceName,
                                          String teamName,
                                          Long forUserId,
                                          Long teamId,
                                          Long raceId,
                                          Date expireTime)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("<p>用户：")
                .append(userName)
                .append(" 您好！</p>")
                .append("<p>您已成功加入：")
                .append(raceName)
                .append(" 比赛，队伍名为：")
                .append(teamName)
                .append("</p>")
                .append("<p>队伍详情可在我的团队中查看。</p>")
                .append("<p>祝您比赛愉快！</p>");

        SendMessageDTO dto = new SendMessageDTO();
        dto.setSubject("比赛入队成功通知-" + teamName);
        dto.setContent(builder.toString());
        dto.setExpireTime(expireTime);
        dto.setForUser(forUserId);
        dto.setRelationshipId(raceId);
        dto.setFromUser(Long.valueOf(MessageUserRoleEnum.SYSTEM.getCode()));
        dto.setTargetUrl("/team/profile/" + teamId);
        return dto;
    }

    public static SendMessageDTO rejectTeam(String userName,
                                          String raceName,
                                          String teamName,
                                          Long forUserId,
                                          Long raceId,
                                          Date expireTime)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("<p>用户：")
                .append(userName)
                .append(" 您好！</p>")
                .append("<p>您申请加入：")
                .append(raceName)
                .append(" 比赛，队伍名为：")
                .append(teamName)
                .append(" 的申请已被拒绝</p>")
                .append("<p>欢迎再次来到团队广场找到您心仪的团队吧！</p>")
                .append("<p>祝您比赛愉快！</p>");

        SendMessageDTO dto = new SendMessageDTO();
        dto.setSubject("比赛入队拒绝通知-" + teamName);
        dto.setContent(builder.toString());
        dto.setExpireTime(expireTime);
        dto.setForUser(forUserId);
        dto.setRelationshipId(raceId);
        dto.setFromUser(Long.valueOf(MessageUserRoleEnum.SYSTEM.getCode()));
        dto.setTargetUrl("/team/index");
        return dto;
    }


    public static SendMessageDTO applyTeam(String userName,
                                            String raceName,
                                            String teamName,
                                            Long teamId,
                                            Long forUserId,
                                            Long raceId,
                                            Date expireTime)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("<p>用户：")
                .append(userName)
                .append(" 您好！</p>")
                .append("<p>您创建的：")
                .append(raceName)
                .append(" 比赛，队伍名为：")
                .append(teamName)
                .append(" 有新的同学申请加入，快去审批一下吧</p>")
                .append("<p>祝您比赛愉快！</p>");

        SendMessageDTO dto = new SendMessageDTO();
        dto.setSubject("比赛入队申请通知-" + teamName);
        dto.setContent(builder.toString());
        dto.setExpireTime(expireTime);
        dto.setForUser(forUserId);
        dto.setRelationshipId(raceId);
        dto.setFromUser(Long.valueOf(MessageUserRoleEnum.SYSTEM.getCode()));
        dto.setTargetUrl("/team/profile/" + teamId );
        return dto;
    }
}
