package com.qyj.store.service.impl;

import com.qyj.common.page.PageBean;
import com.qyj.common.page.PageParam;
import com.qyj.common.page.ResultBean;
import com.qyj.store.dao.QyjSellProductMapper;
import com.qyj.store.dao.QyjStockProductMapper;
import com.qyj.store.model.QyjProductMonthCountModel;
import com.qyj.store.service.QyjStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计service
 * @author shitl
 */
@Service
public class QyjStatisticsServiceImpl  implements QyjStatisticsService {

    @Autowired
    private QyjSellProductMapper qyjSellProductMapper;

    @Autowired
    private QyjStockProductMapper qyjStockProductMapper;

    /**
     * 加载销售产品按月统计
     * @param paramMap
     * @return
     */
    @Override
    public ResultBean listSellProductMonthPage(Map<String, Object> paramMap) {
        ResultBean resultBean = new ResultBean();

        PageParam pageParam = (PageParam) paramMap.get("pageParam");
        if (pageParam == null) {
            pageParam = new PageParam();
            paramMap.put("pageParam", pageParam);
        }

        Map<String, Object> countMap = qyjSellProductMapper.countSellProductMonth(paramMap);
        int totalCount = Integer.parseInt(String.valueOf(countMap.get("totalCount")));

        pageParam.setTotalCount(totalCount);
        pageParam.splitPageInstance();

        if (totalCount == 0) {
            PageBean pageBean = new PageBean(pageParam.getCurrentPage(), pageParam.getPageSize(), pageParam.getTotalCount(),
                    new ArrayList<QyjProductMonthCountModel>());
            countMap.put("pageBean", pageBean);
            return resultBean.init("0000", "success", countMap);
        }

        List<QyjProductMonthCountModel> list = qyjSellProductMapper.listSellProductMonth(paramMap);
        PageBean pageBean = new PageBean(pageParam.getCurrentPage(), pageParam.getPageSize(), pageParam.getTotalCount(),
                list);
        countMap.put("pageBean", pageBean);

        return resultBean.init("0000", "success", countMap);
    }

    /**
     * 加载进货产品按月统计
     * @param paramMap
     * @return
     */
    @Override
    public ResultBean listStockProductMonthPage(Map<String, Object> paramMap) {
        ResultBean resultBean = new ResultBean();

        PageParam pageParam = (PageParam) paramMap.get("pageParam");
        if (pageParam == null) {
            pageParam = new PageParam();
            paramMap.put("pageParam", pageParam);
        }

        Map<String, Object> countMap = qyjStockProductMapper.countStockProductMonth(paramMap);
        int totalCount = Integer.parseInt(String.valueOf(countMap.get("totalCount")));

        pageParam.setTotalCount(totalCount);
        pageParam.splitPageInstance();

        if (totalCount == 0) {
            PageBean pageBean = new PageBean(pageParam.getCurrentPage(), pageParam.getPageSize(), pageParam.getTotalCount(),
                    new ArrayList<QyjProductMonthCountModel>());
            countMap.put("pageBean", pageBean);
            return resultBean.init("0000", "success", countMap);
        }

        List<QyjProductMonthCountModel> list = qyjStockProductMapper.listStockProductMonth(paramMap);
        PageBean pageBean = new PageBean(pageParam.getCurrentPage(), pageParam.getPageSize(), pageParam.getTotalCount(),
                list);
        countMap.put("pageBean", pageBean);

        return resultBean.init("0000", "success", countMap);
    }

    /**
     * 获取按月图标数据
     * @param year
     * @return
     */
    @Override
    public ResultBean getProductMonthData(int year) {
        // 化肥按月销售额
        Double[] manureSellData = {0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D};
        // 农药按月销售额
        Double[] pesticideSellData = {0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D};
        // 化肥按月进货额
        Double[] manureStockData = {0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D};
        // 农药按月进货额
        Double[] pesticideStockData = {0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D};
        // 按月销售额
        Double[] sumSellData = {0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D};
        // 按月进货额
        Double[] sumStockData = {0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D};

        Map<String, Double[]> resultMap = new HashMap<>();
        resultMap.put("manureSellData", manureSellData);
        resultMap.put("pesticideSellData", pesticideSellData);
        resultMap.put("manureStockData", manureStockData);
        resultMap.put("pesticideStockData", pesticideStockData);
        resultMap.put("sumSellData", sumSellData);
        resultMap.put("sumStockData", sumStockData);

        ResultBean resultBean = new ResultBean();
        List<Map<String, Object>> sellMonthData = qyjSellProductMapper.getMonthSellData(year);
        for (Map<String, Object> map : sellMonthData) {
            int index = Integer.parseInt(String.valueOf(map.get("sellMonth")));
            if ("MANURE".equals(map.get("productType"))) {
                manureSellData[index] = Double.parseDouble(String.valueOf(map.get("monthPrice")));
            }
            if ("PESTICIDE".equals(map.get("productType"))) {
                pesticideSellData[index] = Double.parseDouble(String.valueOf(map.get("monthPrice")));
            }
            sumSellData[index] = sumSellData[index] + Double.parseDouble(String.valueOf(map.get("monthPrice")));
        }

        List<Map<String, Object>> stockMonthData = qyjStockProductMapper.getMonthStockData(year);
        for (Map<String, Object> map : stockMonthData) {
            int index = Integer.parseInt(String.valueOf(map.get("stockMonth")));
            if ("MANURE".equals(map.get("productType"))) {
                manureStockData[index] = Double.parseDouble(String.valueOf(map.get("monthPrice")));
            }
            if ("PESTICIDE".equals(map.get("productType"))) {
                pesticideStockData[index] = Double.parseDouble(String.valueOf(map.get("monthPrice")));
            }
            sumStockData[index] = sumStockData[index] + Double.parseDouble(String.valueOf(map.get("monthPrice")));
        }

        return resultBean.init("0000", "success", resultMap);
    }
}
