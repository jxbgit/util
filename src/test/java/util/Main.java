package util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

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
        // wxpay();
        // orderquery();
        // wxpay();
        // closeorder();
        // wxpay();
        // downloadBill();
        // prop();
        System.out.println(HttpUtils.doGet("http://localhost/MessageQueue/addQueue?msgType=MSM&capacity=2100000000"));
        concurrency();
    }

    @SuppressWarnings("unused")
    private static void wxpay() throws Exception, IOException, UnsupportedEncodingException {
        Map<String, String> reqData = new HashMap<>();
        reqData.put("body", "TRT-test1");
        reqData.put("out_trade_no", "12310");
        // reqData.put("out_trade_no", "123123123");// 2018-07-11 13:00:00
        reqData.put("total_fee", "1");
        reqData.put("spbill_create_ip", "60.25.179.113");
        reqData.put("notify_url", "http://www.jimlp.com/t");
        reqData.put("trade_type", "NATIVE");
        reqData.put("attach", "attach");
        // reqData.put("time_start", SimpleDateFormatUtils.format(new Date(),
        // "yyyyMMddHHmmss"));
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, 1);
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
        int i = 1000;
        final CountDownLatch latch = new CountDownLatch(i);
        Thread[] ts = new Thread[i];
        while (--i >= 0) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int j = 0; j < 10000; j++) {
                            String rst = HttpUtils.doGet("http://localhost/MessageQueue/putMsg?name=MSM&target=13800138000&msg=msg");
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
