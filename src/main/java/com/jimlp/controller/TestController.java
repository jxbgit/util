package com.jimlp.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * <br>
 * 创建时间 2018年7月12日下午5:10:44
 *
 * @author jxb
 *
 */
@Controller
public class TestController {
    @RequestMapping("/")
    private void index() {

    }

    @RequestMapping("/t1")
    @ResponseBody
    private Object name(HttpServletRequest req) throws Exception {
        return "a";
    }
}
