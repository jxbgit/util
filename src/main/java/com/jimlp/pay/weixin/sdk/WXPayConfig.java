package com.jimlp.pay.weixin.sdk;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class WXPayConfig {

    private static byte[] certData;

    static {
        InputStream certStream = null;
        try {
            // TODO
            String certPath = WXPayConfig.class.getResource("/").getPath() + "config/apiclient_cert.p12";
            File file = new File(certPath);
            certStream = new FileInputStream(file);
            certData = new byte[(int) file.length()];
            certStream.read(certData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (certStream != null) {
                try {
                    certStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getAppID() {
        // TODO
        return "";
    }

    public static String getMchID() {
        // TODO
        return "";
    }

    public static String getKey() {
        // TODO
        return "";
    }

    
    public static int getHttpConnectTimeoutMs() {
        return 30000;
    }
    
    public static int getHttpReadTimeoutMs() {
        return 30000;
    }
    
    public static InputStream getCertStream() {
        ByteArrayInputStream certBis;
        certBis = new ByteArrayInputStream(certData);
        return certBis;
    }
}
