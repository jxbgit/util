package util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jimlp.pay.weixin.sdk.WXPay;
import com.jimlp.pay.weixin.sdk.WXPayConfig;
import com.jimlp.pay.weixin.sdk.WXPayUtil;
import com.jimlp.util.ChineseUtils;
import com.jimlp.util.file.PropertiesUtils;
import com.jimlp.util.time.SimpleDateFormatUtils;
import com.jimlp.util.web.http.HttpUtils;
import com.jimlp.util.xml.XmlUtils;


public class Main {
    public static WXPayConfig configTest = new WXPayConfigTestImpl();
    public static WXPayConfig configRelease = new WXPayConfigImpl();
    public static WXPayConfig config = configTest;

    public static void main(String[] args) throws Exception {
        Map<String, Object> reqData = new HashMap<>();
        Map<String, String> head = new HashMap<>();
        head.put("Version", "v1.0.3");
        head.put("MerCode", "207133");
        Map<String, String> body = new HashMap<>();
        body.put("ServerUrl", "http://www.ips.com/back.html");
        body.put("GoodsName", "测试GoodsName");
        Map<String, Object> GateWayReq = new HashMap<>();
        GateWayReq.put("head", head);
        GateWayReq.put("body", body);
        Map<String, Object> Ips = new HashMap<>();
        Ips.put("GateWayReq", GateWayReq);
        reqData.put("Ips", Ips);
        reqData = XmlUtils.XmlToMap(new File("C:/Users/LY/Desktop/workspace_jxb/WG-etonepay/src/main/resources/config/sys/spring-mvc.xml"));
        System.out.println(reqData);
        System.out.println(JSON.toJSONString(reqData));
        System.out.println(XmlUtils.mapToXml(reqData,"utf-8"));
        
        
        // wxpay();
        // orderquery();
        // wxpay();
        // closeorder();
        // wxpay();
        // downloadBill();
        // prop();
//        concurrency();
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
        final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(9);
        queue.put("13800138000");
        int i = queue.size();
        final CountDownLatch latch = new CountDownLatch(i);
        Thread[] ts = new Thread[i];
        while (--i >= 0) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Map<String, String> params = new HashMap<>();
                    params.put("name", "SMS");
                    params.put("target", queue.poll());
                    try {
                        for (int j = 0; j < 2; j++) {
                            params.put("msg", ""+Thread.currentThread().getId()+j);
                            String rst = HttpUtils.doGet("http://localhost/MessageQueue/putMsg", params, "UTF-8");
                            System.out.println(rst);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                }
            });
            ts[i] = t;
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
