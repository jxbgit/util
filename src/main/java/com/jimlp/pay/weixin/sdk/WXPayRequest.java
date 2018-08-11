package com.jimlp.pay.weixin.sdk;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.SecureRandom;

public class WXPayRequest {
    private WXPayRequest() {
    }

    /**
     * 请求，只请求一次，不做重试
     * 
     * @param domain
     * @param urlSuffix
     * @param uuid
     * @param data
     * @param connectTimeoutMs
     * @param readTimeoutMs
     * @param useCert
     *            是否使用证书，针对退款、撤销等操作
     * @return
     * @throws Exception
     */
    private static String requestOnce(final String domain, String urlSuffix, String uuid, String data, int connectTimeoutMs, int readTimeoutMs, boolean useCert, WXPayConfig config) throws Exception {
        BasicHttpClientConnectionManager connManager;
        if (useCert) {
            // 证书
            char[] password = config.getMchID().toCharArray();
            InputStream certStream = config.getCertStream();
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(certStream, password);

            // 实例化密钥库 & 初始化密钥工厂
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, password);

            // 创建 SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, new SecureRandom());

            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1" }, null, new DefaultHostnameVerifier());

            connManager = new BasicHttpClientConnectionManager(RegistryBuilder.<ConnectionSocketFactory> create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslConnectionSocketFactory).build(), null, null, null);
        } else {
            connManager = new BasicHttpClientConnectionManager(RegistryBuilder.<ConnectionSocketFactory> create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", SSLConnectionSocketFactory.getSocketFactory()).build(), null, null, null);
        }

        HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connManager).build();

        String url = "https://" + domain + urlSuffix;
        HttpPost httpPost = new HttpPost(url);

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeoutMs).setConnectTimeout(connectTimeoutMs).build();
        httpPost.setConfig(requestConfig);

        StringEntity postEntity = new StringEntity(data, "UTF-8");
        httpPost.addHeader("Content-Type", "text/xml");
        httpPost.addHeader("User-Agent", WXPayConstants.USER_AGENT);
        httpPost.setEntity(postEntity);

        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity httpEntity = httpResponse.getEntity();
        return EntityUtils.toString(httpEntity, "UTF-8");
    }

    private static String request(String urlSuffix, String uuid, String data, int connectTimeoutMs, int readTimeoutMs, boolean useCert, WXPayConfig config) throws Exception {
        WXPayDomain.DomainInfo domainInfo = WXPayDomain.getDomain();
        if (domainInfo == null) {
            throw new Exception("WXPayConfig.getWXPayDomain().getDomain() is empty or null");
        }
        Exception exception = null;
        try {
            return requestOnce(domainInfo.domain, urlSuffix, uuid, data, connectTimeoutMs, readTimeoutMs, useCert, config);
        } catch (UnknownHostException ex) { // dns 解析错误，或域名不存在
            exception = ex;
        } catch (ConnectTimeoutException ex) {
            exception = ex;
        } catch (SocketTimeoutException ex) {
            exception = ex;
        } catch (Exception ex) {
            exception = ex;
        }
        WXPayDomain.report(domainInfo.domain, exception);
        throw exception;
    }

    /**
     * 可重试的，非双向认证的请求
     * 
     * @param urlSuffix
     * @param uuid
     * @param data
     * @return
     */
    public static String requestWithoutCert(String urlSuffix, String uuid, String data, WXPayConfig config) throws Exception {
        return request(urlSuffix, uuid, data, config.getHttpConnectTimeoutMs(), config.getHttpReadTimeoutMs(), false, config);
    }

    /**
     * 可重试的，非双向认证的请求
     * 
     * @param urlSuffix
     * @param uuid
     * @param data
     * @param connectTimeoutMs
     * @param readTimeoutMs
     * @return
     */
    public static String requestWithoutCert(String urlSuffix, String uuid, String data, int connectTimeoutMs, int readTimeoutMs, WXPayConfig config) throws Exception {
        return request(urlSuffix, uuid, data, connectTimeoutMs, readTimeoutMs, false, config);
    }

    /**
     * 可重试的，双向认证的请求
     * 
     * @param urlSuffix
     * @param uuid
     * @param data
     * @return
     */
    public static String requestWithCert(String urlSuffix, String uuid, String data, WXPayConfig config) throws Exception {
        return request(urlSuffix, uuid, data, config.getHttpConnectTimeoutMs(), config.getHttpReadTimeoutMs(), true, config);
    }

    /**
     * 可重试的，双向认证的请求
     * 
     * @param urlSuffix
     * @param uuid
     * @param data
     * @param connectTimeoutMs
     * @param readTimeoutMs
     * @return
     */
    public static String requestWithCert(String urlSuffix, String uuid, String data, int connectTimeoutMs, int readTimeoutMs, WXPayConfig config) throws Exception {
        return request(urlSuffix, uuid, data, connectTimeoutMs, readTimeoutMs, true, config);
    }
}
