package com.qyj.common.utils;

/**
 * 字符串工具类
 * @author shitongle
 *
 */
public class StringUtils {
	
	/**
	 * 判断字符串是否为空
	 * @param str 字符串
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return str == null || "".equals(str) || "".equals(str.trim());
	}
}
