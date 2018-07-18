package com.jimlp.util.web;

import java.util.Random;

public class userAgentUtils {
	public static String getRandomUserAgent() {
		Random random = new Random();
		// 网络状态
		String net = random.nextInt(2) == 0 ? "wifi" : "4g";
		// 微信版本生成（6.0.0-6.4.15）
		String mmV = random.nextInt(2) + 5 + "." + random.nextInt(5) + "." + random.nextInt(16);
		// applewebkit版本（530.1-609.20）
		String applewebkit = random.nextInt(80) + 530 + "." + (random.nextInt(20) + 1);

		String userAgent = null;

		if (random.nextInt(2) == 0) {
			// iphone
			userAgent = "mozilla/5.0 (iphone; cpu iphone os " + (random.nextInt(2) + 9) + "_" + random.nextInt(4) + "_" + random.nextInt(6) + " like mac os x) applewebkit/" + applewebkit + ".50 (khtml, like gecko) mobile/14" + (random.nextInt(2) == 0 ? "a" : random.nextInt(2) == 0 ? "b" : "c") + (random.nextInt(330) + 100) + " micromessenger/" + mmV + " nettype/" + net + " language/zh_cn";
		} else {
			// android
			String[] model = { "vivo x7", "vivo x7plus", "vivo x9", "vivo x9plus", "oppo a59m", "oppo r9m", "oppo r9km", "oppo r9tm", "oppo a59s", "mi 4c", "mi 4lte", "mi 5", "mi 5s", "mi max", "mi 6", "mi note", "sm-j5008", "sm-j7008", "huaweirio-al00", "huaweirio-tl00", "huaweirio-ul00" };
			int modelIndex = random.nextInt(model.length);
			userAgent = "mozilla/5.0 (linux; android " + (random.nextInt(4) + 4) + "." + random.nextInt(4) + "." + random.nextInt(3) + "; " + model[modelIndex] + " build/lmy47v; wv) applewebkit/" + applewebkit + " (khtml, like gecko) version/4.0 chrome/53.0.27" + random.nextInt(90) + "5.49 mobile mqqbrowser/6.2 tbs/043409 safari/537.36 micromessenger/" + mmV + ".1080 nettype/" + net + " language/zh_cn";
		}
		return userAgent;
	}
}
