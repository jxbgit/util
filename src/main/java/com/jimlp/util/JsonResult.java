package com.jimlp.util;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

public class JsonResult implements Serializable {

    private static final long serialVersionUID = 2942225717336212657L;
    // 状态码，默认0。
    protected int code = 0;
    // 提示信息
    protected String msg;
    // 主体数据
    protected Object data;

    public JsonResult() {
        super();
    }

    public JsonResult(int code, String msg, Object data) {
        super();
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public JsonResult(Object data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String toJsonString() {
        return JSON.toJSONString(this);
    }

    @Override
    public String toString() {
        return toJsonString();
    }
}