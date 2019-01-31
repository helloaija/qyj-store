package com.qyj.store.controller.bussiness;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qyj.store.config.QyjUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.qyj.store.common.constant.CommonConstant;
import com.qyj.store.common.enums.CommonEnums.ProductStatusEnum;
import com.qyj.store.common.util.FileUtils;
import com.qyj.store.common.util.SessionUtil;
import com.qyj.store.common.util.Utils;
import com.qyj.store.controller.BaseController;
import com.qyj.store.entity.QyjProductEntity;
import com.qyj.store.service.QyjProductService;
import com.qyj.store.vo.QyjProductBean;
import com.qyj.store.vo.SysUserBean;
import com.qyj.common.page.PageBean;
import com.qyj.common.page.PageParam;
import com.qyj.common.page.ResultBean;

@RestController
@RequestMapping("/admin/product")
public class QyjProductController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(QyjProductController.class);

	@Autowired
	private QyjProductService productService;

	/**
	 * 获取产品分页数据信息
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/listProductPage")
	public ResultBean listProductPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PageParam pageParam = this.initPageParam(request);
		// 产品标题
		String productTitle = request.getParameter("title");
		// 产品类型
		String productType = request.getParameter("productType");
		// 创建开始时间
		String createTimeBegin = request.getParameter("createTimeBegin");
		// 创建结束时间
		String createTimeEnd = request.getParameter("createTimeEnd");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("title", productTitle);
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
	@RequestMapping("/getProductInfo")
	public ResultBean getProductInfo(Long productId, HttpServletRequest request, HttpServletResponse response) {
		try {
			QyjProductBean product = productService.selectProductInfo(productId);
			return new ResultBean("0000", "请求成功", product);
		} catch (Exception e) {
			logger.error("getProductInfo error", e);
			return new ResultBean("0001", "请求异常:" + e.getMessage(), e);
		}
	}

	/**
	 * 插入产品信息
	 * @param productEntity
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveProductInfo")
	public ResultBean saveProductInfo(QyjProductEntity productEntity, HttpServletRequest request,
			HttpServletResponse response) throws Exception{
            QyjUserDetails userDetails = (QyjUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			Date nowDate = new Date();
			productEntity.setUpdateTime(nowDate);
			productEntity.setUpdateUser(userDetails.getUserId());

			// id为空插入数据
			if (productEntity.getId() == null || productEntity.getId() == 0) {
				productEntity.setCreateTime(nowDate);
				productEntity.setCreateUser(userDetails.getUserId());
				productEntity.setNumber(0);
				productEntity.setSoldNumber(0);
				productEntity.setUnpayNumber(0);
				productEntity.setProductStatus(ProductStatusEnum.PUBLISH.toString());
				int insertResult = productService.insert(productEntity);
				logger.info("saveProductInfo insert productEntity, info={}, result={}", productEntity.toString(),
						insertResult);
				if (insertResult == 1) {
					return new ResultBean("0000", "新增产品信息成功", insertResult);
				}
				return new ResultBean("0002", "新增产品信息失败", insertResult);
			}

			// 编辑更新数据
			productEntity.setUpdateTime(nowDate);
			int updateResult = productService.updateByPrimaryKey(productEntity);
			logger.info("saveProductInfo update productEntity, info={}, result={}", productEntity.toString(),
					updateResult);
			if (updateResult == 1) {
				return new ResultBean("0000", "更新产品信息成功", updateResult);
			}
			return new ResultBean("0002", "更新产品信息失败", updateResult);
	}

	/**
	 * 根据主键删除产品信息
	 * @param productId
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/delProductInfo")
	public ResultBean delProductInfo(Long productId, HttpServletRequest request, HttpServletResponse response) {
		try {
			int deleteResult = productService.deleteProductInfo(productId);
			if (deleteResult == 1) {
				return new ResultBean("0000", "删除产品信息成功", deleteResult);
			}
			return new ResultBean("0002", "删除产品信息失败", deleteResult);
		} catch (Exception e) {
			logger.error("delProductInfo error", e);
			return new ResultBean("0001", "请求异常:" + e.getMessage(), e);
		}
	}


	/**
	 * 保存产品信息
	 * @param productBean
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(value = "/saveAllProductInfo")
	public ResultBean saveAllProductInfo(QyjProductBean productBean,
			HttpServletResponse response, HttpServletRequest request) {
		try {
			SysUserBean userBean = (SysUserBean) SessionUtil.getAttribute(request, CommonConstant.SESSION_USER);
			 productService.saveAllProductInfo(userBean, productBean);
			 return new ResultBean("0000", "保存成功", null);
		} catch (Exception e) {
			logger.error("saveAllProductInfo error", e);
			return new ResultBean("0001", "请求异常:" + e.getMessage(), e);
		}
	}
	
	/**
	 * 更新产品状态
	 * @param productId 产品id
	 * @param productStatus 产品状态
	 * @param response
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateProductStatus")
	public ResultBean updateProductStatus(Long productId, String productStatus, HttpServletResponse response, HttpServletRequest request) {
		try {
			SysUserBean userBean = (SysUserBean) SessionUtil.getAttribute(request, CommonConstant.SESSION_USER);
			Boolean result = productService.updateProductStatus(userBean, productId, productStatus);
			if (result) {
				return new ResultBean("0000", "成功", null);
			}
			return new ResultBean("0002", "更新状态失败", null);
		} catch (Exception e) {
			logger.error("updateProductStatus error", e);
			return new ResultBean("0001", "请求异常:" + e.getMessage(), e);
		}
	}
}
