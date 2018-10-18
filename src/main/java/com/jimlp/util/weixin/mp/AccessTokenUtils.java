package com.jimlp.util.weixin.mp;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.jimlp.util.time.SimpleDateFormatUtils;
import com.jimlp.util.web.http.HttpUtils;

/**
 *
 * <br>
 * 创建时间 2018年7月2日上午11:58:42
 *
 * @author jxb
 *
 */
public class AccessTokenUtils {
    private static String appid;
    private static String secret;
    private static int expiresIn = 60;
    private static JSONObject accessToken;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private static final Runnable r = new Runnable() {
        @Override
        public void run() {
            getAccessTokenAndReconfigureAutoRun();
            startAutoRun();
            Thread.currentThread().stop();
        }
    };

    private AccessTokenUtils() {
        super();
    }

    private static void init(String _appid, String _secret) {
        appid = _appid;
        secret = _secret;
    }

    private static void startAutoRun() {
        scheduler.scheduleAtFixedRate(r, expiresIn - 600, expiresIn - 600, TimeUnit.SECONDS);
    }

    /**
     * 获取普通access_token。<br>
     * 注意：appid 和 secret参数只会在系统启动后第一次调用此方法时有效，<br>
     * &emsp;&emsp;&emsp;若更改此参数，请调用 getAccessToken(String, String, boolean)
     * 
     * @param appid
     * @param secret
     * @return 正常情况：{"access_token":"ACCESS_TOKEN","expires_in":7200}<br>
     *         错误情况：{"errcode":40013,"errmsg":"invalid appid"}
     */
    public static JSONObject getAccessToken(String appid, String secret) {
        if (accessToken == null) {
            synchronized (scheduler) {
                if (accessToken == null) {
                    init(appid, secret);
                    getAccessTokenAndReconfigureAutoRun();
                    startAutoRun();
                }
            }
        }
        return accessToken;
    }

    /**
     * 获取普通access_token。注意：避免频繁调用，否则达到调用上限后将限制调用（具体参见微信公众平台）。
     * 
     * @param appid
     * @param secret
     * @param refresh
     *            更新appid、secret到自动刷新程序，并立即刷新一次access_token。
     * @return 正常情况：{"access_token":"ACCESS_TOKEN","expires_in":7200}<br>
     *         错误情况：{"errcode":40013,"errmsg":"invalid appid"}
     */
    public static JSONObject getAccessToken(String appid, String secret, boolean refresh) {
        if (refresh) {
            init(appid, secret);
            getAccessTokenFromWeiXin(appid, secret);
        } else {
            getAccessToken(appid, secret);
        }
        return accessToken;
    }

    private static void getAccessTokenAndReconfigureAutoRun() {
        getAccessTokenFromWeiXin(appid, secret);
        int _expiresIn = accessToken.getIntValue("expires_in");
        if (_expiresIn != expiresIn && _expiresIn > 0) {
            expiresIn = _expiresIn;
        }
    }

    private static void getAccessTokenFromWeiXin(String appid, String secret) {
        synchronized (scheduler) {
            try {
                String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid + "&secret=" + secret;
                // 正常情况下，微信会返回下述JSON数据包给公众号：
                // ----{"access_token":"ACCESS_TOKEN","expires_in":7200}
                // expires_in：凭证有效时间，单位：秒
                // 错误时微信会返回错误码等信息，JSON数据包示例如下（该示例为AppID无效错误）:
                // ----{"errcode":40013,"errmsg":"invalid appid"}
                String r = HttpUtils.doGet(url);
                accessToken = JSONObject.parseObject(r);
            } catch (Exception e) {
                accessToken = new JSONObject();
                accessToken.put("errcode", "-1");
                accessToken.put("errmsg", "WeiXin API request failed:[" + e.getMessage() + "]");
            }
            accessToken.put("generate_at", SimpleDateFormatUtils.format(new Date()));
        }
    }
}
