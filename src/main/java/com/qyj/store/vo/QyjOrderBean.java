package com.qyj.store.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单
 * @author CTF_stone
 */
public class QyjOrderBean implements Serializable {

	private static final long serialVersionUID = 5525427361307362076L;

	private Long id;

	/** 用户id */
	private Long userId;

	/** 订单号 */
	private String orderNumber;

	/** 订单实际价格 */
	private BigDecimal orderAmount;

	/** 修改后的订单价格 */
	private BigDecimal modifyAmount;

	/** 订单状态[UNPAY：待支付，UNSEND：代发货，UNTAKE：待收货，END：已结束] */
	private String status;

	/** 买家名字 */
	private String buyerName;

	/** 买家电话 */
	private String buyerPhone;

	/** 买家地址 */
	private String buyerAddress;

	/** 买家留言 */
	private String buyerMessage;

	/** 创建时间 */
	private Date createTime;

	/** 支付时间 */
	private Date payTime;

	/** 完成时间 */
	private Date finishTime;

	/** 更新时间 */
	private Date updateTime;
	
	/** 订单商品列表 */
	List<QyjOrderGoodsBean> orderGoodsList = null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public BigDecimal getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(BigDecimal orderAmount) {
		this.orderAmount = orderAmount;
	}

	public BigDecimal getModifyAmount() {
		return modifyAmount;
	}

	public void setModifyAmount(BigDecimal modifyAmount) {
		this.modifyAmount = modifyAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public String getBuyerPhone() {
		return buyerPhone;
	}

	public void setBuyerPhone(String buyerPhone) {
		this.buyerPhone = buyerPhone;
	}

	public String getBuyerAddress() {
		return buyerAddress;
	}

	public void setBuyerAddress(String buyerAddress) {
		this.buyerAddress = buyerAddress;
	}

	public String getBuyerMessage() {
		return buyerMessage;
	}

	public void setBuyerMessage(String buyerMessage) {
		this.buyerMessage = buyerMessage;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public List<QyjOrderGoodsBean> getOrderGoodsList() {
		return orderGoodsList;
	}

	public void setOrderGoodsList(List<QyjOrderGoodsBean> orderGoodsList) {
		this.orderGoodsList = orderGoodsList;
	}

	@Override
	public String toString() {
		return "QyjOrderBean [id=" + id + ", userId=" + userId + ", orderNumber=" + orderNumber + ", orderAmount="
				+ orderAmount + ", modifyAmount=" + modifyAmount + ", status=" + status + ", buyerName=" + buyerName
				+ ", buyerPhone=" + buyerPhone + ", buyerAddress=" + buyerAddress + ", buyerMessage=" + buyerMessage
				+ ", createTime=" + createTime + ", payTime=" + payTime + ", finishTime=" + finishTime + ", updateTime="
				+ updateTime + "]";
	}
}