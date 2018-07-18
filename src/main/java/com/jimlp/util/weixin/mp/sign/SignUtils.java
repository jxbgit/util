package com.jimlp.util.weixin.mp.sign;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.jimlp.util.StringUtils;

/**
 * 微信签名工具类 <br>
 * 创建时间 2018年7月2日上午11:54:39
 *
 * @author jxb
 *
 */
public class SignUtils {
    /**
     * 获取JS-SDK使用权限签名数据
     * 
     * @param jsApiTicket
     * @param appId
     * @param url
     *            JS-SDK接口调用所在网页的URL，不包含#及其后面部分。<br>
     *            （示例：http://mp.weixin.qq.com?params=value）
     * @return {<br>
     *         &emsp;&emsp;"appId":“APPID”,<br>
     *         &emsp;&emsp;"nonceStr":"随机字符串",<br>
     *         &emsp;&emsp;"timestamp":"时间戳",<br>
     *         &emsp;&emsp;"signature":"签名数据"<br>
     *         }
     * 
     * @throws Exception
     */
    public static Map<String, String> getJsApiSign(String jsApiTicket, String appId, String url) throws Exception {
        String nonceStr = get32NonceStr();
        String timestamp = getTimestamp();

        // 签名生成规则如下：参与签名的字段包括noncestr（随机字符串）, 有效的jsapi_ticket, timestamp（时间戳）,
        // url（当前网页的URL，不包含#及其后面部分） 。
        // 对所有待签名参数按照字段名的ASCII码从小到大排序（字典序）后，
        // 使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串string1。
        // 这里需要注意的是所有参数名均为小写字符。对string1作sha1加密，字段名和字段值都采用原始值，不进行URL转义。
        // -->这里手动排序了。
        String string1 = "jsapi_ticket=" + jsApiTicket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url;

        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.reset();
        crypt.update(string1.getBytes("UTF-8"));
        String signature = StringUtils.toHexString(crypt.digest());

        Map<String, String> ret = new HashMap<String, String>();
        ret.put("appId", appId);
        ret.put("nonceStr", nonceStr);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);
        return ret;
    }

    /**
     * 获取一个32位的随机字符串。
     *
     * @return String 随机字符串
     */
    private static String get32NonceStr() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }

    /**
     * 获取当前时间戳，单位秒。
     * 
     * @return
     */
    private static String getTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
}
