package com.jimlp.pay.weixin.sdk;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class WXPayConfig {

    private static byte[] certData;

    public abstract String getAppID();

    public abstract String getMchID();

    public abstract String getKey();

    public abstract String getCertAbsolutePath();

    public int getHttpConnectTimeoutMs() {
        return 30000;
    }

    public int getHttpReadTimeoutMs() {
        return 30000;
    }

    public final InputStream getCertStream() {
        if (certData != null) {
            return new ByteArrayInputStream(certData);
        }
        InputStream certStream = null;
        try {
            String certPath = getCertAbsolutePath();
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
        return new ByteArrayInputStream(certData);
    }
}
