package util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.dom4j.DocumentException;

import com.alibaba.fastjson.JSON;
import com.jimlp.pay.weixin.sdk.WXPay;
import com.jimlp.pay.weixin.sdk.WXPayUtil;
import com.jimlp.util.ProjectUtils;
import com.jimlp.util.StringUtils;
import com.jimlp.util.file.PropertiesUtils;
import com.jimlp.util.time.SimpleDateFormatUtils;
import com.jimlp.util.xml.XmlUtils;

public class Main {
    public static void main(String[] args) throws Exception {
        // wxpay();
        // 查询();
         多线程();
        // xml();
        // prop();
        // byteToHex();
    }

    private static void wxpay() throws Exception, IOException, UnsupportedEncodingException {
        Map<String, String> reqData = new HashMap<>();
        reqData.put("body", "TRT-test1");
        reqData.put("out_trade_no", "1235");
        //reqData.put("out_trade_no", "123123123"); 2018-07-11 13:00:00
        reqData.put("total_fee", "1");
        reqData.put("spbill_create_ip", "60.25.179.113");
        reqData.put("notify_url", "http://www.jimlp.com/t");
        reqData.put("trade_type", "NATIVE");
        reqData.put("attach", "attach");
        //reqData.put("openid", openId);
        System.out.println(WXPayUtil.mapToXml(WXPay.instance().unifiedOrder(reqData)));
        System.out.println(WXPayUtil.mapToXml(reqData));
    }
    
    private static void 查询() throws Exception, IOException, UnsupportedEncodingException {
        Map<String, String> reqData = new HashMap<>();
        reqData.put("out_trade_no", "123123123");
        System.out.println(WXPayUtil.mapToXml(WXPay.instance().orderQuery(reqData)));
        System.out.println(WXPayUtil.mapToXml(reqData));
    }

    private static void prop() throws Exception {
        File file = new File(ProjectUtils.getClassPath() + "config.properties");
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        PropertiesUtils.setValue(file, params);
    }

    private static void xml() throws DocumentException, UnsupportedEncodingException {
        System.out.println(XmlUtils.mapToSimpleXml(JSON.parseObject("", HashMap.class)));
    }

    private static void byteToHex() {
        final byte b[] = "<xml><return_code><![CDATA[SUCC啊ESS]]></return_code><return_msg><![CDATA[OK]]></return_msg><appid><![CDATA[wx2421<xml><return_code><![CDATA[SUCC啊ESS]]></return_code><return_msg><![CDATA[OK]]></return_msg><appid><![CDATA[wx2421b1c4370ec43b]]></appid><mch_id><![CDATA[10000100<xml><return_code><![CDATA[SUCC啊ESS]]></return_code><return_msg><![CDATA[OK]]></return_msg><appid><![CDATA[wx2421b1c4370ec43b]]></appid><mch_id><![CDATA[10000100<xml><return_code><![CDATA[SUCC啊ESS]]></return_code><return_msg><![CDATA[OK]]></return_msg><appid><![CDATA[wx2421b1c4370ec43b]]></appid><mch_id><![CDATA[10000100<xml><return_code><![CDATA[SUCC啊ESS]]></return_code><return_msg><![CDATA[OK]]></return_msg><appid><![CDATA[wx2421b1c4370ec43b]]></appid><mch_id><![CDATA[10000100<xml><return_code><![CDATA[SUCC啊ESS]]></return_code><return_msg><![CDATA[OK]]></return_msg><appid><![CDATA[wx2421b1c4370ec43b]]></appid><mch_id><![CDATA[10000100<xml><return_code><![CDATA[SUCC啊ESS]]></return_code><return_msg><![CDATA[OK]]></return_msg><appid><![CDATA[wx2421b1c4370ec43b]]></appid><mch_id><![CDATA[10000100<xml><return_code><![CDATA[SUCC啊ESS]]></return_code><return_msg><![CDATA[OK]]></return_msg><appid><![CDATA[wx2421b1c4370ec43b]]></appid><mch_id><![CDATA[10000100<xml><return_code><![CDATA[SUCC啊ESS]]></return_code><return_msg><![CDATA[OK]]></return_msg><appid><![CDATA[wx2421b1c4370ec43b]]></appid><mch_id><![CDATA[10000100<xml><return_code><![CDATA[SUCC啊ESS]]></return_code><return_msg><![CDATA[OK]]></return_msg><appid><![CDATA[wx2421b1c4370ec43b]]></appid><mch_id><![CDATA[10000100<xml><return_code><![CDATA[SUCC啊ESS]]></return_code><return_msg><![CDATA[OK]]></return_msg><appid><![CDATA[wx2421b1c4370ec43b]]></appid><mch_id><![CDATA[10000100<xml><return_code><![CDATA[SUCC啊ESS]]></return_code><return_msg><![CDATA[OK]]></return_msg><appid><![CDATA[wx2421b1c4370ec43b]]></appid><mch_id><![CDATA[10000100<xml><return_code><![CDATA[SUCC啊ESS]]></return_code><return_msg><![CDATA[OK]]></return_msg><appid><![CDATA[wx2421b1c4370ec43b]]></appid><mch_id><![CDATA[10000100b1c4370ec43b]]></appid><mch_id><![CDATA[10000100]]></mch_id><nonce_str><![CDATA[IITRi8Iabbblz1Jc]]></nonce_str><openid><![CDATA[oUpF8uMuAJO_M2pxb1Q9zNjWeS6o]]></openid><sign><![CDATA[7921E432F65EB8ED0CE9755F0E86D72F]]></sign><result_code><![CDATA[SUCCESS]]></result_code><prepay_id><![CDATA[wx201411101639507cbf6ffd8b0779950874]]></prepay_id><trade_type><![CDATA[JSAPI]]></trade_type></xml>".getBytes();
        long now = System.currentTimeMillis();
        StringUtils.toHexString(b);
        System.out.println(System.currentTimeMillis()-now);
    }

    private static void 多线程() throws Exception {
        int i = 10;
        Thread[] ts = new Thread[i];
        while (--i >= 0) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(SimpleDateFormatUtils.parse("2018-07-11 12:55:55"));
                        System.out.println(SimpleDateFormatUtils.parse("2018-07-11 12:55:55"));
                        System.out.println(SimpleDateFormatUtils.parse("2018-07-11 12:55:55"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            ts[i] = t;
        }
        for (int j = 0, l = ts.length; j < l; j++) {
            ts[j].start();
        }
    }
}