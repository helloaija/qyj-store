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

import java.util.ArrayList;
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
}
