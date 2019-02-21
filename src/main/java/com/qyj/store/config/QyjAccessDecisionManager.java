package com.qyj.store.config;

import com.qyj.store.entity.SysMenuModel;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * 验证访问权限
 * @CTF_stone
 */
@Component
public class QyjAccessDecisionManager implements AccessDecisionManager {
    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {
        if (!(authentication.getPrincipal() instanceof QyjUserDetails)) {
           throw new AccessDeniedException("权限不足");
        }
        QyjUserDetails userDetails = (QyjUserDetails) authentication.getPrincipal();
        if (userDetails.isSuperadmin()) {
            // 超级用户拥有所有权限
            return;
        }

        String url = ((FilterInvocation) o).getRequest().getRequestURI().replaceAll("/", "");
        String[] allowUrls = new String[]{"admin/user/getUserInfo", "admin/user/logout"};
        for (String allowUrl : allowUrls) {
            if (url.equals(allowUrl.replaceAll("/", ""))) {
                return;
            }
        }

        // 用户拥有的访问路径
        List<SysMenuModel> menuList = userDetails.getMenuList();

        boolean notContain = true;

        for (SysMenuModel menu : menuList) {
            if (menu.getUrl().replaceAll("/", "").contains(url)) {
                notContain = false;
                break;
            }
        }

        // 如果访问的路径不在拥有的访问路径中就抛出权限不足
        if (notContain) {
            throw new AccessDeniedException("权限不足");
        }
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
