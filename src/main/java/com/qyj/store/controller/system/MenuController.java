package com.qyj.store.controller.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qyj.store.config.QyjUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qyj.store.common.constant.CommonConstant;
import com.qyj.store.common.tree.TreeNode;
import com.qyj.store.common.tree.TreeUtil;
import com.qyj.store.common.util.SessionUtil;
import com.qyj.store.entity.SysMenuModel;
import com.qyj.store.service.SysMenuService;
import com.qyj.store.vo.SysUserBean;
import com.qyj.common.page.ResultBean;

/**
 * 菜单controller
 * @author shitongle
 */
@Controller
@RequestMapping("/admin/menu")
public class MenuController {

    @Autowired
    private SysMenuService sysMenuService;

    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

    /**
     * 根据权限查询所有菜单
     * @param response
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/queryMenuTree", method = RequestMethod.GET)
    public ResultBean queryMenuTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<SysMenuModel> sysMenuList = sysMenuService.querySysMenuList();

        TreeNode rootNode = new TreeNode(new Long(0), "根目录");

        TreeUtil.loadTreeNode(rootNode, sysMenuList);

        List<TreeNode> tree = new ArrayList<>();
        tree.add(rootNode);

        return new ResultBean("0000", "获取成功", tree);
    }

    /**
     * 根据菜单id获取菜单
     * @param menuId
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getMenuById")
    @ResponseBody
    public ResultBean getMenuById(Long menuId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (menuId == null || menuId == 0) {
            return new ResultBean("0002", "菜单id为空", null);
        }

        SysMenuModel menuModel = sysMenuService.getMenuById(menuId);

        if (menuModel == null) {
            return new ResultBean("0002", "获取不到菜单" + menuId, null);
        }

        return new ResultBean("0000", "获取菜单成功", menuModel);
    }

    /**
     * 添加菜单
     * @param menuModel
     * @return
     */
    @ResponseBody
    @RequestMapping("/addMenu")
    public ResultBean addMenu(SysMenuModel menuModel, HttpServletRequest request) throws Exception {
        QyjUserDetails userDetails = (QyjUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (menuModel == null) {
            return new ResultBean("0002", "添加菜单为空", null);
        }
        if (menuModel.getParentId() == null) {
            return new ResultBean("0002", "父菜单id为空", null);
        }

        if (menuModel.getParentId() != 0) {
            // 查找父菜单
            SysMenuModel parentMenuModel = sysMenuService.getMenuById(menuModel.getParentId());
            if (parentMenuModel == null) {
                return new ResultBean("0002", "父菜单为空", null);
            }
        }

        menuModel.setUpdateUser(userDetails.getUserId());
        menuModel.setUpdateTime(new Date());
        menuModel.setCreateUser(userDetails.getUserId());
        menuModel.setCreateTime(new Date());
        if (sysMenuService.insertMenu(menuModel) != 1) {
            return new ResultBean("0002", "新增菜单不成功", null);
        }

        return new ResultBean("0000", "新增菜单成功", TreeUtil.convertModelToNode(menuModel));
    }

    /**
     * 更新菜单
     * @param menuModel
     * @return
     */
    @ResponseBody
    @RequestMapping("/updateMenu")
    public ResultBean updateMenu(SysMenuModel menuModel, HttpServletRequest request) throws Exception {
        QyjUserDetails userDetails = (QyjUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (menuModel == null) {
            return new ResultBean("0002", "更新菜单为空", null);
        }
        if (menuModel.getParentId() == null) {
            return new ResultBean("0002", "父菜单id为空", null);
        }

        menuModel.setUpdateUser(userDetails.getUserId());
        menuModel.setUpdateTime(new Date());
        if (sysMenuService.updateMenu(menuModel) != 1) {
            return new ResultBean("0002", "更新菜单不成功", null);
        }

        return new ResultBean("0000", "更新菜单成功", menuModel);
    }

    /**
     * 删除菜单及其子菜单
     * @param menuId
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/deleteMenu")
    public ResultBean deleteMenu(Long menuId, HttpServletRequest request) throws Exception {
        QyjUserDetails userDetails = (QyjUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (menuId == null || menuId == 0) {
            return new ResultBean("0002", "删除的菜单id为空", null);
        }

        // 记录谁删了哪个菜单
        logger.info("deleteMenu delete by user:{},menuId:{},menuName={}", userDetails.getUserId(), menuId);
        return sysMenuService.deleteMenuAndChild(menuId);
    }

}
