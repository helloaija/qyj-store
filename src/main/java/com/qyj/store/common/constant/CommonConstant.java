package com.qyj.store.common.constant;

import com.qyj.store.config.QyjUserDetails;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.concurrent.TimeUnit;

/**
 * 全局静态变量
 * @author CTF_stone
 */
public class CommonConstant {

	/** session中保存的用户信息key */
	public static final String SESSION_USER = "loginUser";

	/** session中保存的用户菜单key */
	public static final String SESSION_MENU = "loginMenu";
	
	/** sesson中保存的验证码key */
	public static final String SESSION_VERIFY = "verifyCode";

	/** 客户端类型-app */
	public static final String CLIENT_TYPE_APP = "APP";

	/** 登录用户信息 */
	public static final ExpiringMap<String, QyjUserDetails> USER_LOGIN_MAP = ExpiringMap.builder().variableExpiration()
			.expirationPolicy(ExpirationPolicy.CREATED).build();
	/** APP登录用户信息 */
	public static final ExpiringMap<String, QyjUserDetails> USER_LOGIN_MAP_APP = ExpiringMap.builder().variableExpiration()
			.expirationPolicy(ExpirationPolicy.CREATED).build();

	/** 登录过期时间 单位秒 */
	public static final long LOGOUT_TIME = 30 * 60;
	/** 登录过期时间-app 单位秒 */
	public static final long LOGOUT_TIME_APP = 60 * 60 * 24 * 15;


	/** 登录验证码，openId-验证码，过期时间三分钟 */
	public static final ExpiringMap<String, String> LOGIN_VALID_CODE_MAP = ExpiringMap.builder().variableExpiration()
			.expirationPolicy(ExpirationPolicy.CREATED).expiration(3 * 60, TimeUnit.SECONDS).build();

}
