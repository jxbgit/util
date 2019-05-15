package com.jimlp.util;

import java.util.regex.Pattern;

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
	 // 用于构建新的字符串
        StringBuilder sb = new StringBuilder();
        // 从左向右扫描字符串。tmpStr是还没有被扫描的剩余字符串。
        // 下面有两个判断分支：
        // 1. 如果剩余字符串是Unicode字符开头，就把Unicode转换成汉字，加到StringBuilder中。然后跳过这个Unicode字符。
        // 2.反之， 如果剩余字符串不是Unicode字符开头，把普通字符加入StringBuilder，向右跳过1.
        int length = unicode.length();
        for (int i = 0; i < length;) {
            String tmpStr = unicode.substring(i);
            if (isStartWithUnicode(tmpStr)) { // 分支1
                sb.append(ustartToCn(tmpStr));
                i += 6;
            } else { // 分支2
                sb.append(unicode.substring(i, i + 1));
                i++;
            }
        }
        return sb.toString();
	}/**
     * 把 \\u 开头的单字转成汉字，如 \\u6B65 ->　步
     * @param str
     * @return
     */
    private static String ustartToCn(final String str) {
        StringBuilder sb = new StringBuilder().append("0x")
                .append(str.substring(2, 6));
        Integer codeInteger = Integer.decode(sb.toString());
        int code = codeInteger.intValue();
        char c = (char)code;
        return String.valueOf(c);
    }
	// 单个字符的正则表达式
    private static final String singlePattern = "[0-9|a-f|A-F]";
    // 4个字符的正则表达式
    private static final String pattern = singlePattern + singlePattern +
            singlePattern + singlePattern;
	/**
     * 字符串是否以Unicode字符开头。约定Unicode字符以 \\u开头。
     * @param str 字符串
     * @return true表示以Unicode字符开头.
     */
    private static boolean isStartWithUnicode(final String str) {
        if (null == str || str.length() == 0) {
            return false;
        }
        if (!str.startsWith("\\u")) {
            return false;
        }
        // \u6B65
        if (str.length() < 6) {
            return false;
        }
        String content = str.substring(2, 6);
        
        boolean isMatch = Pattern.matches(pattern, content);
        return isMatch;
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
