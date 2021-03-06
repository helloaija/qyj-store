package com.qyj.store.config;

import com.qyj.store.entity.SysMenuModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 登录用户验证
 * @author shitl
 */
public class QyjUserDetails implements UserDetails, Serializable {

	private Long userId;
	private String username;
	private String password;
	private String openId;
	private Set<? extends GrantedAuthority> authorities;
	private List<SysMenuModel> menuList;

	/** 设置当前登录的token */
	private String jwtToken;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAuthorities(Set<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public String getJwtToken() {
		return jwtToken;
	}

	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<SysMenuModel> getMenuList() {
		return menuList;
	}

	public void setMenuList(List<SysMenuModel> menuList) {
		this.menuList = menuList;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public boolean isSuperadmin() {
		// id为1是超级管理员，拥有全部菜单和权限
		return 1 == this.getUserId();
	}

}
