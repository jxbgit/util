package com.jimlp.pay.weixin.sdk;

import java.util.Map;

/**
 *
 * <br>
 * 创建时间 2018年7月8日下午8:59:38
 *
 * @author jxb
 *
 */
public class WXPayNotifyInfo {
	private int code;
	private String msg;
	private String raw;
	private Map<String, String> rawMap;

	public WXPayNotifyInfo() {
		super();
	}

	public WXPayNotifyInfo(int code, String msg, String raw, Map<String, String> rawMap) {
		super();
		this.code = code;
		this.msg = msg;
		this.raw = raw;
		this.rawMap = rawMap;
	}

	/**
	 * 错误码[0-5]（0为正常）
	 * 
	 * @return
	 */
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * 错误描述
	 * 
	 * @return
	 */
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * 微信支付（退款）结果通知原数据
	 * 
	 * @return
	 */
	public String getRaw() {
		return raw;
	}

	public void setRaw(String raw) {
		this.raw = raw;
	}

	/**
	 * 微信支付（退款）结果通知，各参数值直接通过get(参数名参考微信通知参数)<br>
	 * <li>退款结果数据结构简化处理：所有关键字段直接包含在返回的Map中。req_info字段值为原加密串，其内部字段直接包含在返回的Map中。</li>
	 * 
	 * @return
	 */
	public Map<String, String> getRawMap() {
		return rawMap;
	}

	public void setRawMap(Map<String, String> rawMap) {
		this.rawMap = rawMap;
	}

	@Override
	public String toString() {
		return "WXPayNotify [code=" + code + ", msg=" + msg + ", raw=" + raw + ", rawMap=" + rawMap + "]";
	}

}
