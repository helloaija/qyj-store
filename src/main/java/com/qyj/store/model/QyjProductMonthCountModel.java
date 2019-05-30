package com.qyj.store.model;

import java.math.BigDecimal;

/**
 * 产品按月统计model
 */
public class QyjProductMonthCountModel {
    /** 产品id */
    private Long productId;
    /** 产品标题 */
    private String productTitle;
    /** 产品单位 */
    private String productUnit;
    /** 进货、销售总数量 */
    private int totalNumber;
    /** 进货、销售总金额 */
    private BigDecimal totalPrice;
    /** 进货、销售最小金额 */
    private BigDecimal minPrice;
    /** 进货、销售最大金额 */
    private BigDecimal maxPrice;
    /** 经销商数量、购买人数 */
    private int userNumber;
    /** 订单数量 */
    private int orderNumber;
    /** 平均进货价 */
    private BigDecimal avgStockPrice;
    /** 利润总额 */
    private BigDecimal totalProfitAmount;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public int getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(int userNumber) {
        this.userNumber = userNumber;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit;
    }

    public BigDecimal getAvgStockPrice() {
        return avgStockPrice;
    }

    public void setAvgStockPrice(BigDecimal avgStockPrice) {
        this.avgStockPrice = avgStockPrice;
    }

    public BigDecimal getTotalProfitAmount() {
        return totalProfitAmount;
    }

    public void setTotalProfitAmount(BigDecimal totalProfitAmount) {
        this.totalProfitAmount = totalProfitAmount;
    }
}
