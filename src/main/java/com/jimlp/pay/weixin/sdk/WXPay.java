package com.jimlp.pay.weixin.sdk;

import java.util.HashMap;
import java.util.Map;

import com.jimlp.pay.weixin.sdk.WXPayConstants.SignType;

public class WXPay {
	private WXPayConfig config;
	private boolean useSandbox = false;
	private static volatile WXPay instance;
	private static volatile WXPay sandboxInstance;

	private WXPay() {
	}

	/**
	 * 获取微信支付接口实例
	 * 
	 * @param config
	 *            只在第一次获取实例时生效
	 * 
	 * @return
	 */
	public static WXPay instance(WXPayConfig config) {
		if (instance == null) {
			synchronized (WXPay.class) {
				if (instance == null) {
					instance = new WXPay();
					instance.config = config;
				}
			}
		}
		return instance;
	}

	/**
	 * 获取微信支付接口实例（沙箱模式）
	 * 
	 * @param config
	 *            只在第一次获取实例时生效
	 * 
	 * @return
	 */
	public static WXPay instanceSandbox(WXPayConfig config) {
		if (sandboxInstance == null) {
			synchronized (WXPay.class) {
				if (sandboxInstance == null) {
					sandboxInstance = new WXPay();
					sandboxInstance.config = config;
					sandboxInstance.useSandbox = true;
				}
			}
		}
		return sandboxInstance;
	}

	/**
	 * 作用：提交统一下单<br>
	 * 场景：除付款码支付场景以外，商户系统先调用该接口在微信支付服务后台生成预支付交易单
	 * <li>JSAPI--JSAPI支付（公共号或小程序支付）</li>
	 * <li>NATIVE--扫码支付</li>
	 * <li>APP--app支付</li>
	 * <li>MWEB--H5支付</li>
	 * 
	 * @param reqData
	 *            接口请求数据
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
		return this.processResponseXml(respXml, reqData.get(WXPayConstants.FIELD_SIGN_TYPE));
	}

	/**
	 * 作用：提交付款码支付<br>
	 * 场景：MICROPAY--付款码支付
	 * 
	 * @param reqData
	 *            接口请求数据
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
		return this.processResponseXml(respXml, reqData.get(WXPayConstants.FIELD_SIGN_TYPE));
	}

	/**
	 * 作用：提交付款码支付<br>
	 * 场景：MICROPAY--付款码支付。针对软POS，尽可能做成功，内置重试机制（最多持续重试60s）
	 * 
	 * @param reqData
	 *            接口请求数据
	 * @return API返回数据
	 * @throws Exception
	 */
	public Map<String, String> microPayWithPos(Map<String, String> reqData) throws Exception {
		int connectTimeoutMs = 6 * 1000;
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
							// 看错误码，若支付结果未知，则重试提交付款码支付
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
	 * 作用：查询订单<br>
	 * 场景：该接口提供所有微信支付订单的查询，商户可以通过查询订单接口主动查询订单状态，完成下一步的业务逻辑。<br>
	 * 
	 * 需要调用查询接口的情况：<br>
	 * <li>当商户后台、网络、服务器等出现异常，商户系统最终未接收到支付通知</li>
	 * <li>调用支付接口后，返回系统错误或未知交易状态情况</li>
	 * <li>调用付款码支付API，返回USERPAYING的状态</li>
	 * <li>调用关单或撤销接口API之前，需确认支付状态</li>
	 * 
	 * @param reqData
	 *            接口请求数据
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
		return this.processResponseXml(respXml, reqData.get(WXPayConstants.FIELD_SIGN_TYPE));
	}

