package com.jimlp.util.weixin.mp.web;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.jimlp.util.web.http.HttpUtils;
import com.jimlp.util.weixin.Constant;
import com.jimlp.util.weixin.mp.AccessTokenUtils;

/**
 *
 * <br>
 * 创建时间 2018年7月2日下午2:28:28
 *
 * @author jxb
 *
 */
public class TicketUtils {
    private static final Object LOCK = new Object();
    private static JSONObject jsApiTicket = new JSONObject();
    private static long jsApiTicketExpiresAt = 0L;

    /**
     * 获取jsapi_ticket。<br>
     * jsapi_ticket是公众号用于调用微信JS接口的临时票据。
     * 
     * @param accessToken
     * @return {<br>
     *         &emsp;&emsp;"errcode":0,<br>
     *         &emsp;&emsp;"errmsg":"ok",<br>
     *         &emsp;&emsp;"ticket":"bxLdikRXVbTPKd8-41ZO3MhKhFKA...",<br>
     *         &emsp;&emsp;"expires_in":7200<br>
     *         }
     * @throws IOException
     */
    public static JSONObject getJsApiTicket(String accessToken) throws IOException {
        long now = System.currentTimeMillis();
        if (jsApiTicketExpiresAt < now) {
            synchronized (LOCK) {
                if (jsApiTicketExpiresAt < now) {
                    String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi&access_token=" + accessToken;
                    // 正常情况下，微信会返回下述JSON数据包给公众号：
                    // ----{
                    // --------"errcode":0,
                    // --------"errmsg":"ok",
                    // --------"ticket":"bxLdikRXVbTPdHSM05e5u5sUoXNKd8-41ZO3MhKoyN5OfkWITDGgnr2fwJ0m9E8NYzWKVZvdVtaUgWvsdshFKA",
                    // --------"expires_in":7200
                    // ----}
                    String r = HttpUtils.doGet(url);
                    jsApiTicket = JSONObject.parseObject(r);
                    Object _jsApiTicket = jsApiTicket.get("ticket");
                    if (_jsApiTicket != null) {
                        jsApiTicketExpiresAt = now + jsApiTicket.getLongValue("expires_in") * 1000 - Constant.FIVE_MINUTE_OF_MILLISECOND;
                    }
                }
            }
        }
        return jsApiTicket;
    }

    /**
     * 获取jsapi_ticket。<br>
     * jsapi_ticket是公众号用于调用微信JS接口的临时票据。
     * 
     * @param appid
     * @param secret
     * @return {<br>
     *         &emsp;&emsp;"errcode":0,<br>
     *         &emsp;&emsp;"errmsg":"ok",<br>
     *         &emsp;&emsp;"ticket":"bxLdikRXVbTPKd8-41ZO3MhKhFKA...",<br>
     *         &emsp;&emsp;"expires_in":7200<br>
     *         }
     * @throws IOException
     */
    public static JSONObject getJsApiTicket(String appid, String secret) throws IOException {
        JSONObject accessToken = AccessTokenUtils.getAccessToken(appid, secret);
        return getJsApiTicket(accessToken.getString("access_token"));
    }
}
