package com.qyj.store.controller.system;

import com.qyj.common.exception.ValidException;
import com.qyj.common.page.PageBean;
import com.qyj.common.page.PageParam;
import com.qyj.common.page.ResultBean;
import com.qyj.common.utils.StringUtils;
import com.qyj.store.common.constant.CommonConstant;
import com.qyj.store.common.util.SessionUtil;
import com.qyj.store.config.QyjUserDetails;
import com.qyj.store.controller.BaseController;
import com.qyj.store.entity.SysRoleModel;
import com.qyj.store.service.SysRoleService;
import com.qyj.store.vo.SysUserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * 用户角色信息控制器
 * @author shitongle
 */
@Controller
@RequestMapping("/admin/role")
public class RoleController extends BaseController {

    @Autowired
    private SysRoleService sysRoleService;

    protected static final Logger logger = LoggerFactory.getLogger(RoleController.class);


    /**
     * 跳转到用户角色信息页面
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/loadRolePage", method = RequestMethod.GET)
    public ResultBean loadRolePage(HttpServletRequest request, HttpServletResponse response, SysRoleModel queryRole) {
        try {
            PageParam pageParam = initPageParam(request);
            if (queryRole != null) {
                StringBuffer queryConditionSb = new StringBuffer();
                if (!StringUtils.isEmpty(queryRole.getRoleCode())) {
                    queryConditionSb.append(" and roleCode like '%").append(queryRole.getRoleCode()).append("%' ");
                }
                if (!StringUtils.isEmpty(queryRole.getRoleName())) {
                    queryConditionSb.append(" and roleName like '%").append(queryRole.getRoleName()).append("%' ");
                }

                pageParam.setQueryCondition(queryConditionSb.toString());
            }

            PageBean pageBean = sysRoleService.listSysRolePage(null, pageParam);

            return new ResultBean("0000", "加载数据成功", pageBean);
        } catch (ValidException e1) {
            logger.error("loadRolePage error：", e1);
            return new ResultBean("0001", e1.getMessage(), null);
        } catch (Exception e) {
            logger.error("loadRolePage error：", e);
            return new ResultBean("0001", "系统异常：" + e.getMessage(), null);
        }
    }

    /**
     * 新增用户角色信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/addRole", method = RequestMethod.POST)
    public ResultBean addRole(HttpServletRequest request, HttpServletResponse response,
                              SysRoleModel roleModel, @RequestParam(value = "menuIds", required = false) Long... menuIds) throws Exception {
        QyjUserDetails userDetails = (QyjUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        roleModel.setCreateUser(userDetails.getUserId());
        roleModel.setUpdateUser(userDetails.getUserId());

        if (sysRoleService.addRole(roleModel, menuIds) <= 0) {
            return new ResultBean("0001", "添加角色失败", null);
        }
        return new ResultBean("0000", "添加角色成功", null);
    }

    /**
     * 删除用角色户信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delRole", method = RequestMethod.POST)
    public ResultBean delRole(@RequestParam("roleIds") Long... ids) throws Exception {
        ResultBean resultBean = new ResultBean();
        int result = sysRoleService.delRole(ids);
        if (result <= 0) {
            return resultBean.init("0002", "删除角色失败");
        }
        return resultBean.init("0000", "删除角色成功");
    }

    /**
     * 根据id获取系统用户角色
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getRoleById", method = RequestMethod.GET)
    public ResultBean getRoleById(@RequestParam Long roleId) throws Exception {
        if (roleId == null) {
            return new ResultBean("0002", "参数错误，roleId为空", null);
        }
        Map<String, Object> roleMap = sysRoleService.getRoleAndMenuByRoleId(roleId);

        return new ResultBean("0000", "获取角色成功", roleMap);

    }

    /**
     * 编辑用户角色信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/editRole", method = RequestMethod.POST)
    public ResultBean editRole(HttpServletRequest request, HttpServletResponse response,
                               SysRoleModel roleModel, @RequestParam(value = "menuIds", required = false) Long... menuIds) throws Exception {

        QyjUserDetails userDetails = (QyjUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        roleModel.setUpdateUser(userDetails.getUserId());
        roleModel.setUpdateTime(new Date());

        if (!sysRoleService.editRole(roleModel, menuIds)) {
            return new ResultBean("0001", "编辑角色失败", null);
        }
        return new ResultBean("0000", "编辑角色成功", null);
    }

}
