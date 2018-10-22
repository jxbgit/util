package com.jimlp.util.weixin.mp;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
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
    // 缓存appid
    private static String appid;
    // 缓存secret
    private static String secret;
    // 缓存accessToken
    private static JSONObject accessToken;
    // 当前accessToken有效时长，单位秒
    private static int expiresIn = 10;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2, new ThreadFactory() {
        private int index = 0;

        @Override
        public Thread newThread(Runnable r) {
            ThreadGroup tg = new ThreadGroup("AccessTokenFreshPool");
            Thread t = new Thread(tg, r, tg.getName() + "-Thread-" + (++index));
            return t;
        }
    });
    private static ScheduledFuture<?> sf = null;
    private static final Runnable r = new Runnable() {
        @Override
        public void run() {
            refreshAccessTokenAndReconfigureAutoRun();
            System.out.println("定时刷新：" + expiresIn + accessToken);
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
        if (sf != null) {
            sf.cancel(true);
        }
        sf = scheduler.scheduleAtFixedRate(r, expiresIn - expiresIn / 10, expiresIn - expiresIn / 10, TimeUnit.SECONDS);
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
        return getAccessToken(appid, secret, false);
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
        if (refresh || accessToken == null) {
            synchronized (scheduler) {
                if (refresh || accessToken == null) {
                    init(appid, secret);
                    refreshAccessTokenAndReconfigureAutoRun();
                }
            }
        }
        JSONObject jo = null;
        try {
            jo = (JSONObject) accessToken.clone();
        } catch (Exception e) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                jo = (JSONObject) accessToken.clone();
            }
            jo = (JSONObject) accessToken.clone();
        }
        return jo;
    }

    private static void refreshAccessTokenAndReconfigureAutoRun() {
        accessToken = getAccessTokenFromWeiXin(appid, secret);
        int _expiresIn = accessToken.getIntValue("expires_in");
        if (_expiresIn != expiresIn && _expiresIn > 0) {
            expiresIn = _expiresIn;
        }
        startAutoRun();
    }

    private static JSONObject getAccessTokenFromWeiXin(String appid, String secret) {
        JSONObject jo = null;
        try {
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid + "&secret=" + secret;
            // 正常情况下，微信会返回下述JSON数据包给公众号：
            // ----{"access_token":"ACCESS_TOKEN","expires_in":7200}
            // expires_in：凭证有效时间，单位：秒
            // 错误时微信会返回错误码等信息，JSON数据包示例如下（该示例为AppID无效错误）:
            // ----{"errcode":40013,"errmsg":"invalid appid"}
            String r = HttpUtils.doGet(url);
            jo = JSONObject.parseObject(r);
        } catch (Exception e) {
            jo = new JSONObject();
            jo.put("errcode", "-1");
            jo.put("errmsg", "WeiXin API request failed:[" + e.getMessage() + "]");
        }
        jo.put("generate_at", SimpleDateFormatUtils.format(new Date()));
        return jo;
    }
}
