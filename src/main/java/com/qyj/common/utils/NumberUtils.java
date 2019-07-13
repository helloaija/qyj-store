package com.qyj.common.utils;

/**
 * 字符串工具类
 * @author shitongle
 *
 */
public class NumberUtils {
	
	/**
	 * 判断字符串是整数
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * 判断字符串是整数
	 * @param str
	 * @return
	 */
	public static boolean isLong(String str) {
		try {
			Long.parseLong(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		System.out.println(NumberUtils.isInteger("123."));
	}
}
