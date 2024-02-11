-- 数据库初始化

-- 切换库
use competition;

-- region 用户表
create table user
(
    id             bigint auto_increment comment 'id'
        primary key,
    userAccount    varchar(64)                           not null comment '账号(用户学号/工号)',
    userPassword   varchar(512)                          not null comment '密码',
    userEmail      varchar(256)                          null comment '用户邮箱',
    userPhone      varchar(32)                           null comment '手机号',
    userDepartment bigint                                null comment '用户部门/院系id(学院)',
    userMajor      bigint                                null comment '用户专业id',
    userName       varchar(256)                          null comment '用户昵称',
    userTags       text                                  null comment '用户标签',
    userAvatar     varchar(1024)                         null comment '用户头像',
    userProfile    varchar(512)                          null comment '用户简介',
    userRole       varchar(64) default 'user'            not null comment '用户角色：user/admin/ban',
    userRoleLevel  int         default 0                 not null comment '角色权限等级(细分角色权限)',
    createTime     datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint     default 0                 not null comment '是否删除',
    constraint idx_user_account
        unique (userAccount)
)
    comment '用户' collate = utf8mb4_unicode_ci;

create index idx_user_userDepartment
    on user (userDepartment);

create index idx_user_userMajor
    on user (userMajor);
-- endregion

-- region学院信息表
CREATE TABLE IF NOT EXISTS `department_info`
(
    id         BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    name       VARCHAR(256)                       NOT NULL COMMENT '学院名称',
    createTime DATETIME default CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime DATETIME default CURRENT_TIMESTAMP NOT NULL on update CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete   TINYINT  default 0                 NOT NULL COMMENT '是否删除',
    index idx_name (name)
) COMMENT '学院信息表'
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;
-- endregion

-- region 专业信息表
CREATE TABLE IF NOT EXISTS `major_info`
(
    id         BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    departId   BIGINT                             NOT NULL COMMENT '学院id',
    name       VARCHAR(256)                       NOT NULL COMMENT '学院名称',
    createTime DATETIME default CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime DATETIME default CURRENT_TIMESTAMP NOT NULL on update CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete   TINYINT  default 0                 NOT NULL COMMENT '是否删除',
    index idx_name (name)
) COMMENT '专业信息表'
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;
-- endregion

-- region比赛信息表
CREATE TABLE IF NOT EXISTS `match_info`
(
    id                  BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY COMMENT '比赛ID',
    matchName           VARCHAR(80)                        NOT NULL COMMENT '比赛名称',
    matchDesc           TEXT                               NOT NULL COMMENT '比赛描述',
    matchStatus         TINYINT                            NOT NULL COMMENT '比赛状态: 0-报名中; 1-已开始; 2-已结束;',
    matchPic            VARCHAR(1024)                      NOT NULL COMMENT '比赛宣传图片(logo)',
    matchType           VARCHAR(20)                        NOT NULL COMMENT '比赛类型: A类, B类, C类',
    matchLevel          VARCHAR(20)                        NOT NULL COMMENT '比赛等级: 国家级, 省级',
    matchRule           TEXT                               NOT NULL COMMENT '比赛规则',
    matchPermissionRule TEXT                               NOT NULL COMMENT '比赛所允许的分组(学院/部门): 默认为全部学院/专业专业可以参加',
    matchTags           TEXT                               NOT NULL COMMENT '比赛标签',
    matchAward          TEXT                               NOT NULL COMMENT '比赛奖品',
    createdUser         BIGINT                             NOT NULL COMMENT '比赛创建人id',
    teamSize            INT(11)                            NOT NULL COMMENT '比赛团队大小',
    startTime           TIMESTAMP                          NOT NULL COMMENT '比赛开始时间',
    endTime             TIMESTAMP                          NOT NULL COMMENT '比赛结束时间',
    createTime          DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime          DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete            TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除'
) COMMENT '比赛信息表'
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;
-- endregion

-- region 公告推送信息表
CREATE TABLE IF NOT EXISTS announce
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '公告id',
    createUserId BIGINT                             NOT NULL COMMENT '公告创建所属用户id',
    content      varchar(256)                       NOT NULL DEFAULT '' COMMENT '公告内容',
    createTime   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete     TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除'
) COMMENT '公告信息表'
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;
-- endregion

-- region 团队信息
CREATE TABLE IF NOT EXISTS team_info
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '报名id',
    raceId     BIGINT                             NOT NULL COMMENT '报名的竞赛id',
    leaderId   BIGINT                             NOT NULL COMMENT '队长id',
    teamName   varchar(256)                       NOT NULL DEFAULT '' COMMENT '队伍名称',
    signInfo   TEXT                               NOT NULL COMMENT '队员信息-报名信息json',
    status     int(2)                             NOT NULL DEFAULT '1' COMMENT '报名状态 1：待审核；2：报名成功；3：报名失败(审核失败)',
    remark     varchar(256)                       NOT NULL DEFAULT '' COMMENT '备注',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '报名时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete   TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除'
) COMMENT '(队伍)报名信息表'
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;
-- endregion