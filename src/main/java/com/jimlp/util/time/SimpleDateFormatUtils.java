package com.jimlp.util.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 时间操作工具类
 * 
 * <br>
 * 创建时间 2018年5月24日下午2:14:41
 *
 * @author jxb
 *
 */
public class SimpleDateFormatUtils {
    /** 锁对象 */
    private static final Object LOCK = new Object();

    /** 默认模板 */
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 存放不同的日期模板格式的sdf的Map<br>
     */
    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

    /**
     * 私有构造器
     */
    private SimpleDateFormatUtils() {
    }

    /**
     * 返回一个线程安全的 {@link SimpleDateFormat} 实例 。
     * 
     * @param pattern
     * @return
     */
    private static SimpleDateFormat instanceSimpleDateFormat(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);
        if (tl == null) {
            synchronized (LOCK) {
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    sdfMap.put(pattern, tl);
                }
            }
        }
        return tl.get();
    }

    /**
     * 将日期字符串转换为 {@link Date}。
     * 
     * @param dateStr
     *            时间字符串（必须为 yyyy-MM-dd HH:mm:ss 时间格式）
     * @return
     */
    public static Date parse(String dateStr) {
        return parse(dateStr, DATE_TIME_FORMAT);
    }

    /**
     * 将日期字符串转换为 {@link Date}。
     * 
     * @param dateStr
     *            时间字符串
     * @param pattern
     *            时间字符串的模板
     * @return
     */
    public static Date parse(String dateStr, String pattern) {
        SimpleDateFormat format = instanceSimpleDateFormat(pattern);
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将日期格式化为 yyyy-MM-dd HH:mm:ss 格式的字符串。
     * 
     * @param date
     * @return
     */
    public static String format(Date date) {
        return format(date, DATE_TIME_FORMAT);
    }

    /**
     * 对日期进行字符串格式化，采用指定的模板格式。
     * 
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        SimpleDateFormat format = instanceSimpleDateFormat(pattern);
        return format.format(date);
    }
}
