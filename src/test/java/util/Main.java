package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.dom4j.DocumentException;

import com.alibaba.fastjson.JSON;
import com.jimlp.pay.weixin.sdk.WXPay;
import com.jimlp.pay.weixin.sdk.WXPayUtil;
import com.jimlp.util.ProjectUtils;
import com.jimlp.util.StringUtils;
import com.jimlp.util.file.PropertiesUtils;
import com.jimlp.util.xml.XmlUtils;

public class Main {
    public static void main(String[] args) throws Exception {
//         wxpay();
        // 查询();
//         多线程();
        // xml();
         prop();
        // byteToHex();
    }

    private static void wxpay() throws Exception, IOException, UnsupportedEncodingException {
        Map<String, String> reqData = new HashMap<>();
        reqData.put("body", "TRT-test1");
//        reqData.put("out_trade_no", "1236");
        reqData.put("out_trade_no", "123123123");// 2018-07-11 13:00:00
        reqData.put("total_fee", "1");
        reqData.put("spbill_create_ip", "60.25.179.113");
        reqData.put("notify_url", "http://www.jimlp.com/t");
        reqData.put("trade_type", "NATIVE");
        reqData.put("attach", "attach");
        reqData.put("time_start", "20180724101700");
        reqData.put("time_expire", "20180724105500");
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
        File file = new File("C:\\Java\\workspace\\util\\src\\test\\resources\\config.properties");
//        Properties p = new Properties();
//        p.load(new FileInputStream(file));
//        System.out.println(p);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("#info", "ads");
        params.put("info", null);
        params.put("#info3", "da78");
        params.put("info3", "dasasdd");
        PropertiesUtils.setValue(file, params, true);
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

    static volatile int tempInt = 0;
    static AtomicInteger ai = new AtomicInteger(0);
    private static void 多线程() throws Exception {
        Map<String, String> map = new HashMap<>();
         final Map<String, Long> PAYING_ORDER_TEMP = new Hashtable<>();
         final Map<String, String> OPENID_USERID = new Hashtable<>();
        int i = 10;
        Thread[] ts = new Thread[i];
        while (--i >= 0) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        long now = System.currentTimeMillis();
                        for(int i = 0 ;i<1000;i++){
                            //new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-07-11 12:55:55");
                            //SimpleDateFormatUtils.parse("2018-07-11 12:55:55");
//                            WXPayUtil.generateNonceStr();
//                            map.put(Thread.currentThread().getName()+i, "a");
//                            tempInt++;
//                            ai.getAndAdd(1);
                            PAYING_ORDER_TEMP.put(Thread.currentThread().getName()+i, 1L);
                            OPENID_USERID.put(Thread.currentThread().getName()+i, "123a,"+now);
                        }
                        System.out.println(PAYING_ORDER_TEMP.size());
//                        System.out.println(map.size());
//                        System.out.println(System.currentTimeMillis()-now);
                        System.out.println(tempInt);
                        System.out.println(ai.get());
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                long endTime = System.currentTimeMillis()-4*60*60*1000;
                Entry<String, Long>[] arr = new Entry[0];
                arr = PAYING_ORDER_TEMP.entrySet().toArray(arr);
                long createTime = 0;
                for (Entry<String, Long> entry : arr){
                    createTime = entry.getValue();
                    if(createTime<endTime){
                        PAYING_ORDER_TEMP.remove(entry.getKey());
                    }
                }
                
                Entry<String, String>[] arr2 = new Entry[0];
                arr2 = OPENID_USERID.entrySet().toArray(arr2);
                for (Entry<String, String> entry : arr2){
                    createTime = Long.parseLong(entry.getValue().split(",")[1]);
                    if(createTime<endTime){
                        OPENID_USERID.remove(entry.getKey());
                    }
                }
            }
        }).start();
    }
}