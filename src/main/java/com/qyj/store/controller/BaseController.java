package com.qyj.store.controller;

import javax.servlet.http.HttpServletRequest;

import com.qyj.common.exception.ValidException;
import com.qyj.common.page.PageParam;
import com.qyj.common.utils.NumberUtils;

/**
 * 公用控制器
 * @author CTF_stone
 */
public class BaseController {

	/**
	 * 加载分页信息
	 * @param request
	 * @return
	 */
	public PageParam initPageParam(HttpServletRequest request) {
		PageParam pageParam = new PageParam();
		// 当前页
		String currentPage = request.getParameter("currentPage");
		if (NumberUtils.isInteger(currentPage)) {
			pageParam.setCurrentPage(Integer.valueOf(currentPage));
		}
		// 分页数
		String pageSize = request.getParameter("pageSize");
		if (NumberUtils.isInteger(pageSize)) {
			pageParam.setPageSize(Integer.valueOf(pageSize));
		}

		return pageParam;
	}
	
	/**
	 * 获取异常信息，自定义异常直接返回
	 * @param e
	 * @return
	 */
	public String getExceptionMessage(Exception e) {
		if (e instanceof ValidException) {
			return e.getMessage();
		} else {
			return "系统异常：" + e.getMessage();
		}
	}
}
