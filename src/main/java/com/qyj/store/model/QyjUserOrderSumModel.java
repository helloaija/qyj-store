package com.qyj.store.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 买家用户订单金额信息
 * @author CTF_stone
 */
public class QyjUserOrderSumModel {

    /** 主键ID **/
    private Long id;

    /** 用户姓名 */
    private String userName;

    /** 电话号码 */
    private String mobilePhone;

    /** 订单总额 */
    private BigDecimal orderAmountTotal;

    /** 已支付金额 */
    private BigDecimal hasPayAmountTotal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getOrderAmountTotal() {
        return orderAmountTotal;
    }

    public void setOrderAmountTotal(BigDecimal orderAmountTotal) {
        this.orderAmountTotal = orderAmountTotal;
    }

    public BigDecimal getHasPayAmountTotal() {
        return hasPayAmountTotal;
    }

    public void setHasPayAmountTotal(BigDecimal hasPayAmountTotal) {
        this.hasPayAmountTotal = hasPayAmountTotal;
    }
}