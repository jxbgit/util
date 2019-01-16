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

	public int getHttpConnectTimeoutMs() {
		return 10000;
	}

	public int getHttpReadTimeoutMs() {
		return 10000;
	}

	/**
	 * 获取微信支付接口主域名
	 * 
	 * @return
	 */
	public abstract String getWXPayDomain();

	/**
	 * 获取微信支付接口备用域名，用于多域名容灾自动切换
	 * 
	 * @return
	 */
	public abstract String getWXPayDomain2();

	public abstract String getCertAbsolutePath();

	public final InputStream getCertStream() {
		if (certData == null) {
			synchronized (WXPayConfig.class) {
				if (certData == null) {

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
				}
			}
		}
		return new ByteArrayInputStream(certData);
	}
}