	/**
	 * 作用：撤销订单<br>
	 * 场景：付款码支付。支付交易返回失败或支付系统超时，调用该接口撤销交易<br>
	 * --如果此订单用户支付失败，微信支付系统会将此订单关闭<br>
	 * --如果用户支付成功，微信支付系统会将此订单资金退还给用户<br>
	 * 证书：需要证书<br>
	 * 注意：
	 * <li>7天以内的交易单可调用撤销，其他正常支付的单如需实现相同功能请调用申请退款API</li>
	 * <li>提交支付交易后调用【查询订单API】，没有明确的支付结果再调用【撤销订单API】</li>
	 * <li>调用支付接口后请勿立即调用撤销订单API，建议支付后至少15s后再调用撤销订单接口</li>
	 * 
	 * @param reqData
	 *            接口请求数据
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
		return this.processResponseXml(respXml, reqData.get(WXPayConstants.FIELD_SIGN_TYPE));
	}

	/**
	 * 作用：关闭订单<br>
	 * 场景：公共号支付、扫码支付、APP支付<br>
	 * 以下情况需要调用关单接口：<br>
	 * <li>商户订单支付失败需要生成新单号重新发起支付，要对原订单号调用关单，避免重复支付</li>
	 * <li>系统下单后，用户支付超时， 系统退出不再受理，避免用户继续，请调用关单接口</li>
	 * 
	 * <strong>注意：订单生成后不能马上调用关单接口，最短调用时间间隔为5分钟</strong>>
	 * 
	 * @param reqData
	 *            接口请求数据
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
		return this.processResponseXml(respXml, reqData.get(WXPayConstants.FIELD_SIGN_TYPE));
	}

	/**
	 * 作用：申请退款，按照退款规则将支付款按原路退到买家帐号上<br>
	 * 场景：付款码支付、公共号支付、扫码支付、APP支付<br>
	 * 其他：需要证书 注意：
	 * 
	 * <li>交易时间超过一年的订单无法提交退款</li>
	 * <li>微信支付退款支持单笔交易分多次退款，多次退款需要提交原支付订单的商户订单号和设置不同的退款单号</li>
	 * <li>申请退款总金额不能超过订单金额</li>
	 * <li>一笔退款失败后重新提交，请不要更换退款单号，请使用原商户退款单号</li>
	 * <li>请求频率限制：150qps，即每秒钟正常的申请退款请求次数不超过150次</li>
	 * <li>错误或无效请求频率限制：6qps，即每秒钟异常或错误的退款申请请求不超过6次</li>
	 * <li>每个支付订单的部分退款次数不能超过50次</li>
	 * 
	 * @param reqData
	 *            接口请求数据
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
		return this.processResponseXml(respXml, reqData.get(WXPayConstants.FIELD_SIGN_TYPE));
	}

	/**
	 * 作用：退款查询。退款有一定延时，用零钱支付的退款20分钟内到账，银行卡支付的退款3个工作日后重新查询退款状态<br>
	 * 场景：付款码支付、公共号支付、扫码支付、APP支付<br>
	 * <strong>注意：如果单个支付订单部分退款次数超过20次请使用退款单号查询</strong>
	 * 
	 * @param reqData
	 *            接口请求数据
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
		return this.processResponseXml(respXml, reqData.get(WXPayConstants.FIELD_SIGN_TYPE));
	}

	/**
	 * 作用：对账单下载<br>
	 * 场景：付款码支付、公共号支付、扫码支付、APP支付<br>
	 * 
	 * @param reqData
	 *            接口请求数据
	 * @return 经过封装的API返回数据。返回字段包括return_code、return_msg， 若return_code为SUCCESS时还包括data（对账单数据，数据格式详见微信官方文档）
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
	 * 场景：扫码支付。该接口主要用于Native支付模式一中的二维码链接转成短链接(weixin://wxpay/s/XXXXXX)，减小二维码数据量，提升扫描速度和精确度
	 * 
	 * @param reqData
	 *            接口请求数据
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
		return this.processResponseXml(respXml, reqData.get(WXPayConstants.FIELD_SIGN_TYPE));
	}

	/**
	 * 作用：授权码查询OPENID接口<br>
	 * 场景：付款码支付
	 * 
	 * @param reqData
	 *            接口请求数据
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
		return this.processResponseXml(respXml, reqData.get(WXPayConstants.FIELD_SIGN_TYPE));
	}

	/**
	 * 不需要证书的请求
	 * 
	 * @param urlSuffix
	 *            接口地址后缀（域名后面的地址）
	 * @param reqData
	 *            接口请求数据
	 * @return 返回API响应原数据
	 * @throws Exception
	 */
	private String requestWithoutCert(String urlSuffix, Map<String, String> reqData) throws Exception {
		String msgUUID = reqData.get("nonce_str");
		String reqBody = WXPayUtil.mapToXml(reqData);

		String resp = WXPayRequest.requestWithoutCert(urlSuffix, msgUUID, reqBody, config.getHttpConnectTimeoutMs(), config.getHttpReadTimeoutMs(), config);
		return resp;
	}

	/**
	 * 需要证书的请求
	 * 
	 * @param urlSuffix
	 *            接口地址后缀（域名后面的地址）
	 * @param reqData
	 *            接口请求数据
	 * @return 返回API响应原数据
	 * @throws Exception
	 */
	private String requestWithCert(String urlSuffix, Map<String, String> reqData) throws Exception {
		String msgUUID = reqData.get("nonce_str");
		String reqBody = WXPayUtil.mapToXml(reqData);

		String resp = WXPayRequest.requestWithCert(urlSuffix, msgUUID, reqBody, config.getHttpConnectTimeoutMs(), config.getHttpReadTimeoutMs(), config);
		return resp;
	}

	/**
	 * 将API返回的xml字符串，转换成Map。返回参数中 return_code==SUCCESS 时，将验证签名。
	 * 
	 * @param xmlStr
	 *            微信接口返回的XML格式数据
	 * @param signType
	 *            指定xmlStr参数未指定签名类型时所使用的验签签名类型
	 * @return 返回Map类型数据
	 * 
	 * @throws Exception
	 *             <li>当返回数据中不包含“return_code”字段时</li>
	 *             <li>当返回数据中“return_code”字段值无效时</li>
	 *             <li>当返回的数据签名无效时</li>
	 *             <li>其他异常</li>
	 */
	private Map<String, String> processResponseXml(String xmlStr, String signType) throws Exception {
		String RETURN_CODE = "return_code";
		String return_code;
		Map<String, String> respData = WXPayUtil.xmlToMap(xmlStr);
		if (respData.containsKey(RETURN_CODE)) {
			return_code = respData.get(RETURN_CODE);
		} else {
			throw new NoSuchFieldException(String.format("No `return_code` in XML: %s", xmlStr));
		}

		if (return_code.equals(WXPayConstants.FAIL)) {
			return respData;
		} else if (return_code.equals(WXPayConstants.SUCCESS)) {
			if (WXPayUtil.isSignatureValid(respData, config.getKey(), SignType.getByName(signType))) {
				return respData;
			} else {
				throw new IllegalSignException(String.format("Invalid sign value in XML: %s", xmlStr));
			}
		} else {
			throw new IllegalArgumentException(String.format("return_code value %s is invalid in XML: %s", return_code, xmlStr));
		}
	}
}