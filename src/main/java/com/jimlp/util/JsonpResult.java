package com.jimlp.util;

import java.io.Serializable;

public class JsonpResult extends JsonResult implements Serializable {

	private static final long serialVersionUID = 2942225717336212658L;
	// 回调函数名。
	private String callback;

	public JsonpResult() {
		super();
	}

	public JsonpResult(String callback, int code, String msg, Object result) {
		super(code, msg, result);
		this.callback = callback;
	}

	public JsonpResult(String callback, Object result) {
		super(result);
		this.callback = callback;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	@Override
	public String toJsonString() {
		String result = super.toJsonString();
		return callback + "({" + result.substring(result.indexOf("\"result\":\"")) + ")";
	}

	@Override
	public String toString() {
		return "JsonpResult [callback=" + callback + ", code=" + code + ", msg=" + msg + ", data=" + data + "]";
	}

}