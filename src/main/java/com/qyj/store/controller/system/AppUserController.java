package com.qyj.store.controller.system;

import com.qyj.common.page.ResultBean;
import com.qyj.store.common.constant.CommonConstant;
import com.qyj.store.common.util.SessionUtil;
import com.qyj.store.config.QyjUserDetails;
import com.qyj.store.controller.BaseController;
import com.qyj.store.service.SysUserService;
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
import javax.servlet.http.HttpSession;

/**
 * 用户信息控制器
 * @author shitongle
 */
@Controller
@RequestMapping("/admin/app/user")
public class AppUserController extends BaseController {

    /**
     * 系统用户service
     */
    private SysUserService sysUserService;

    protected static final Logger logger = LoggerFactory.getLogger(AppUserController.class);

    /**
     * 查询用户拥有的菜单
     * @param response
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    public ResultBean getUserInfo(HttpServletRequest request, HttpServletResponse response) {
        QyjUserDetails userDetails = (QyjUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SysUserBean userBean = new SysUserBean();
        userBean.setId(userDetails.getUserId());
        userBean.setUserName(userDetails.getUsername());
        return new ResultBean("0000", "获取成功", userBean);
    }

    /**
     * 清除session
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResultBean logOut(HttpSession session) {
        QyjUserDetails userDetails = (QyjUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CommonConstant.USER_LOGIN_MAP.remove(userDetails.getUsername());
        SessionUtil.removeSessionAll(session);
        return new ResultBean("0000", "请求成功");
    }

    @Autowired
    public void setSysUserService(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }
}
