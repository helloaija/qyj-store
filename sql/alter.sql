alter table qyj_product add column is_del TINYINT(1) not null default 0 comment '已经被删除[0：未删除，1：已删除]';
alter table qyj_product add column del_user int null default null comment '删除记录人';
alter table qyj_product add column del_time datetime null default null comment '删除记录时间';
alter table qyj_product drop key title;