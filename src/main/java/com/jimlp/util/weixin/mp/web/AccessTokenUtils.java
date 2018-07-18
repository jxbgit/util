package com.jimlp.util.weixin.mp.web;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
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

    /**
     * 获取网页授权access_token。<br>
     * <br>
     * 注意获取code时设置的应用授权作用域。<br>
     * snsapi_base：静默授权并自动跳转到回调页，只能得到用户openid；<br>
     * snsapi_userinfo：弹出授权页面，可通过openid获取用户基本信息。<br>
     * &emsp;&emsp;并且， 即使在未关注的情况下，只要用户授权，也能获取其信息 。<br>
     * 
     * @param appid
     * @param secret
     * @param code
     * @return 正常情况：{ "access_token":"ACCESS_TOKEN", "expires_in":7200,
     *         "refresh_token":"REFRESH_TOKEN", "openid":"OPENID",
     *         "scope":"SCOPE" }<br>
     *         错误情况：{"errcode":40029,"errmsg":"invalid code"}
     * @throws IOException
     */
    public static JSONObject getAccessToken(String appid, String secret, String code) throws IOException {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?grant_type=authorization_code&appid=" + appid + "&secret=" + secret + "&code=" + code;
        String result = HttpUtils.doGet(url);
        JSONObject jo = JSONObject.parseObject(result);
        return jo;
    }
}
