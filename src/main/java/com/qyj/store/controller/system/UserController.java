package com.qyj.store.controller.system;

import com.qyj.common.page.PageBean;
import com.qyj.common.page.PageParam;
import com.qyj.common.page.ResultBean;
import com.qyj.common.utils.StringUtils;
import com.qyj.store.common.constant.CommonConstant;
import com.qyj.store.common.enums.CommonEnums;
import com.qyj.store.common.tree.TreeNode;
import com.qyj.store.common.tree.TreeUtil;
import com.qyj.store.common.util.SessionUtil;
import com.qyj.store.config.QyjUserDetails;
import com.qyj.store.controller.BaseController;
import com.qyj.store.entity.SysMenuModel;
import com.qyj.store.entity.SysUserModel;
import com.qyj.store.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 用户信息控制器
 * @author shitongle
 */
@Controller
@RequestMapping("/admin/user")
public class UserController extends BaseController {

    /**
     * 系统用户service
     */
    private SysUserService sysUserService;

    protected static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * 加载用户信息列表
     * @return resultBean
     */
    @ResponseBody
    @RequestMapping(value = "/loadUserInfoList", method = RequestMethod.GET)
    public ResultBean loadUserInfoList(HttpServletRequest request, SysUserModel queryRole) {
        PageParam pageParam = this.initPageParam(request);

        if (queryRole != null) {
            StringBuilder queryConditionSb = new StringBuilder();
            if (!StringUtils.isEmpty(queryRole.getUserName())) {
                queryConditionSb.append(" and user_name like '%").append(queryRole.getUserName()).append("%' ");
            }

            pageParam.setQueryCondition(queryConditionSb.toString());
        }

        PageBean pageBean = sysUserService.listSysUserPage(null, pageParam);

        return new ResultBean("0000", "加载数据成功", pageBean);
    }

    /**
     * 获取用户对应角色列表
     * @return resultBean
     */
    @ResponseBody
    @RequestMapping(value = "/getUserRoleData", method = RequestMethod.GET)
    public ResultBean getUserRoleData(Long userId, HttpServletRequest request) {
        return sysUserService.getUserRoleData(userId);
    }

    /**
     * 新增用户信息
     * @param userModel 用户信息
     * @param request   request
     * @param roleIds   角色id数组
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public ResultBean addUser(SysUserModel userModel, HttpServletRequest request,
                              @RequestParam(value = "roleIds", required = false) Long... roleIds) {
        QyjUserDetails userDetails = (QyjUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        userModel.setUpdateUser(userDetails.getUserId());
        userModel.setCreateUser(userDetails.getUserId());
        userModel.setCreateTime(new Date());
        userModel.setUpdateTime(new Date());

        return sysUserService.addUser(userModel, roleIds);
    }

    /**
     * 更新用户信息
     * @param userModel 用户信息
     * @param request   request
     * @param roleIds   角色id数组
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
    public ResultBean updateUser(SysUserModel userModel, HttpServletRequest request,
                                 @RequestParam(value = "roleIds", required = false) Long... roleIds) {
        QyjUserDetails userDetails = (QyjUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userModel.setUpdateUser(userDetails.getUserId());
        userModel.setUpdateTime(new Date());
        // 这些数据不更新
        userModel.setLastIp(null);
        userModel.setLastTime(null);
        userModel.setPassword(null);
        userModel.setCreateTime(null);
        userModel.setCreateUser(null);
        userModel.setIpAddr(null);

        return sysUserService.updateUser(userModel, roleIds);
    }

    /**
     * 删除用户信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delUser", method = RequestMethod.POST)
    public ResultBean delUser(@RequestParam("ids") Long... ids) throws Exception {
        sysUserService.delUser(ids);
        return new ResultBean("0000", "删除用户成功！");
    }

    /**
     * 更新用户状态
     * @param userId
     * @param request
     * @param enable
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateUserEnable", method = RequestMethod.POST)
    public ResultBean updateUserEnable(Long userId, HttpServletRequest request, String enable) {
        QyjUserDetails userDetails = (QyjUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info("updateUserEnable message. userId={}, enable={}, optUserId={}, optUserName={}",
                userId, enable, userDetails.getUserId(), userDetails.getUsername());

        if (userId == null) {
            return new ResultBean("0002", "用户id为空！");
        }

        if (!CommonEnums.UserEnableEnum.DISABLED.toString().equals(enable) &&
                !CommonEnums.UserEnableEnum.USABLE.toString().equals(enable)) {
            return new ResultBean("0002", "参数错误！");
        }

        SysUserModel userModel = new SysUserModel();
        userModel.setId(userId);
        userModel.setEnable(enable);

        sysUserService.updateUserInfo(userModel);

        return new ResultBean("0000", "删除用户成功！");
    }

    /**
     * 查询用户拥有的菜单
     * @param response
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    public ResultBean getUserInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QyjUserDetails userDetails = (QyjUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<SysMenuModel> sysMenuList = userDetails.getMenuList();
        List<SysMenuModel> userMenuList = new ArrayList<>();

        for (SysMenuModel model : sysMenuList) {
            if ("MENU".equals(model.getMenuType())) {
                SysMenuModel menu = new SysMenuModel();
                BeanUtils.copyProperties(model, menu);
                userMenuList.add(menu);
            }
        }

        TreeNode rootNode = new TreeNode(new Long(0), "根目录");
        TreeUtil.loadTreeNode(rootNode, userMenuList);

        List<TreeNode> tree = new ArrayList<>();
        tree.add(rootNode);

        SysUserModel user = new SysUserModel();
        user.setUserName(userDetails.getUsername());

        Map<String, Object> userInfoMap = new HashMap<>();
        userInfoMap.put("menu", rootNode.getChildren());
        userInfoMap.put("user", user);

        return new ResultBean("0000", "获取成功", userInfoMap);
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
