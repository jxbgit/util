package com.jimlp.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class ChineseUtils {
	/**
	 * 把unicode编码转化为汉字。
	 * 
	 * @param unicode
	 * @return
	 */
	public static String unicodeToChinese(String unicode) {
		if (!StringUtils.isEmpty(unicode)) {
			StringBuilder out = new StringBuilder();
			for (int i = 0; i < unicode.length(); i++) {
				out.append(unicode.charAt(i));
			}
			return out.toString();
		} else {
			return unicode;
		}
	}

	/**
	 * 
	 * 中文字符转小写拼音，其他字符不变。
	 * 
	 * @param str
	 *            要转换的字符串 是否大写。
	 * 
	 * @return 返回转换后的新字符串。
	 * 
	 */
	public static String toPinYin(String str) {
		return toPinYin(str, false, false, false);
	}

	/**
	 * 
	 * 中文字符转首字母大写拼音，其他字符不变。
	 * 
	 * @param str
	 *            要转换的字符串 是否大写。
	 * 
	 * @return 返回转换后的新字符串。
	 * 
	 */
	public static String toPinYinOfFirstUpperCase(String str) {
		return toPinYin(str, false, false, true);
	}

	/**
	 * 
	 * 中文字符转大写拼音，其他字符不变。
	 * 
	 * @param str
	 *            要转换的字符串 是否大写。
	 * 
	 * @return 返回转换后的新字符串。
	 * 
	 */
	public static String toPinYinOfUpperCase(String str) {
		return toPinYin(str, false, false, false);
	}

	/**
	 * 
	 * 中文字符转拼音首字母，其他字符不变。
	 * 
	 * @param str
	 *            要转换的字符串。
	 * @param upperCase
	 *            是否大写。
	 * 
	 * @return 返回转换后的新字符串。
	 * 
	 */
	public static String toPinYinOnlyFirst(String str, boolean upperCase) {
		return toPinYin(str, true, upperCase, false);
	}

	/**
	 * 
	 * 中文字符转拼音或首字母，其他字符不变。
	 * 
	 * @param str
	 *            要转换的字符串。
	 * @param onlyFirst
	 *            是否只取首字母。
	 * @param upperCase
	 *            是否大写。
	 * @param firstUpperCase
	 *            仅用于控制首字母为大写（不用于控制为小写）。
	 * 
	 * @return 返回转换后的新字符串。
	 * 
	 */
	private static String toPinYin(String str, boolean onlyFirst, boolean upperCase, boolean firstUpperCase) {
		StringBuffer sb = new StringBuffer();
		char c[] = str.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		if (upperCase) {
			defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		} else {
			defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		}
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < c.length; i++) {
			if (c[i] > 128) {
				try {
					if (onlyFirst) {
						sb.append(PinyinHelper.toHanyuPinyinStringArray(c[i], defaultFormat)[0].charAt(0));
						continue;
					}
					if (firstUpperCase) {
						sb.append(StringUtils
								.firstUpperCase(PinyinHelper.toHanyuPinyinStringArray(c[i], defaultFormat)[0]));
						continue;
					}
					sb.append(PinyinHelper.toHanyuPinyinStringArray(c[i], defaultFormat)[0]);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
				continue;
			}
			sb.append(c[i]);
		}
		return sb.toString();
	}

}
