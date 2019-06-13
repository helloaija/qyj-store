package com.qyj.store.controller.bussiness.app;

import com.qyj.common.page.PageBean;
import com.qyj.common.page.PageParam;
import com.qyj.common.page.ResultBean;
import com.qyj.store.config.QyjUserDetails;
import com.qyj.store.controller.BaseController;
import com.qyj.store.entity.QyjProductEntity;
import com.qyj.store.service.QyjProductService;
import com.qyj.store.vo.QyjProductBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/app/product")
public class AppProductController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AppProductController.class);

    @Autowired
    private QyjProductService productService;

    /**
     * 获取产品分页数据信息
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/listProductPage", method = RequestMethod.GET)
    public ResultBean listProductPage(HttpServletRequest request, HttpServletResponse response) {
        PageParam pageParam = this.initPageParam(request);
        String id = request.getParameter("id");
        // 产品标题
        String productTitle = request.getParameter("title");
        // 产品类型
        String productType = request.getParameter("productType");
        // 创建开始时间
        String createTimeBegin = request.getParameter("createTimeBegin");
        // 创建结束时间
        String createTimeEnd = request.getParameter("createTimeEnd");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("id", id);
        paramMap.put("title_like", productTitle);
        paramMap.put("productType", productType);
        paramMap.put("createTimeBegin", createTimeBegin);
        paramMap.put("createTimeEnd", createTimeEnd);
        pageParam.setOrderByCondition("create_time desc");
        PageBean pageBean = productService.listProjectPage(pageParam, paramMap);
        return new ResultBean("0000", "请求成功", pageBean);
    }

    /**
     * 根据主键查询产品信息
     * @param productId
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getProductInfo", method = RequestMethod.GET)
    public ResultBean getProductInfo(Long productId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QyjProductBean product = productService.selectProductInfo(productId);
        return new ResultBean("0000", "请求成功", product);
    }

    /**
     * 插入产品信息
     * @param productEntity
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveProductInfo", method = RequestMethod.POST)
    public ResultBean saveProductInfo(QyjProductEntity productEntity, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        QyjUserDetails userDetails = (QyjUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Date nowDate = new Date();
        productEntity.setUpdateTime(nowDate);
        productEntity.setUpdateUser(userDetails.getUserId());

        // id为空插入数据
        if (productEntity.getId() == null || productEntity.getId() == 0) {
            productEntity.setCreateTime(nowDate);
            productEntity.setCreateUser(userDetails.getUserId());
            return productService.insertProduct(productEntity);
        }

        // 编辑更新数据
        productEntity.setUpdateTime(nowDate);
        return productService.updateProduct(productEntity);
    }

    /**
     * 根据主键删除产品信息
     * @param productId
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delProductInfo", method = RequestMethod.POST)
    public ResultBean delProductInfo(Long productId, HttpServletRequest request, HttpServletResponse response) {
        QyjUserDetails userDetails = (QyjUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return productService.deleteProduct(productId, userDetails.getUserId());
    }

    /**
     * 获取库存明细数量
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/listStoreDetail", method = RequestMethod.GET)
    public ResultBean listStoreDetail(HttpServletRequest request, HttpServletResponse response) {
        PageParam pageParam = this.initPageParam(request);
        String id = request.getParameter("id");
        // 产品标题
        String productTitle = request.getParameter("title");
        // 产品类型
        String productType = request.getParameter("productType");
        // 创建开始时间
        String createTimeBegin = request.getParameter("createTimeBegin");
        // 创建结束时间
        String createTimeEnd = request.getParameter("createTimeEnd");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("id", id);
        paramMap.put("title_like", productTitle);
        paramMap.put("productType", productType);
        paramMap.put("createTimeBegin", createTimeBegin);
        paramMap.put("createTimeEnd", createTimeEnd);
        pageParam.setOrderByCondition("create_time desc");
        return productService.listStoreDetail(pageParam, paramMap);
    }

}
