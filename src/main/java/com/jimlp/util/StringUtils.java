package com.jimlp.util;

/**
 * 字符串工具类
 * 
 * <br>
 * 创建时间 2017年12月27日上午11:11:54
 *
 * @author jxb
 *
 */
public final class StringUtils {

	private StringUtils() {
	}

	/**
	 * 检查指定的字符串是否为空。
	 * <ul>
	 * <li>SysUtils.isEmpty(null) = true</li>
	 * <li>SysUtils.isEmpty("") = true</li>
	 * <li>SysUtils.isEmpty("   ") = true</li>
	 * <li>SysUtils.isEmpty("abc") = false</li>
	 * </ul>
	 * 
	 * @param value
	 *            待检查的字符串
	 * @return true/false
	 */
	public static boolean isEmpty(String value) {
		return value == null || value.length() == 0 || value.trim().length() == 0;
	}

	/**
	 * 检查指定的字符串是否存在空值。
	 * <ul>
	 * <li>SysUtils.isEmpty(null) = true</li>
	 * <li>SysUtils.isEmpty("") = true</li>
	 * <li>SysUtils.isEmpty("   ") = true</li>
	 * <li>SysUtils.isEmpty("abc") = false</li>
	 * </ul>
	 * 
	 * @param values
	 *            待检查的字符串
	 * @return true/false
	 */
	public static boolean haveEmpty(String... values) {
		int l = values.length;
		for (int i = 0; i < l; i++) {
			if (isEmpty(values[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检查指定的字符串是否全部为空值。
	 * <ul>
	 * <li>SysUtils.isEmpty(null) = true</li>
	 * <li>SysUtils.isEmpty("") = true</li>
	 * <li>SysUtils.isEmpty("   ") = true</li>
	 * <li>SysUtils.isEmpty("abc") = false</li>
	 * </ul>
	 * 
	 * @param values
	 *            待检查的字符串
	 * @return true/false
	 */
	public static boolean isAllEmpty(String... values) {
		int l = values.length;
		for (int i = 0; i < l; i++) {
			if (!isEmpty(values[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 首字母大写
	 * 
	 * @param str
	 * @return
	 */
	public static String firstUpperCase(String str) {
		char[] cs = str.toCharArray();
		if ('a' <= cs[0] && cs[0] <= 'z') {
			cs[0] ^= 32;
		}
		return String.valueOf(cs);
	}

	/**
	 * 把名称转换为小写加下划线的形式。
	 */
	public static String toUnderlineStyle(String name) {
		StringBuilder newName = new StringBuilder();
		int len = name.length();
		for (int i = 0; i < len; i++) {
			char c = name.charAt(i);
			if (Character.isUpperCase(c)) {
				if (i > 0) {
					newName.append("_");
				}
				newName.append(Character.toLowerCase(c));
			} else {
				newName.append(c);
			}
		}
		return newName.toString();
	}

	/**
	 * 把名称转换为首字母小写的驼峰形式。
	 */
	public static String toCamelStyle(String name) {
		StringBuilder newName = new StringBuilder();
		int len = name.length();
		for (int i = 0; i < len; i++) {
			char c = name.charAt(i);
			if (i == 0) {
				newName.append(Character.toLowerCase(c));
			} else {
				newName.append(c);
			}
		}
		return newName.toString();
	}

	/**
	 * 将16进制字符串转字节数组
	 * 
	 * @param hexString
	 *            16进制字符串
	 * @return
	 */
	public static byte[] toByteArray(String hexString) {
		byte digest[] = new byte[hexString.length() / 2];
		for (int i = 0; i < digest.length; i++) {
			String byteString = hexString.substring(2 * i, 2 * i + 2);
			int byteValue = Integer.parseInt(byteString, 16);
			digest[i] = (byte) byteValue;
		}
		return digest;
	}

	/**
	 * 将字节数组转16进制字符串
	 * 
	 * @param b
	 *            字节数组
	 * @return
	 */
	public static String toHexString(byte b[]) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0, l = b.length; i < l; i++) {
			String plainText = Integer.toHexString(0xff & b[i]);
			if (plainText.length() < 2)
				plainText = "0" + plainText;
			hexString.append(plainText);
		}

		return hexString.toString();
	}
}
