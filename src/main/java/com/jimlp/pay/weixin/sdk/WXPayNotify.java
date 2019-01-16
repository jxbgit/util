package com.jimlp.pay.weixin.sdk;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class WXPayNotify {

	private WXPayNotify() {
	}

	/**
	 * 微信支付结果通知检查及结果封装处理
	 * 
	 * @param req
	 * @return
	 */
	public static WXPayNotifyInfo payNotify(HttpServletRequest req, String key) {
		WXPayNotifyInfo rst = new WXPayNotifyInfo();
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
		if (!WXPayConstants.SUCCESS.equals(rawMap.get("return_code"))) {
			rst.setCode(2);
			rst.setMsg(rawMap.get("return_msg"));
			return rst;
		}
		// 业务结果
		if (!WXPayConstants.SUCCESS.equals(rawMap.get("result_code"))) {
			rst.setCode(3);
			rst.setMsg(rawMap.get("err_code") + "：" + rawMap.get("err_code_des"));
			return rst;
		}
		// 检验签名
		try {
			if (!WXPayUtil.isSignatureValid(rawMap, key, null)) {
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

	/**
	 * 微信退款结果通知检查及结果封装处理
	 * 
	 * @param req
	 * @return
	 */
	public static WXPayNotifyInfo refundNotify(HttpServletRequest req, String key) {
		WXPayNotifyInfo rst = new WXPayNotifyInfo();
		rst.setCode(0);
		String xml = null;
		Map<String, String> rawMap = new HashMap<>();
		try {
			InputStream inStream = req.getInputStream();
			xml = inputStreamToString(inStream, "UTF-8");
			// 测试
			// key = "2IBtBXdrqC3kCBs4gaceL7nl2nnFadQv";
			// xml = "<xml><return_code>SUCCESS</return_code><appid><![CDATA[wx2421b1c4370ec43b]]></appid><mch_id><![CDATA[10000100]]></mch_id><nonce_str><![CDATA[TeqClE3i0mvn3DrK]]></nonce_str><req_info><![CDATA[m4NnwrtY0jhpDgNp65H1V/0OWMtSoTYhhY89MHbflhmnaHq9ZKjx9ABq6Jpg4SccA876HVy7J9P85NpdvCMNGInZ4fANDRE+YfZe4HeF+bbFj6JETcEFPpE9YW+bTbC0D+gl/otScJfvB2QUK7+EeBGPHN1EWX9zbr2Gw6AUaORdFk3mGxV5dtjuwWQrv5juzkXDs33Z2dUMslO+i3j0cTDHqwS4hptx2j6h2HvzgzltFbjo7nysU+4rArqJvrGW/9r18e1St9XgG21NALqixfaSmqetOR4zLVL4/+z3CEz8cg5r+/4GUOTf3XFmLCZ/wEkRQhKRNVibG0NFfiG3KnqArMJ/dheQHCd7qL+XX/ZV6tj8RLMgL7R6hOiR03Ljyikdxq9M3K9CTYgf03pHJd3geXX1LgXrLxc1flL6NW+zD3ZayGYpr1WpLsSMG7z8W5j1pme6cRj3n2+CwSFnOnOkxaFuLKoJAJIqM3gbC0eN++vY73RKphlI4zZqg6o5s3MXI6ju1yoi/ZQ+XbTg2JttsdbU0aySernKwkt0rYMf0j/Mcvo2axgHbI3w/iTm141WxHUjkQ+ga2X1pOWdGifGhSmMP8oGaA/WD5MAsK1qXX0eFvUWS/PTauCSTWq5Cmr8loA/KL3jgvB0nyR4mfccB+tPy4Ny7kzOlr/VNeb0ULf96R0AWFWCtdt8AmujAP0DYiM5FSmYLI0XRhpSDjnEbBM8+isNE1GlAVR3NzzemwQORihScovpAktbRSN/d3N+NgTjSoVDiJvCOxCs3thX9qt9iwYbA+/X/gv8lza2FZyIzwkQxGRcYl8JWKpXzNW8EWUNVnSLdHvQttDeV3CvgP/x579RGd6whyFYS6AaI0qw7oTjCFh2EHS/VzGvFuv166ZlVIJ4MNvg79O9h63ZOSE1LzVqEsVh8fDCfM2GgJ9aUdl95Djgunit4yIZOdoigR3f/BEHKrYCEham11rYohaAXs4XAXWihsV3WD5j4G/P+txvjAwujvf4HDwzHgFsmSml013U2mUiy+v4zw==]]></req_info></xml>";
			rst.setRaw(xml);
			rawMap = WXPayUtil.xmlToMap(xml);
			rst.setRawMap(rawMap);
		} catch (Exception e) {
			rst.setCode(1);
			rst.setMsg("获取或解析支付结果数据失败：" + e.getMessage());
			return rst;
		}
		// 状态（通信标识，非交易标识）
		if (!WXPayConstants.SUCCESS.equals(rawMap.get("return_code"))) {
			rst.setCode(2);
			rst.setMsg(rawMap.get("return_msg"));
			return rst;
		}

		// 解密数据
		try {
			String reqInfo = rawMap.get("req_info");
			reqInfo = WXPayUtil.refundNotifyDecode(reqInfo, key);
			Map<String, String> reqInfoMap = WXPayUtil.xmlToMap(reqInfo);
			rawMap.putAll(reqInfoMap);
		} catch (Exception e) {
			rst.setCode(3);
			rst.setMsg("数据解密失败");
			return rst;
		}

		return rst;
	}

	/**
	 * 将输入流转字符串
	 * 
	 * @param stream
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	private static String inputStreamToString(InputStream stream, String charset) throws IOException {
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
}
