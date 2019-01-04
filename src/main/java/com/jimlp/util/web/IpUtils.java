package com.jimlp.util.web;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jimlp.util.web.http.HttpUtils;
import com.jimlp.util.web.pojo.IpInfo;

/**
 * <p>
 * JavaEE项目中，获取客户端信息的工具类
 * 
 * @author JIML
 *
 */
public final class IpUtils {

    private IpUtils() {
    }

    /**
     * <p>
     * 获取客户端 IPv4 地址，若获取失败则返回空字符串
     *
     * @param req
     * 
     * @return
     */
    public static String getIp(HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() != 0 || "unKnown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getRemoteAddr();
        }
        if (ip != null && ip.indexOf(",") > 0) {
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            ip = ip.substring(0, ip.indexOf(","));
        }
        if (ip == null || ip.length() == 0 || "0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
            ip = getLocalHostAddress();
        }
        return ip;
    }

    /**
     * 返回本机网卡 IPv4 地址
     * 
     * @return 返回本机网卡ip，若获取失败则返回空字符串
     */
    public static String getLocalHostAddress() {
        String ip = "";
        try {
            // 根据网卡取本机配置的IP
            InetAddress inet = InetAddress.getLocalHost();
            ip = inet.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip;
    }

    /**
     * <p>
     * 获取客户端 ip 的详细信息。
     * 
     * @param req
     * 
     * @return IpInfo IPv4信息<br>
     */
    public static IpInfo getIpInfo(HttpServletRequest req) {
        return getIpInfo(getIp(req));
    }

    /**
     * <p>
     * 解析指定 IPv4 的详细信息。
     * 
     * @param ip
     *            指定需要解析的IPv4地址
     * 
     * @return IpInfo IPv4信息<br>
     */
    public static IpInfo getIpInfo(String ip) {
        if (!isValidIP(ip)) {
            throw new IllegalArgumentException("Not a valid ip:" + ip);
        }
        String url = "http://whois.pconline.com.cn/ipJson.jsp";
        String encoding = "GBK";

        JSONObject jo = null;
        try {
            // {"ip":"125.37.234.156",
            // "pro":"天津市","proCode":"120000","city":"天津市","cityCode":"120000","region":"南开区","regionCode":"120104",
            // "addr":"天津市南开区 联通",
            // "regionNames":"","err":""}
            Map<String, String> params = new HashMap<>();
            params.put("json", "true");
            params.put("ip", ip);
            jo = JSON.parseObject(HttpUtils.doGet(url, params, encoding));
        } catch (IOException e) {
            // 内部维护一般不会出现异常，也没必要向外抛出。
            e.printStackTrace();
        }

        String province = jo.getString("pro");
        String ispWithAddr = jo.getString("addr");
        IpInfo info = new IpInfo();
        info.setIp(ip);
        if (province.length() < 1) {
            String country = ispWithAddr.substring(1);
            info.setCountry(country);
            return info;
        } else {
            String country = "中国";
            info.setCountry(country);
        }
        info.setProvince(province);
        String city = jo.getString("city");
        info.setCity(city);
        String county = jo.getString("region");
        info.setCounty(county);
        String isp = ispWithAddr.substring(ispWithAddr.lastIndexOf(" ") + 1);
        info.setIsp(isp);
        return info;
    }

    /**
     * 简单判断是否是有效 IPv4 地址
     * 
     * @param ip
     * @return
     */
    public static boolean isValidIP(String ip) {
        if (ip == null || "".equals(ip.trim())) {
            return false;
        }

        String[] parts = ip.split("\\.");
        int l = 4;
        if (parts.length != l) {
            return false;
        }

        for (String part : parts) {
            try {
                int intVal = Integer.parseInt(part);
                if (intVal < 0 || intVal > 255) {
                    return false;
                }
            } catch (NumberFormatException nfe) {
                return false;
            }
        }

        return true;
    }

    /**
     * 随机生成一个有效 IPv4 地址
     * 
     * @return
     */
    public static String getRandomIp() {
        // ip范围
        int[][] range = {
                // 36.56.0.0-36.63.255.255
                { 607649792, 608174079 },
                // 61.232.0.0-61.237.255.255
                { 1038614528, 1039007743 },
                // 106.80.0.0-106.95.255.255
                { 1783627776, 1784676351 },
                // 121.76.0.0-121.77.255.255
                { 2035023872, 2035154943 },
                // 123.232.0.0-123.235.255.255
                { 2078801920, 2079064063 },
                // 139.196.0.0-139.215.255.255
                { -1950089216, -1948778497 },
                // 171.8.0.0-171.15.255.255
                { -1425539072, -1425014785 },
                // 182.80.0.0-182.92.255.255
                { -1236271104, -1235419137 },
                // 210.25.0.0-210.47.255.255
                { -770113536, -768606209 },
                // 222.16.0.0-222.95.255.255
                { -569376768, -564133889 } };

        Random r = new Random();
        int i = r.nextInt(10);
        int ipInt = range[i][0];
        int add = range[i][1] - range[i][0];
        ipInt += r.nextInt(add);
        String ip = parseStrIp(ipInt);
        return ip;
    }

    /**
     * IPv4地址转成正整型 long<br>
     * 将IPv4地址转化成整数的方法如下： <br>
     * 1、通过String的split方法按.分隔得到4个长度的数组<br>
     * 2、通过左移位操作（<<）给每一段的数字加权，第一段的权为2的24次方，第二段的权为2的16次方，第三段的权为2的8次方，最后一段的权为1
     * 
     * @param ip
     * @return
     */
    public static long parseLongIp(String ip) {
        String[] parts = ip.split("\\.");
        long l = 0;
        l += Long.parseLong(parts[0]) << 24;
        l += Long.parseLong(parts[1]) << 16;
        l += Long.parseLong(parts[2]) << 8;
        l += Long.parseLong(parts[3]);
        return l;
    }

    /**
     * IPv4地址转成 int（含负数）<br>
     * 将IPv4地址转化成整数的方法如下： <br>
     * 1、通过String的split方法按.分隔得到4个长度的数组<br>
     * 2、通过左移位操作（<<）给每一段的数字加权，第一段的权为2的24次方，第二段的权为2的16次方，第三段的权为2的8次方，最后一段的权为1
     * 
     * @param ip
     * @return
     */
    public static int parseIntIp(String ip) {
        String[] parts = ip.split("\\.");
        int i = 0;
        i += Integer.parseInt(parts[0]) << 24;
        i += Integer.parseInt(parts[1]) << 16;
        i += Integer.parseInt(parts[2]) << 8;
        i += Integer.parseInt(parts[3]);
        return i;
    }

    /**
     * 尝试将数字转成 IPv4 地址，若此数字超出 IPv4 有效范围，则输出的 IPv4 将不准确的。
     * 
     * @param num
     * @return
     */
    public static String parseStrIp(long num) {
        int[] b = new int[4];
        b[0] = (int) ((num >> 24) & 0xff);
        b[1] = (int) ((num >> 16) & 0xff);
        b[2] = (int) ((num >> 8) & 0xff);
        b[3] = (int) (num & 0xff);

        StringBuilder ip = new StringBuilder();
        ip.append(Integer.toString(b[0])).append(".");
        ip.append(Integer.toString(b[1])).append(".");
        ip.append(Integer.toString(b[2])).append(".");
        ip.append(Integer.toString(b[3]));
        return ip.toString();
    }
}
