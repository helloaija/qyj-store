package com.qyj.store.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.qyj.common.page.ResultBean;
import com.qyj.store.service.SysMenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qyj.store.dao.SysMenuMapper;
import com.qyj.store.entity.SysMenuModel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.ValidationException;

/**
 * 系统菜单service实现类
 * @author shitongle
 */
@Service("sysMenuService")
public class SysMenuServiceImpl implements SysMenuService {

    private static final Logger logger = LoggerFactory.getLogger(SysMenuServiceImpl.class);

    @Autowired
    private SysMenuMapper sysMenuMapper;

    /**
     * 获取系统所有菜单
     * @return
     */
    @Override
    public List<SysMenuModel> querySysMenuList() throws Exception {
        return sysMenuMapper.querySysMenuList();
    }

    /**
     * 根据id获取菜单
     * @param id
     * @return
     */
    @Override
    public SysMenuModel getMenuById(Long id) throws Exception {
        return sysMenuMapper.getMenuById(id);
    }

    /**
     * 插入菜单
     * @param menuModel
     * @return
     */
    @Override
    public int insertMenu(SysMenuModel menuModel) throws Exception {
        return sysMenuMapper.insertMenu(menuModel);
    }

    /**
     * 更新菜单
     * @param menuModel
     * @return
     */
    @Override
    public int updateMenu(SysMenuModel menuModel) throws Exception {
        if (menuModel.getId() == null || menuModel.getId() == 0) {
            throw new Exception("更新的菜单id为空");
        }
        return sysMenuMapper.updateMenu(menuModel);
    }

    /**
     * 删除菜单以及其子菜单
     * @param id
     * @return
     */
    @Override
    public int deleteMenuAndChildById(Long id) throws Exception {
        return sysMenuMapper.deleteMenuAndChildById(id);
    }

    /**
     * 根据userId获取用户对应的菜单
     * @param userId
     * @return
     */
    @Override
    public List<SysMenuModel> listMenuByUserId(Long userId) {
        return sysMenuMapper.listMenuByUserId(userId);
    }

    /**
     * 删除菜单及其子孙菜单
     * @param menuId
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResultBean deleteMenuAndChild(Long menuId) throws Exception {
        boolean menuExist = false;

        List<SysMenuModel> menuList = sysMenuMapper.querySysMenuList();
        for (SysMenuModel menu : menuList) {
            if (menu.getId().longValue() == menuId.longValue()) {
                menuExist = true;
            }
        }

        if (!menuExist) {
            return new ResultBean("0002", "菜单" + menuId + "不存在", null);
        }

        List<Long> menuIdList = this.getSubMenuIdList(menuList, menuId);

        if (menuIdList == null || menuIdList.isEmpty()) {
            return new ResultBean("0002", "删除菜单不成功", null);
        }

        if (sysMenuMapper.deleteMenuList(menuIdList) != menuIdList.size()) {
            throw new ValidationException("删除菜单不成功");
        }

        logger.info("deleteMenu delete by menuIds:{}", menuIdList.toArray());

        return new ResultBean("0000", "删除菜单及其子孙菜单成功！");
    }

    /**
     * 获取子孙菜单id
     * @param menuList
     * @param menuId
     * @return
     */
    private List<Long> getSubMenuIdList(List<SysMenuModel> menuList, Long menuId) {
        List<Long> allIdList = new ArrayList<>();
        List<Long> subIdList = new ArrayList<>();
        for (SysMenuModel menu : menuList) {
            if (menu.getParentId().longValue() == menuId.longValue()) {
                subIdList.add(menu.getId());
            }
        }

        if (!subIdList.isEmpty()) {
            for (Long id : subIdList) {
                allIdList.addAll(getSubMenuIdList(menuList, id));
            }
        }

        allIdList.add(menuId);
        return allIdList;
    }

}
