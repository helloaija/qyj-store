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
}