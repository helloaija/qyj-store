package com.qyj.store.common.interceptor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qyj.store.common.constant.CommonConstant;
import com.qyj.store.common.util.SessionUtil;
import com.qyj.store.vo.SysUserBean;

/**
 * 过滤未登录用户直接通过走jsp页面访问资源
 * 
 * @author shitongle
 *
 */
public class JspPathFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(JspPathFilter.class);

	@SuppressWarnings("unused")
	private FilterConfig filterConfig = null;

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.filterConfig = config;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException, IOException {
		HttpServletRequest req = (HttpServletRequest) request;
		String path = req.getServletPath();
		logger.info("doFilter path:" + path);
		// 登录用户信息
		SysUserBean bean = (SysUserBean) SessionUtil.getAttribute((HttpServletRequest) request, CommonConstant.SESSION_USER);
		
		if (bean == null) {
			request.getRequestDispatcher("/login/toLoginPage").forward(request, response);
		} else {
			request.getRequestDispatcher(path).forward(request, response);
		}
	}

	@Override
	public void destroy() {
		this.filterConfig = null;
	}
}
