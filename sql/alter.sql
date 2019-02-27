alter table qyj_product add column is_del TINYINT(1) not null default 0 comment '已经被删除[0：未删除，1：已删除]';
alter table qyj_product add column del_user int null default null comment '删除记录人';
alter table qyj_product add column del_time datetime null default null comment '删除记录时间';
alter table qyj_product drop key title;

ALTER TABLE `sys_menu` MODIFY COLUMN `sortNumber`  int(11) NULL DEFAULT 0 COMMENT '排序号' AFTER `menuCode`;

ALTER TABLE `qyj_stock_order`
MODIFY COLUMN `order_status`  varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单状态[UNPAY：未支付，UNPAYALL:未支付完，HASPAYALL: 完成支付]' AFTER `has_pay_amount`,
CHANGE COLUMN `finish_time` `order_time`  datetime NULL DEFAULT NULL COMMENT '完成时间' AFTER `pay_time`,
MODIFY COLUMN `supplier_phone`  varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '供应商电话' AFTER `supplier_name`,
MODIFY COLUMN `supplier_address`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '供应商地址' AFTER `supplier_phone`,
MODIFY COLUMN `supplier_message`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '供应商留言' AFTER `supplier_address`;

ALTER TABLE `qyj_sell_order`
MODIFY COLUMN `order_status`  varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单状态[UNPAY：未支付，UNPAYALL:未支付完，HASPAYALL: 完成支付]' AFTER `has_pay_amount`,
CHANGE COLUMN `finish_time` `order_time`  datetime NULL DEFAULT NULL COMMENT '完成时间' AFTER `pay_time`;

ALTER TABLE `qyj_sell_order`
ADD COLUMN `user_id` int(0) NULL COMMENT '购买用户ID' AFTER `order_status`;

alter table qyj_sell_order
drop COLUMN buyer_name,
drop COLUMN buyer_phone,
drop COLUMN buyer_address,
drop COLUMN buyer_message;

alter table sys_user add open_id varchar(64) null default null comment '微信关联公众号的openId' after tel_phone ;



