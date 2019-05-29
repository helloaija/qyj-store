package com.qyj.store.service;

import com.qyj.common.page.ResultBean;
import com.qyj.store.entity.QyjSellProductEntity;

import java.util.List;
import java.util.Map;

/**
 * 数据统计service
 * @author shitl
 */
public interface QyjStatisticsService {

    /**
     * 加载销售产品按月统计
     * @param paramMap
     * @return
     */
    ResultBean listSellProductMonthPage(Map<String, Object> paramMap);

    /**
     * 加载进货产品按月统计
     * @param paramMap
     * @return
     */
    ResultBean listStockProductMonthPage(Map<String, Object> paramMap);

    /**
     * 获取按月图标数据
     * @param year
     * @return
     */
    ResultBean getProductMonthData(int year);

    /**
     * 加载销售产品数据
     * @param paramMap
     * @return
     */
    ResultBean listSellProductPage(Map<String, Object> paramMap);

    /**
     * 统计获取订单信息
     * @param paramMap
     * @return
     */
    Map<String, Object> countSellProductAllInfo(Map<String, Object> paramMap);

    /**
     * 获取订单信息，关联产品、用户、订单
     * @param paramMap
     * @return
     */
    List<QyjSellProductEntity> listSellProductAllInfo(Map<String, Object> paramMap);
}
