package com.qyj.store.entity;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 销售单
 * @author CTF_stone
 */
public class QyjSellOrderEntity implements Serializable {

    private Long id;

    /** 订单号 */
    private String orderNumber;

    /** 订单实际价格 */
    private BigDecimal orderAmount;

    /** 修改后的订单价格 */
    private BigDecimal modifyAmount;

    /** 已支付金额 */
    private BigDecimal hasPayAmount;

    /** 订单状态[UNPAY：待支付，UNSEND：代发货，UNTAKE：待收货，END：已结束] */
    private String orderStatus;

    /** 支付时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    /** 完成时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date orderTime;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 创建人 */
    private Long createUser;

    /** 更新时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /** 更新人 */
    private Long updateUser;

    /** 订单关联的用户ID */
    private Long userId;

    /** 订单关联的用户的名称 */
    private String userName;

    /** 订单关联的用户的电话号码 */
    private String mobilePhone;

    /** 订单商品列表 */
    private List<QyjSellProductEntity> sellProductList = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
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

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public BigDecimal getHasPayAmount() {
        return hasPayAmount;
    }

    public void setHasPayAmount(BigDecimal hasPayAmount) {
        this.hasPayAmount = hasPayAmount;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }

    public Long getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
    }

    public List<QyjSellProductEntity> getSellProductList() {
        return sellProductList;
    }

    public void setSellProductList(List<QyjSellProductEntity> sellProductList) {
        this.sellProductList = sellProductList;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    @Override
    public String toString() {
        return "QyjSellOrderEntity{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", orderAmount=" + orderAmount +
                ", modifyAmount=" + modifyAmount +
                ", hasPayAmount=" + hasPayAmount +
                ", orderStatus='" + orderStatus + '\'' +
                ", payTime=" + payTime +
                ", orderTime=" + orderTime +
                ", remark='" + remark + '\'' +
                ", createTime=" + createTime +
                ", createUser=" + createUser +
                ", updateTime=" + updateTime +
                ", updateUser=" + updateUser +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", sellProductList=" + sellProductList +
                '}';
    }


}