package com.qyj.store.controller.bussiness;

import com.qyj.common.page.PageParam;
import com.qyj.common.page.ResultBean;
import com.qyj.store.controller.BaseController;
import com.qyj.store.service.QyjStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据统计controller
 * @author shitl
 */
@Controller
@RequestMapping("admin/statistics")
public class QyjStatisticsController extends BaseController {

    @Autowired
    private QyjStatisticsService qyjStatisticsService;

    /**
     * 获取产品销售按月统计数据
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/listSellProductMonthPage", method = RequestMethod.GET)
    public ResultBean listSellProductMonthPage(HttpServletRequest request) {
        PageParam pageParam = this.initPageParam(request);
        // 年份
        String year = request.getParameter("year");
        // 月份
        String month = request.getParameter("month");

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("year", year);
        paramMap.put("month", month);
        paramMap.put("pageParam", pageParam);

        return qyjStatisticsService.listSellProductMonthPage(paramMap);
    }
}
