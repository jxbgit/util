package com.jimlp.pay.weixin.sdk;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class WXPay {
    private WXPayConfig config;
    private boolean useSandbox = false;
    private static final WXPay WXPAY = new WXPay();
    private static WXPay sandboxWxPay;

    private WXPay() {
    }

    /**
     * 获取微信支付接口实例
     * 
     * @return
     */
    public static WXPay instance(WXPayConfig config) {
        WXPAY.config = config;
        return WXPAY;
    }

    /**
     * 获取微信支付接口实例（沙箱模式）
     * 
     * @return
     */
    public static WXPay instanceSandbox(WXPayConfig config) {
        if (sandboxWxPay == null) {
            synchronized (WXPAY) {
                if (sandboxWxPay == null) {
                    sandboxWxPay = new WXPay();
                    sandboxWxPay.useSandbox = true;
                }
            }
        }
        sandboxWxPay.config = config;
        return sandboxWxPay;
    }

    /**
     * 不需要证书的请求
     * 
     * @param urlSuffix
     *            String
     * @param reqData
     *            向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public String requestWithoutCert(String urlSuffix, Map<String, String> reqData) throws Exception {
        String msgUUID = reqData.get("nonce_str");
        String reqBody = WXPayUtil.mapToXml(reqData);

        String resp = WXPayRequest.requestWithoutCert(urlSuffix, msgUUID, reqBody, config.getHttpConnectTimeoutMs(), config.getHttpReadTimeoutMs(), config);
        return resp;
    }

    /**
     * 需要证书的请求
     * 
     * @param urlSuffix
     *            String
     * @param reqData
     *            向wxpay post的请求数据 Map
     * @return API返回数据
     * @throws Exception
     */
    public String requestWithCert(String urlSuffix, Map<String, String> reqData) throws Exception {
        String msgUUID = reqData.get("nonce_str");
        String reqBody = WXPayUtil.mapToXml(reqData);

        String resp = WXPayRequest.requestWithCert(urlSuffix, msgUUID, reqBody, config.getHttpConnectTimeoutMs(), config.getHttpReadTimeoutMs(), config);
        return resp;
    }

    /**
     * 处理 HTTPS API返回数据，转换成Map对象。return_code为SUCCESS时，验证签名。
     * 
     * @param xmlStr
     *            API返回的XML格式数据
     * @return Map类型数据
     * @throws Exception
     */
    public Map<String, String> processResponseXml(String xmlStr) throws Exception {
        String RETURN_CODE = "return_code";
        String return_code;
        Map<String, String> respData = WXPayUtil.xmlToMap(xmlStr);
        if (respData.containsKey(RETURN_CODE)) {
            return_code = respData.get(RETURN_CODE);
        } else {
            throw new Exception(String.format("No `return_code` in XML: %s", xmlStr));
        }

        if (return_code.equals(WXPayConstants.FAIL)) {
            return respData;
        } else if (return_code.equals(WXPayConstants.SUCCESS)) {
            if (WXPayUtil.isSignatureValid(respData, config)) {
                return respData;
            } else {
                throw new Exception(String.format("Invalid sign value in XML: %s", xmlStr));
            }
        } else {
            throw new Exception(String.format("return_code value %s is invalid in XML: %s", return_code, xmlStr));
        }
    }

    /**
     * 作用：提交刷卡支付<br>
     * 场景：刷卡支付
     * 
     * @param reqData
     *            向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> microPay(Map<String, String> reqData) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_MICROPAY_URL_SUFFIX;
        } else {
            url = WXPayConstants.MICROPAY_URL_SUFFIX;
        }
        String respXml = this.requestWithoutCert(url, WXPayUtil.fillRequestData(reqData, config));
        return this.processResponseXml(respXml);
    }

    /**
     * 提交刷卡支付，针对软POS，尽可能做成功 内置重试机制，最多60s
     * 
     * @param reqData
     * @param connectTimeoutMs
     * @return
     * @throws Exception
     */
    public Map<String, String> microPayWithPos(Map<String, String> reqData) throws Exception {
        int connectTimeoutMs = config.getHttpConnectTimeoutMs();
        int remainingTimeMs = 60 * 1000;
        long startTimestampMs = 0;
        Map<String, String> lastResult = null;
        Exception lastException = null;

        while (true) {
            startTimestampMs = System.currentTimeMillis();
            int readTimeoutMs = remainingTimeMs - connectTimeoutMs;
            if (readTimeoutMs > 1000) {
                try {
                    lastResult = this.microPay(reqData);
                    String returnCode = lastResult.get("return_code");
                    if (returnCode.equals("SUCCESS")) {
                        String resultCode = lastResult.get("result_code");
                        String errCode = lastResult.get("err_code");
                        if (resultCode.equals("SUCCESS")) {
                            break;
                        } else {
                            // 看错误码，若支付结果未知，则重试提交刷卡支付
                            if (errCode.equals("SYSTEMERROR") || errCode.equals("BANKERROR") || errCode.equals("USERPAYING")) {
                                remainingTimeMs = remainingTimeMs - (int) (System.currentTimeMillis() - startTimestampMs);
                                if (remainingTimeMs <= 100) {
                                    break;
                                } else {
                                    if (remainingTimeMs > 5 * 1000) {
                                        Thread.sleep(5 * 1000);
                                    } else {
                                        Thread.sleep(1 * 1000);
                                    }
                                    continue;
                                }
                            } else {
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                } catch (Exception ex) {
                    lastResult = null;
                    lastException = ex;
                }
            } else {
                break;
            }
        }

        if (lastResult == null) {
            throw lastException;
        } else {
            return lastResult;
        }
    }

    /**
     * 作用：统一下单<br>
     * 场景：公共号支付、扫码支付、APP支付
     * 
     * @param reqData
     *            向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> unifiedOrder(Map<String, String> reqData) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_UNIFIEDORDER_URL_SUFFIX;
        } else {
            url = WXPayConstants.UNIFIEDORDER_URL_SUFFIX;
        }
        String respXml = this.requestWithoutCert(url, WXPayUtil.fillRequestData(reqData, config));
        return this.processResponseXml(respXml);
    }

    /**
     * 作用：查询订单<br>
     * 场景：刷卡支付、公共号支付、扫码支付、APP支付
     * 
     * @param reqData
     *            向wxpay post的请求数据 int
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> orderQuery(Map<String, String> reqData) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_ORDERQUERY_URL_SUFFIX;
        } else {
            url = WXPayConstants.ORDERQUERY_URL_SUFFIX;
        }
        String respXml = this.requestWithoutCert(url, WXPayUtil.fillRequestData(reqData, config));
        return this.processResponseXml(respXml);
    }

    /**
     * 作用：撤销订单<br>
     * 场景：刷卡支付<br>
     * 其他：需要证书
     * 
     * @param reqData
     *            向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> reverse(Map<String, String> reqData) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_REVERSE_URL_SUFFIX;
        } else {
            url = WXPayConstants.REVERSE_URL_SUFFIX;
        }
        String respXml = this.requestWithCert(url, WXPayUtil.fillRequestData(reqData, config));
        return this.processResponseXml(respXml);
    }

    /**
     * 作用：关闭订单<br>
     * 场景：公共号支付、扫码支付、APP支付
     * 
     * @param reqData
     *            向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> closeOrder(Map<String, String> reqData) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_CLOSEORDER_URL_SUFFIX;
        } else {
            url = WXPayConstants.CLOSEORDER_URL_SUFFIX;
        }
        String respXml = this.requestWithoutCert(url, WXPayUtil.fillRequestData(reqData, config));
        return this.processResponseXml(respXml);
    }

    /**
     * 作用：申请退款<br>
     * 场景：刷卡支付、公共号支付、扫码支付、APP支付<br>
     * 其他：需要证书
     * 
     * @param reqData
     *            向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> refund(Map<String, String> reqData) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_REFUND_URL_SUFFIX;
        } else {
            url = WXPayConstants.REFUND_URL_SUFFIX;
        }
        String respXml = this.requestWithCert(url, WXPayUtil.fillRequestData(reqData, config));
        return this.processResponseXml(respXml);
    }

    /**
     * 作用：退款查询<br>
     * 场景：刷卡支付、公共号支付、扫码支付、APP支付
     * 
     * @param reqData
     *            向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> refundQuery(Map<String, String> reqData) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_REFUNDQUERY_URL_SUFFIX;
        } else {
            url = WXPayConstants.REFUNDQUERY_URL_SUFFIX;
        }
        String respXml = this.requestWithoutCert(url, WXPayUtil.fillRequestData(reqData, config));
        return this.processResponseXml(respXml);
    }

    /**
     * 作用：对账单下载<br>
     * 场景：刷卡支付、公共号支付、扫码支付、APP支付<br>
     * 
     * @param reqData
     *            向wxpay post的请求数据
     * @return 经过封装的API返回数据。返回字段包括return_code、return_msg，
     *         若return_code为SUCCESS时还包括data（对账单数据，数据格式详见微信官方文档）
     * @throws Exception
     */
    public Map<String, String> downloadBill(Map<String, String> reqData) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_DOWNLOADBILL_URL_SUFFIX;
        } else {
            url = WXPayConstants.DOWNLOADBILL_URL_SUFFIX;
        }
        String respStr = this.requestWithoutCert(url, WXPayUtil.fillRequestData(reqData, config)).trim();
        Map<String, String> ret;
        // 出现错误，返回XML数据
        if (respStr.indexOf("<") == 0) {
            ret = WXPayUtil.xmlToMap(respStr);
        } else {
            // 正常返回csv数据
            ret = new HashMap<String, String>();
            ret.put("return_code", WXPayConstants.SUCCESS);
            ret.put("return_msg", "OK");
            ret.put("data", respStr);
        }
        return ret;
    }

    /**
     * 作用：转换短链接<br>
     * 场景：刷卡支付、扫码支付
     * 
     * @param reqData
     *            向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> shortUrl(Map<String, String> reqData) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_SHORTURL_URL_SUFFIX;
        } else {
            url = WXPayConstants.SHORTURL_URL_SUFFIX;
        }
        String respXml = this.requestWithoutCert(url, WXPayUtil.fillRequestData(reqData, config));
        return this.processResponseXml(respXml);
    }

    /**
     * 作用：授权码查询OPENID接口<br>
     * 场景：刷卡支付
     * 
     * @param reqData
     *            向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> authCodeToOpenid(Map<String, String> reqData) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_AUTHCODETOOPENID_URL_SUFFIX;
        } else {
            url = WXPayConstants.AUTHCODETOOPENID_URL_SUFFIX;
        }
        String respXml = this.requestWithoutCert(url, WXPayUtil.fillRequestData(reqData, config));
        return this.processResponseXml(respXml);
    }

    /**
     * 将输入流转字符串
     * 
     * @param stream
     * @param charset
     * @return
     * @throws IOException
     */
    public static String inputStreamToString(InputStream stream, String charset) throws IOException {
        try {
            Reader reader = new InputStreamReader(stream, charset);
            StringBuilder response = new StringBuilder();

            final char[] buff = new char[1024];
            int read = 0;
            while ((read = reader.read(buff)) > 0) {
                response.append(buff, 0, read);
            }

            return response.toString();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
    
    /**
     * 微信支付结果通知检查
     * 
     * @param req
     * @return
     */
    public WXPayNotify payNotify(HttpServletRequest req) {
        WXPayNotify rst = new WXPayNotify();
        rst.setCode(0);
        String xml = null;
        Map<String, String> rawMap = new HashMap<>();
        try {
            InputStream inStream = req.getInputStream();
            xml = inputStreamToString(inStream, "UTF-8");
            rst.setRaw(xml);
            rawMap = WXPayUtil.xmlToMap(xml);
            rst.setRawMap(rawMap);
        } catch (Exception e) {
            rst.setCode(1);
            rst.setMsg("获取或解析支付结果数据失败：" + e.getMessage());
            return rst;
        }
        // 状态（通信标识，非交易标识）
        if (!"SUCCESS".equals(rawMap.get("return_code"))) {
            rst.setCode(2);
            rst.setMsg(rawMap.get("return_msg"));
            return rst;
        }
        // 业务结果
        if (!"SUCCESS".equals(rawMap.get("result_code"))) {
            rst.setCode(3);
            rst.setMsg(rawMap.get("err_code") + "：" + rawMap.get("err_code_des"));
            return rst;
        }
        // 检验签名
        try {
            if (!WXPayUtil.isSignatureValid(rawMap, config)) {
                rst.setCode(4);
                rst.setMsg("签名无效");
                return rst;
            }
        } catch (Exception e) {
            rst.setCode(5);
            rst.setMsg("签名校验失败：" + e.getMessage());
            return rst;
        }

        return rst;
    }

}