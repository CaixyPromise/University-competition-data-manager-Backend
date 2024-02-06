-- 数据库初始化

-- 切换库
use competition;

-- 用户表
create table if NOT exists user
(
    id             BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    userAccount    VARCHAR(256)                          NOT NULL COMMENT '账号',
    userPassword   VARCHAR(512)                          NOT NULL COMMENT '密码',
    userOpenId     CHAR(24)                              NOT NULL COMMENT '用户学/工号',
    userEmail      VARCHAR(256)                          NULL COMMENT '用户邮箱',
    userPhone      VARCHAR(32)                           NULL COMMENT '手机号',
    userDepartment BIGINT                                NULL COMMENT '用户部门/院系id(学院)',
    userMajor      BIGINT                                NULL COMMENT '用户专业id',
    userName       VARCHAR(256)                          NULL COMMENT '用户昵称',
    userTags       TEXT                                  NULL COMMENT '用户标签',
    userAvatar     VARCHAR(1024)                         NULL COMMENT '用户头像',
    userProfile    VARCHAR(512)                          NULL COMMENT '用户简介',
    userRole       VARCHAR(64) DEFAULT 'user'            NOT NULL COMMENT '用户角色：user/admin/ban',
    userRoleLevel  INT         DEFAULT 0                 NOT NULL COMMENT '角色权限等级(细分角色权限)',
    createTime     DATETIME    default CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime     DATETIME    default CURRENT_TIMESTAMP NOT NULL on update CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete       TINYINT     default 0                 NOT NULL COMMENT '是否删除',
    index idx_userOpenId (userOpenId)
) COMMENT '用户' collate = utf8mb4_unicode_ci;

-- 学院信息表
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

-- 专业信息表
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

-- 比赛信息表
CREATE TABLE IF NOT EXISTS `match_info`
(
    id                  BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY COMMENT '比赛ID',
    matchName           VARCHAR(80)                        NOT NULL COMMENT '比赛名称',
    matchDesc           TEXT                               NOT NULL COMMENT '比赛描述',
    matchStatus         TINYINT                            NOT NULL COMMENT '比赛状态',
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