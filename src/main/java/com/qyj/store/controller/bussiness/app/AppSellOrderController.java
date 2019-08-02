package com.qyj.store.controller.bussiness.app;

import com.qyj.common.page.PageParam;
import com.qyj.common.page.ResultBean;
import com.qyj.common.utils.StringUtils;
import com.qyj.store.config.QyjUserDetails;
import com.qyj.store.controller.BaseController;
import com.qyj.store.entity.QyjSellOrderEntity;
import com.qyj.store.service.QyjSellOrderService;
import com.qyj.store.service.QyjStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 控制器-订单管理
 * @author CTF_stone
 */
@Controller
@RequestMapping("/admin/app/sellOrder")
public class AppSellOrderController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AppSellOrderController.class);

    private QyjSellOrderService sellOrderService;

    private QyjStatisticsService qyjStatisticsService;

    /**
     * 获取销售订单分页数据信息
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/listSellOrderPage", method = RequestMethod.GET)
    public ResultBean listSellOrderPage(HttpServletRequest request) {
        PageParam pageParam = this.initPageParam(request);
        // 订单状态
        String status = request.getParameter("orderStatus");
        // 订单编号
        String orderNumber = request.getParameter("orderNumber");
        // 创建开始时间
        String createTimeBegin = request.getParameter("createTimeBegin");
        // 创建结束时间
        String createTimeEnd = request.getParameter("createTimeEnd");
        // 买家名称
        String userName = request.getParameter("userName");
        // 买家电话
        String mobilePhone = request.getParameter("mobilePhone");
        // 订单开始时间
        String orderTimeBegin = request.getParameter("orderTimeBegin");
        // 订单结束时间
        String orderTimeEnd = request.getParameter("orderTimeEnd");
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("orderStatus", status);
        paramMap.put("likeOrderNumber", orderNumber);
        paramMap.put("createTimeBegin", createTimeBegin);
        paramMap.put("createTimeEnd", createTimeEnd);
        paramMap.put("likeUserName", userName);
        paramMap.put("likeMobilePhone", mobilePhone);
        paramMap.put("orderTimeBegin", orderTimeBegin);
        paramMap.put("orderTimeEnd", orderTimeEnd);
        pageParam.setOrderByCondition("create_time desc");
        return sellOrderService.listSellOrder(pageParam, paramMap);
    }

    /**
     * 添加销售单
     * @param sellOrder
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/addSellOrder", method = RequestMethod.POST)
    public ResultBean addSellOrder(QyjSellOrderEntity sellOrder, HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        QyjUserDetails userDetails = (QyjUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (sellOrder == null) {
            return new ResultBean("0001", "相关参数为空", null);
        }

        sellOrder.setCreateUser(userDetails.getUserId());
        sellOrder.setUpdateUser(userDetails.getUserId());

        ResultBean resultBean = sellOrderService.addSellOrder(sellOrder);

        resultBean.setResult(sellOrderService.getSellOrderAndProductBySellId((Long) resultBean.getResult()));

        return resultBean;
    }

    /**
     * 根据主键查询销售及其商品
     * @param sellId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getSellOrderInfo", method = RequestMethod.GET)
    public ResultBean getSellOrderInfo(Long sellId) {
        return sellOrderService.loadSellOrderAndProductBySellId(sellId);
    }

    /**
     * 更新销售单
     * @param sellOrder
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateSellOrder", method = RequestMethod.POST)
    public ResultBean updateSellOrder(QyjSellOrderEntity sellOrder, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        QyjUserDetails userDetails = (QyjUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (sellOrder == null) {
            return new ResultBean("0001", "相关参数为空", null);
        }

        sellOrder.setUpdateUser(userDetails.getUserId());

        ResultBean resultBean = sellOrderService.editSellOrder(sellOrder);

        resultBean.setResult(sellOrderService.getSellOrderAndProductBySellId((Long) resultBean.getResult()));

        return resultBean;
    }

    /**
     * 删除销售单
     * @param sellId
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteSellOrder", method = RequestMethod.POST)
    public ResultBean deleteSellOrder(Long sellId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QyjUserDetails userDetails = (QyjUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info("deleteSellOrder userId = {}, SellId = {}", userDetails.getUserId(), sellId);

        if (sellId == null) {
            return new ResultBean("0002", "相关参数为空", null);
        }

        return sellOrderService.deleteSellOrder(sellId);
    }

    /**
     * 获取销售产品分页数据
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getSellProductPage", method = RequestMethod.GET)
    public ResultBean getSellProductPage(HttpServletRequest request) {
        PageParam pageParam = this.initPageParam(request);
        // 用户姓名
        String userName = request.getParameter("userName");
        // 产品名称
        String productTitle = request.getParameter("productTitle");
        // 用户姓名或产品
        String nameOrTitle = request.getParameter("nameOrTitle");
        // 销售时间区间
        String orderTimeBegin = request.getParameter("orderTimeBegin");
        String orderTimeEnd = request.getParameter("orderTimeEnd");
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("pageParam", pageParam);

        if (!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(userName.trim())) {
            paramMap.put("userName", userName);
        }
        if (!StringUtils.isEmpty(productTitle) && !StringUtils.isEmpty(productTitle.trim())) {
            paramMap.put("productTitle", productTitle);
        }
        if (!StringUtils.isEmpty(orderTimeBegin) && !StringUtils.isEmpty(orderTimeBegin.trim())) {
            paramMap.put("orderTimeBegin", orderTimeBegin);
        }
        if (!StringUtils.isEmpty(orderTimeEnd) && !StringUtils.isEmpty(orderTimeEnd.trim())) {
            paramMap.put("orderTimeEnd", orderTimeEnd);
        }
        if (!StringUtils.isEmpty(nameOrTitle) && !StringUtils.isEmpty(nameOrTitle.trim())) {
            paramMap.put("nameOrTitle", nameOrTitle);
        }

        return qyjStatisticsService.listSellProductPage(paramMap);
    }

    @Autowired
    public void setSellOrderService(QyjSellOrderService sellOrderService) {
        this.sellOrderService = sellOrderService;
    }

    @Autowired
    public void setQyjStatisticsService(QyjStatisticsService qyjStatisticsService) {
        this.qyjStatisticsService = qyjStatisticsService;
    }
}
