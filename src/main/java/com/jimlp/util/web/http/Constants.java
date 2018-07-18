package com.jimlp.util.web.http;

/**
 * 公用常量类。
 * 
 * @author carver.gu
 * @since 1.0, Sep 12, 2009
 */
public abstract class Constants {

    /** UTF-8字符集 **/
    public static final String CHARSET_UTF8 = "UTF-8";
    /** GBK字符集 **/
    public static final String CHARSET_GBK = "GBK";

    /** 默认媒体类型 **/
    public static final String MIME_TYPE_DEFAULT = "application/octet-stream";
    /** HTTP请求相关 **/
    public static final String TOP_HTTP_DNS_HOST = "TOP_HTTP_DNS_HOST";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";
    public static final String CTYPE_FORM_DATA = "application/x-www-form-urlencoded";
    public static final String CTYPE_FILE_UPLOAD = "multipart/form-data";
    public static final String CTYPE_TEXT_XML = "text/xml";
    public static final String CTYPE_TEXT_PLAIN = "text/plain";
    public static final String CTYPE_APP_JSON = "application/json";
    /** 响应编码 */
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    public static final String CONTENT_ENCODING = "Content-Encoding";
    public static final String CONTENT_ENCODING_GZIP = "gzip";

    /** 默认流式读取缓冲区大小 **/
    public static final int READ_BUFFER_SIZE = 1024 * 4;

}
