package com.jimlp.util.web.http;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.jimlp.util.web.IpUtils;
import com.jimlp.util.web.userAgentUtils;

/**
 * <p>
 * JavaEE 项目中，对 http 请求进行处理的工具类
 * 
 * @author JIML
 *
 */
public class RequestUtils {
	/**
	 * <p>
	 * 将 HttpServletRequest 转换为 MultipartHttpServletRequest ，当有文件上传并且未在 SpringMVC 中配置相关 bean 时需要此转换。<br>
	 * 默认不限制上传文件大小<br>
	 * 
	 * @param request
	 * @param encoding 使用的字符编码（可选参数，默认 UTF-8 ）
	 * 
	 * @return
	 * 
	 * @throws UnsupportedEncodingException 
	 */
	public static MultipartHttpServletRequest requestTransition(HttpServletRequest request, String... encoding) throws UnsupportedEncodingException {
		if (request == null) {
			return null;
		}
		MultipartHttpServletRequest mhsr = null;
		try {
			mhsr = (MultipartHttpServletRequest) request;
		} catch (ClassCastException  e) {
			CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			mhsr = commonsMultipartResolver.resolveMultipart(request);
		}
		if (encoding != null && encoding.length > 0) {
			mhsr.setCharacterEncoding(encoding[0]);
		} else {
			mhsr.setCharacterEncoding("UTF-8");
		}
		return mhsr;
	}

	/**
	 * <p>
	 * 将HttpServletRequest 转换为 MultipartHttpServletRequest ，当有文件上传时需要此转换。<br>
	 * 默认 UTF-8 字符编码<br>
	 * 
	 * @param request
	 * @param maxUploadSize 允许的最大上传文件大小
	 * @param encoding 使用的字符编码（可选参数，默认 UTF-8 ）
	 * 
	 * @return
	 * 
	 * @throws UnsupportedEncodingException 
	 */
	public static MultipartHttpServletRequest requestTransition(HttpServletRequest request, long maxUploadSize, String... encoding) throws UnsupportedEncodingException {
		if (request == null) {
			return null;
		}
		MultipartHttpServletRequest mhsr = null;
		if (request instanceof MultipartHttpServletRequest) {
			mhsr = (MultipartHttpServletRequest) request;
		} else {
			CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			commonsMultipartResolver.setMaxUploadSize(maxUploadSize);
			mhsr = commonsMultipartResolver.resolveMultipart(request);
		}
		if (encoding != null && encoding.length > 0) {
			mhsr.setCharacterEncoding(encoding[0]);
		} else {
			mhsr.setCharacterEncoding("UTF-8");
		}
		return mhsr;
	}

	public static HttpURLConnection randomRequestProperty(HttpURLConnection c) {
		c.setRequestProperty("accept", "*/*");
		c.setRequestProperty("connection", "Keep-Alive");
		c.setRequestProperty("user-agent", userAgentUtils.getRandomUserAgent());
		c.setRequestProperty("X-Real-IP", IpUtils.getRandomIp());
		return c;
	}
	
    /**
     * 
     * @param request
     * @param withPort
     * @return http://domain/contextPath、http://domain:port/contextPath
     */
    public static String getbaseUrl(HttpServletRequest request, boolean withPort) {
        StringBuilder sb = new StringBuilder(request.getScheme());
        sb.append("://");
        sb.append(request.getServerName());
        if (withPort) {
            sb.append(':');
            sb.append(request.getServerPort());
        }
        sb.append(request.getServletContext().getContextPath());
        return sb.toString();
    }
}
