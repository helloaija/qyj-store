package com.qyj.store.controller.bussiness;

import com.qyj.common.page.PageBean;
import com.qyj.common.page.PageParam;
import com.qyj.common.page.ResultBean;
import com.qyj.store.common.constant.CommonConstant;
import com.qyj.store.common.enums.CommonEnums.NewsStatusEnum;
import com.qyj.store.common.util.SessionUtil;
import com.qyj.store.controller.BaseController;
import com.qyj.store.entity.QyjNewsInfoEntity;
import com.qyj.store.service.QyjNewsInfoService;
import com.qyj.store.vo.SysUserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("")
public class QyjTestController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(QyjTestController.class);

	/**
	 * 获取新闻公告分页数据信息
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/test")
	public ResultBean test(HttpServletRequest request, HttpServletResponse response) {
		return new ResultBean("100", "200");
	}

}
