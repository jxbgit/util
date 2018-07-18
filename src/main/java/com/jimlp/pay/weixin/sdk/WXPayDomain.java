package com.jimlp.pay.weixin.sdk;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.conn.ConnectTimeoutException;

/**
 * 域名管理，实现主备域名自动切换
 */
public class WXPayDomain {
    private static final Object LOCK = new Object();
    private static final int MIN_SWITCH_PRIMARY_MSEC = 5 * 60 * 1000;  //3 minutes
    private static long switchToAlternateDomainTime = 0;
    private static Map<String, DomainStatics> domainData = new HashMap<String, DomainStatics>(4,0.5f);

    private WXPayDomain() {
    }

    /**
     * 记录域名网络异常状况
     * @param domain 域名。 比如：api.mch.weixin.qq.com
     * @param ex 网络请求中出现的异常。
     *           null表示没有异常
     *           ConnectTimeoutException，表示建立网络连接异常
     *           UnknownHostException， 表示dns解析异常
     */
    public static void report(final String domain, final Exception ex){
        if(ex == null){ //success
            return;
        }
        
        DomainStatics info = domainData.get(domain);
        if(info == null){
            synchronized (LOCK) {
                if (info == null) {
                    info = new DomainStatics();
                    domainData.put(domain, info);
                }
            }
        }

        if(ex instanceof ConnectTimeoutException){
            info.dnsErrorCount = 0;
            ++info.connectTimeoutCount;
        }else if(ex instanceof UnknownHostException){
            ++info.dnsErrorCount;
        }else{
            ++info.otherErrorCount;
        }
    }

    /**
     * 获取域名
     * @param config 配置
     * @return 域名
     */
    public static DomainInfo getDomain(){
        DomainStatics primaryDomain = domainData.get(WXPayConstants.DOMAIN_API);
        if(primaryDomain == null || primaryDomain.isGood()) {
            return new DomainInfo(WXPayConstants.DOMAIN_API, true);
        }

        long now = System.currentTimeMillis();
        if(switchToAlternateDomainTime == 0){   //first switch
            switchToAlternateDomainTime = now;
            return new DomainInfo(WXPayConstants.DOMAIN_API2, false);
        }else if(now - switchToAlternateDomainTime < MIN_SWITCH_PRIMARY_MSEC){
            DomainStatics alternateDomain = domainData.get(WXPayConstants.DOMAIN_API2);
            if(alternateDomain == null ||
                alternateDomain.isGood() ||
                alternateDomain.badCount() < primaryDomain.badCount()){
                return new DomainInfo(WXPayConstants.DOMAIN_API2, false);
            }else{
                return new DomainInfo(WXPayConstants.DOMAIN_API, true);
            }
        }else{  //force switch back
            switchToAlternateDomainTime = 0;
            primaryDomain.resetCount();
            DomainStatics alternateDomain = domainData.get(WXPayConstants.DOMAIN_API2);
            if(alternateDomain != null)
                alternateDomain.resetCount();
            return new DomainInfo(WXPayConstants.DOMAIN_API, true);
        }
    }

    static class DomainInfo{
        public String domain;       //域名
        public boolean primaryDomain;     //该域名是否为主域名。例如:api.mch.weixin.qq.com为主域名
        public DomainInfo(String domain, boolean primaryDomain) {
            this.domain = domain;
            this.primaryDomain = primaryDomain;
        }

        @Override
        public String toString() {
            return "DomainInfo{" +
                    "domain='" + domain + '\'' +
                    ", primaryDomain=" + primaryDomain +
                    '}';
        }
    }
    
    private static class DomainStatics {
        int connectTimeoutCount = 0;
        int dnsErrorCount = 0;
        int otherErrorCount = 0;

        DomainStatics() {
        }

        void resetCount() {
            connectTimeoutCount = dnsErrorCount = otherErrorCount = 0;
        }

        boolean isGood() {
            return connectTimeoutCount <= 2 && dnsErrorCount <= 2;
        }

        int badCount() {
            return connectTimeoutCount + dnsErrorCount * 5 + otherErrorCount / 4;
        }
    }
}