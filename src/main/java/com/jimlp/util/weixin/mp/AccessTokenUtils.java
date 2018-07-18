package com.jimlp.util.weixin.mp;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.jimlp.util.web.http.HttpUtils;
import com.jimlp.util.weixin.Constant;

/**
 *
 * <br>
 * 创建时间 2018年7月2日上午11:58:42
 *
 * @author jxb
 *
 */
public class AccessTokenUtils {
    private static final Object LOCK = new Object();
    private static JSONObject accessToken = new JSONObject();
    private static long accessTokenExpiresAt = 0L;

    /**
     * 获取普通access_token
     * 
     * @param appid
     * @param secret
     * @return 正常情况：{"access_token":"ACCESS_TOKEN","expires_in":7200}<br>
     *         错误情况：{"errcode":40013,"errmsg":"invalid appid"}
     * @throws IOException
     */
    public static JSONObject getAccessToken(String appid, String secret) throws IOException {
        long now = System.currentTimeMillis();
        if (accessTokenExpiresAt < now) {
            synchronized (LOCK) {
                if (accessTokenExpiresAt < now) {
                    String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid + "&secret=" + secret;
                    // 正常情况下，微信会返回下述JSON数据包给公众号：
                    // ----{"access_token":"ACCESS_TOKEN","expires_in":7200}
                    // 错误时微信会返回错误码等信息，JSON数据包示例如下（该示例为AppID无效错误）:
                    // ----{"errcode":40013,"errmsg":"invalid appid"}
                    String r = HttpUtils.doGet(url);
                    accessToken = JSONObject.parseObject(r);
                    Object _accessToken = accessToken.get("access_token");
                    if (_accessToken != null) {
                        accessTokenExpiresAt = now + accessToken.getLongValue("expires_in") * 1000 - Constant.FIVE_MINUTE_OF_MILLISECOND;
                    }
                }
            }
        }
        return accessToken;
    }
}
