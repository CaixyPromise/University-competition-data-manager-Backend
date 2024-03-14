-- 数据库初始化

-- 切换库
use competition;
create table if not exists announce
(
    id           bigint auto_increment comment '公告id'
        primary key,
    createUserId bigint                                 not null comment '公告创建所属用户id',
    raceId       bigint                                 not null comment '关联的比赛信息',
    title        varchar(30)                            not null comment '公告标题',
    content      varchar(256) default ''                not null comment '公告内容',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '公告信息表' collate = utf8mb4_unicode_ci;

create index announce_raceId_index
    on announce (raceId);

create table if not exists comment_info
(
    id         bigint                             not null comment '评论id'
        primary key,
    userId     bigint                             not null comment '评论创建人',
    raceId     bigint                             not null comment '比赛id',
    content    varchar(512)                       null comment '评论内容',
    replyCount bigint                             null comment '评论数量',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    isDelete   tinyint  default 0                 not null comment '是否被删除'
)
    comment '比赛提问与回答信息表';

create index comment_info_raceId_index
    on comment_info (raceId);

create index comment_info_userId_index
    on comment_info (userId);

create table if not exists demand_takes
(
    id       bigint unsigned auto_increment comment '承接id'
        primary key,
    demandId bigint unsigned                       not null comment '需求ID',
    userId   bigint unsigned                       not null comment '承接者ID',
    takeTime timestamp   default CURRENT_TIMESTAMP not null comment '承接时间',
    status   tinyint(11) default 0                 not null comment '承接状态'
)
    comment '需求承接表';

create index demand_takes_demandId_index
    on demand_takes (demandId);

create index demand_takes_status_index
    on demand_takes (status);

create index demand_takes_userId_index
    on demand_takes (userId);

create table if not exists demands
(
    id          bigint unsigned auto_increment
        primary key,
    title       varchar(255)                          not null comment '需求标题',
    description text                                  not null comment '需求描述',
    status      tinyint(11) default 0                 not null comment '需求状态',
    reward      decimal(10, 2)                        not null comment '报酬',
    creatorId   bigint unsigned                       not null comment '需求发布者ID',
    createTime  timestamp   default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  timestamp   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '最后更新时间',
    deadline    date                                  not null comment '截止日期'
)
    comment '需求市场表';

create index demands_creatorId_index
    on demands (creatorId);

create index demands_status_index
    on demands (status);

create table if not exists department_info
(
    id           bigint auto_increment comment 'id'
        primary key,
    name         varchar(256)                       not null comment '学院名称',
    createUserId bigint                             not null comment '添加该学院的用户id',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除'
)
    comment '学院信息表' collate = utf8mb4_unicode_ci;

create index idx_name
    on department_info (name);

create table if not exists major_info
(
    id           bigint auto_increment comment 'id'
        primary key,
    departId     bigint                             not null comment '学院id',
    name         varchar(256)                       not null comment '学院名称',
    createUserId bigint                             not null comment '创建该目录用户id',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除'
)
    comment '专业信息表' collate = utf8mb4_unicode_ci;

create index idx_depart_id
    on major_info (departId);

create index idx_name
    on major_info (name);

create table if not exists match_info
(
    id                  bigint auto_increment comment '比赛ID'
        primary key,
    matchName           varchar(80)                             not null comment '比赛名称',
    matchDesc           text                                    not null comment '比赛描述',
    matchStatus         tinyint                                 not null comment '比赛状态',
    matchPic            varchar(1024)                           null comment '比赛宣传图片(logo)',
    matchType           varchar(20)                             not null comment '比赛类型: A类, B类, C类',
    matchLevel          varchar(20)                             not null comment '比赛等级: 国家级, 省级',
    matchRule           text                                    not null comment '比赛规则',
    matchPermissionRule text                                    not null comment '比赛所允许的分组(学院/部门): 默认为全部学院/专业专业可以参加',
    matchGroup          text                                    null comment '比赛分组',
    matchTags           text                                    not null comment '比赛标签',
    matchAward          text                                    not null comment '比赛奖品',
    matchFileList       text                                    null comment '比赛附件列表',
    createdUser         bigint                                  not null comment '比赛创建人id',
    teamSize            text                                    null comment '团队成员大小配置【最小, 最大】',
    teacherSize         text                                    null comment '团队指导老师数量配置【最小, 最大】',
    signUpStartTime     timestamp default CURRENT_TIMESTAMP     not null on update CURRENT_TIMESTAMP comment '比赛报名开始时间',
    signUpEndTime       timestamp                               null comment '比赛报名结束时间',
    startTime           timestamp default CURRENT_TIMESTAMP     not null on update CURRENT_TIMESTAMP comment '比赛开始时间',
    endTime             timestamp default '0000-00-00 00:00:00' not null comment '比赛结束时间',
    createTime          datetime  default CURRENT_TIMESTAMP     not null comment '创建时间',
    updateTime          datetime  default CURRENT_TIMESTAMP     not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete            tinyint   default 0                     not null comment '是否删除'
)
    comment '比赛信息表' collate = utf8mb4_unicode_ci;

create table if not exists message
(
    id             bigint                             not null comment '消息id'
        primary key,
    subject        varchar(64)                        null comment '消息主题',
    content        varchar(1024)                      not null comment '消息内容',
    targetUrl      varchar(512)                       null comment '跳转链接',
    fromUser       bigint                             not null comment '发信人id',
    forUser        bigint                             null comment '接受消息人id',
    relationshipId bigint                             null comment '关联的信息id',
    expireTime     datetime                           null comment '过期时间',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '添加时间'
)
    comment '站内消息信息表';

create index message_expireTime_index
    on message (expireTime);

create index message_forUser_index
    on message (forUser);

create index message_relationshipId_index
    on message (relationshipId);

create table if not exists registration_info
(
    id          bigint                             not null comment '报名id'
        primary key,
    teamId      bigint                             not null comment '报名的团队id',
    raceId      bigint                             not null comment '报名的比赛id',
    createdTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updatedTime datetime default CURRENT_TIMESTAMP not null comment '更新时间'
)
    comment '报名信息表';

create index registration_info_raceId_index
    on registration_info (raceId)
    comment '比赛索引';

create index registration_info_teamId_index
    on registration_info (teamId)
    comment '团队索引';

create table if not exists reply_info
(
    id         bigint                             not null comment '评论id'
        primary key,
    parentId   bigint                             null comment '父评论',
    userId     bigint                             not null comment '评论创建人',
    content    varchar(512)                       null comment '评论内容',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    isDelete   tinyint  default 0                 not null comment '是否被删除'
)
    comment '比赛讨论回复信息表';

create index reply_info_parentId_index
    on reply_info (parentId);

create table if not exists team_info
(
    id          bigint auto_increment comment 'id'
        primary key,
    raceId      bigint                             not null comment '比赛id',
    userId      bigint                             not null comment '队长id',
    name        varchar(256)                       not null comment '队伍名称',
    teamTags    text                               null comment '团队标签',
    description varchar(1024)                      null comment '描述',
    maxNum      int      default 1                 not null comment '最大人数：创建时设置，避免满了还被申请加入',
    categoryId  bigint                             null comment '团队报名大项的id',
    eventId     bigint                             null comment '团队报名小项的id',
    expireTime  datetime                           null comment '过期时间，为比赛报名结束时间',
    isPublic    int      default 0                 not null comment '0 - 公开，1 - 私有，2 - 加密',
    status      int      default 0                 not null comment '0 - 团队组建中; 1 - 报名成功; 2 - 已解散',
    password    varchar(512)                       null comment '密码',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete    tinyint  default 0                 not null comment '是否删除'
)
    comment '队伍信息表';

create index idx_raceId
    on team_info (raceId);

create index idx_userId
    on team_info (userId);

create table if not exists user
(
    id             bigint auto_increment comment 'id'
        primary key,
    userAccount    varchar(64)                           not null comment '账号(用户学号/工号)',
    userPassword   varchar(512)                          not null comment '密码',
    userSex        int                                   null comment '用户',
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

create table if not exists user_team
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint                             null comment '用户id',
    teamId     bigint                             null comment '队伍id',
    raceId     bigint                             not null comment '比赛id',
    userRole   int                                not null comment '团队角色',
    joinTime   datetime default CURRENT_TIMESTAMP null comment '加入时间',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '用户队伍关系';

create index idx_teamId
    on user_team (teamId);

create index idx_userId
    on user_team (userId);

create table if not exists user_wallet
(
    id            int(11) unsigned auto_increment comment '钱包id'
        primary key,
    userId        bigint                                   not null comment '用户id',
    balance       decimal(10, 2) default 0.00              not null comment '余额',
    frozenBalance decimal(10, 2) default 0.00              not null comment '已冻结余额',
    payPassword   varchar(512)   default ''                not null comment '支付密码',
    updateTime    timestamp      default CURRENT_TIMESTAMP not null comment '更新时间',
    isDelete      tinyint(11)                              null comment '是否删除',
    constraint user_id
        unique (userId)
)
    comment '用户钱包信息';

