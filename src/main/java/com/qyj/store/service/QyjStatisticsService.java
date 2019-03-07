package com.qyj.store.service;

import com.qyj.common.page.ResultBean;

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
}
