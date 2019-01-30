package com.qyj.common.utils;

public class CommonEnums {
	
	/**
	 * 用户状态[USABLE：可用的]
	 * @author shitl
	 *
	 */
	public enum UserStatusEnum {
		USABLE
	}
	
	/**
	 *  产品发布状态[PUBLISH：发布，PUTAWAY：上架，SOLDOUT：下架]
	 * @author shitl
	 *
	 */
	public enum ProductStatusEnum {
		PUBLISH, PUTAWAY, SOLDOUT
	}
	
	/**
	 * 新闻公告状态[PUBLISH：发布，PUTAWAY：上架，SOLDOUT：下架]
	 * @author shitl
	 *
	 */
	public enum NewsStatusEnum {
		PUBLISH, PUTAWAY, SOLDOUT
	}
	
	/**
	 * 产品详细状态
	 * [SHOW:显示,HIDE:隐藏]
	 * @author shitl
	 *
	 */
	public enum ProductDetailStatusEnum {
		SHOW, HIDE
	}
	
	/**
	 * 订单状态[UNPAY：待支付，UNSEND：代发货，UNTAKE：待收货，END：已结束， CANCEL：取消]
	 * @author shitl
	 *
	 */
	public enum OrderStatusEnum {
		UNPAY, UNSEND, UNTAKE, END, CANCEL
	}
}
