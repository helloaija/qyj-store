package com.qyj.store.controller.bussiness;

import com.qyj.common.page.PageBean;
import com.qyj.common.page.PageParam;
import com.qyj.common.page.ResultBean;
import com.qyj.store.common.constant.CommonConstant;
import com.qyj.store.common.util.SessionUtil;
import com.qyj.store.config.QyjUserDetails;
import com.qyj.store.controller.BaseController;
import com.qyj.store.entity.QyjSellOrderEntity;
import com.qyj.store.service.QyjSellOrderService;
import com.qyj.store.vo.SysUserBean;
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
@RequestMapping("/admin/sellOrder")
public class QyjSellOrderController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(QyjSellOrderController.class);

    @Autowired
    private QyjSellOrderService sellOrderService;

    /**
     * 获取销售订单分页数据信息
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/listSellOrderPage", method = RequestMethod.GET)
    public ResultBean listSellOrderPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("orderStatus", status);
        paramMap.put("likeOrderNumber", orderNumber);
        paramMap.put("createTimeBegin", createTimeBegin);
        paramMap.put("createTimeEnd", createTimeEnd);
        paramMap.put("likeUserName", userName);
        paramMap.put("likeMobilePhone", mobilePhone);
        paramMap.put("orderTimeBegin", orderTimeBegin);
        paramMap.put("orderTimeEnd", orderTimeEnd);
        pageParam.setOrderByCondition("create_time desc");
        return sellOrderService.listSellOrderAndProductPage(pageParam, paramMap);
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

        return sellOrderService.addSellOrder(sellOrder);
    }

    /**
     * 根据主键查询销售已经其商品
     * @param sellId
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getSellOrderInfo", method = RequestMethod.GET)
    public ResultBean getSellOrderInfo(Long sellId, HttpServletRequest request, HttpServletResponse response) {
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

        return sellOrderService.editSellOrder(sellOrder);
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
     * 获取用户订单金额统计数据
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/listUserOrderSumPage", method = RequestMethod.GET)
    public ResultBean listUserOrderSumPage(HttpServletRequest request) {
        PageParam pageParam = this.initPageParam(request);
        // 买家名称
        String userName = request.getParameter("userName");
        // 买家电话
        String mobilePhone = request.getParameter("mobilePhone");
        // 订单开始时间
        String orderTimeBegin = request.getParameter("orderTimeBegin");
        // 订单结束时间
        String orderTimeEnd = request.getParameter("orderTimeEnd");
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("likeUserName", userName);
        paramMap.put("likeMobilePhone", mobilePhone);
        paramMap.put("orderTimeBegin", orderTimeBegin);
        paramMap.put("orderTimeEnd", orderTimeEnd);
        pageParam.setOrderByCondition("create_time desc");
        return sellOrderService.listUserOrderSumPage(pageParam, paramMap);
    }
}
