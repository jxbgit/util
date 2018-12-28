package util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jimlp.pay.weixin.sdk.WXPay;
import com.jimlp.pay.weixin.sdk.WXPayConfig;
import com.jimlp.pay.weixin.sdk.WXPayUtil;
import com.jimlp.util.file.PropertiesUtils;
import com.jimlp.util.time.SimpleDateFormatUtils;
import com.jimlp.util.web.http.HttpUtils;


public class Main {
    public static WXPayConfig configTest = new WXPayConfigTestImpl();
    public static WXPayConfig configRelease = new WXPayConfigImpl();
    public static WXPayConfig config = configTest;
    public static void main(String[] args) throws Exception {
        String ignore = "[{\"dirName\":\"add\",\"fileNameLike\":[]},{\"dirName\":\"addd\",\"fileNameLike\":[\"wqed\",\"qdwe\"]}]";
        if (ignore != null) {
            try {
                JSONArray ja = JSONArray.parseArray(ignore);
                for (Object jo : ja) {
                    String dirName = ((JSONObject) jo).getString("dirName");
                    System.out.println(dirName);
                    JSONArray fileNameLike = ((JSONObject) jo).getJSONArray("fileNameLike");
                    System.out.println(Arrays.toString(fileNameLike.toArray(new String[0])));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // wxpay();
        // orderquery();
        // wxpay();
        // closeorder();
        // wxpay();
        // downloadBill();
        // prop();
        concurrency();
    }

    @SuppressWarnings("unused")
    private static void wxpay() throws Exception, IOException, UnsupportedEncodingException {
        Map<String, String> reqData = new HashMap<>();
        reqData.put("body", "TRT-test1");
        reqData.put("out_trade_no", "12314");
        // reqData.put("out_trade_no", "123123123");// 2018-07-11 13:00:00
        reqData.put("total_fee", "1");
        reqData.put("spbill_create_ip", "60.25.179.113");
        reqData.put("notify_url", "http://www.jimlp.com/t");
        reqData.put("trade_type", "NATIVE");
        reqData.put("attach", "attach");
        // reqData.put("time_start", SimpleDateFormatUtils.format(new Date(),
        // "yyyyMMddHHmmss"));
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, 3);
        reqData.put("time_expire", SimpleDateFormatUtils.format(c.getTime(), "yyyyMMddHHmmss"));
        // reqData.put("openid", openId);
        System.out.println(WXPayUtil.mapToXml(WXPay.instance(config).unifiedOrder(reqData)));
    }

    @SuppressWarnings("unused")
    private static void orderquery() throws Exception, IOException, UnsupportedEncodingException {
        Map<String, String> reqData = new HashMap<>();
        // reqData.put("transaction_id", "4200000169201808088278740199");
        reqData.put("out_trade_no", "555666775619254");
        System.out.println(WXPayUtil.mapToXml(WXPay.instance(config).orderQuery(reqData)));
    }

    @SuppressWarnings("unused")
    private static void closeorder() throws Exception, IOException, UnsupportedEncodingException {
        Map<String, String> reqData = new HashMap<>();
        reqData.put("out_trade_no", "1238");
        System.out.println(WXPayUtil.mapToXml(WXPay.instance(config).closeOrder(reqData)));
    }

    @SuppressWarnings("unused")
    private static void downloadBill() throws Exception, IOException, UnsupportedEncodingException {
        Map<String, String> reqData = new HashMap<>();
        reqData.put("bill_date", "20180808");
        reqData.put("bill_type", "ALL");
        System.out.println(WXPayUtil.mapToXml(WXPay.instance(config).downloadBill(reqData)));
        System.out.println(WXPayUtil.mapToXml(reqData));
    }

    @SuppressWarnings("unused")
    private static void prop() throws Exception {
        File file = new File("C:\\Java\\workspace\\util\\src\\test\\resources\\config.properties");
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("#info", "ads");
        params.put("info", null);
        params.put("#info3", "按段");
        params.put("info3", "dasasdd");
        PropertiesUtils.setValue(file, params, true);
    }

    static AtomicInteger ai = new AtomicInteger(0);

    @SuppressWarnings("unused")
    private static void concurrency() throws Exception {
        int cCount = 100;
        int loopCount = 10;
        final CountDownLatch latch = new CountDownLatch(cCount);
        Thread[] ts = new Thread[cCount];
        while (--cCount >= 0) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int j = 0; j < loopCount; j++) {
                            HttpUtils.doGet("http://java.appserver.com/Data/GetContent.hpo?password=1234567&system=0&captcha=4051&pageIndex=0&ip=218.68.147.59&USER_ID=0&pageSize=20&trigger=1DE0C2F5E4206484F248E69D5B28B86CCE0549C2D5FCECB7932D417DA3198CB09609591AE0DF0ACDCFDFF8AD2B9ACB600BC21F66ED8934D13ED6FB18B356B47BD3850A18A4C8B5608421D8ADF7D9F735DE40C062CDA2979C354397BA636F1E1BA5DC8D6FB5BA0AE5E137C16EA429F60C2556779B9C554012&oid=-999&lang=2&username=admin_aj");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                }
            });
            ts[cCount] = t;
        }
        long now = System.currentTimeMillis();
        for (int j = 0, l = ts.length; j < l; j++) {
            ts[j].start();
        }
        try {
            latch.await();
            System.out.println((System.currentTimeMillis() - now) / 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
