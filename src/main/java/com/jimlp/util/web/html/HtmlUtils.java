package com.jimlp.util.web.html;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * <p>
 * HTML处理的相关工具类<br>
 * 未特殊说明的路径，默认都是以“/”开头的相对项目根目录的路径
 * 
 * @author JIML
 *
 */
public class HtmlUtils {

    /**
     * 将载有 html 源码的输入流保存为word文档
     * 
     * @param htmlIn
     *            载有 html 源码的输入流
     * @param wordSavaAbsolutePath
     *            word文档保存路径（包含文件名且扩展名必须为“.doc”）
     * @throws Exception
     */
    public static void html2WordDoc(InputStream htmlIn, String wordSavaAbsolutePath) throws Exception {
        if (wordSavaAbsolutePath == null || wordSavaAbsolutePath.endsWith(".doc")) {
            throw new IllegalArgumentException(new StringBuilder("文件扩展名必须为 ").append(".doc").toString());
        }
        OutputStream os = new FileOutputStream(wordSavaAbsolutePath);
        html2WordDoc(htmlIn, os);
    }

    /**
     * 将载有 html 源码的输入流保存为word文档
     * 
     * @param htmlIn
     *            载有 html 源码的输入流
     * @param saveFolder
     *            指定保存 word文档的文件夹
     * @param wordFileName
     *            指定保存 word文档所使用的文件名（不包含扩展名）
     * @throws Exception
     */
    public static void html2WordDoc(InputStream htmlIn, String saveFolder, String wordFileName) throws Exception {
        StringBuilder docPath = new StringBuilder(saveFolder).append(File.separatorChar).append(wordFileName).append(".doc");
        OutputStream os = new FileOutputStream(docPath.toString());
        html2WordDoc(htmlIn, os);
    }

    /**
     * 将载有 html 源码的输入流转换装入指定的 word文档输出流
     * 
     * @param htmlIn
     *            载有 html 源码的输入流
     * @param docOut
     *            word文档输出流容器
     * @throws IOException
     */
    public static void html2WordDoc(InputStream htmlIn, OutputStream docOut) throws IOException {
        POIFSFileSystem fs = new POIFSFileSystem();
        // 对应于org.apache.poi.hdf.extractor.WordDocument
        fs.createDocument(htmlIn, "WordDocument");
        fs.writeFilesystem(docOut);
        htmlIn.close();
        docOut.close();
        fs.close();
    }

    // ============================= html2WordDoc end

    /**
     * <p>
     * 获取指定标签名下指定属性的值的集合
     * <p>
     * 
     * @param html
     *            指定要搜索的 html 源码
     * @param tag
     *            指定标签名
     * @param attr
     *            指定属性名
     * 
     * @return 返回属性的值的集合
     */
    public static List<String> listValOfAttr(String html, String tag, String attr) {
        return listValOfAttr(html, tag, attr, null);
    }

    /**
     * <p>
     * 获取指定标签名下指定属性的值的集合
     * <p>
     * 
     * @param html
     *            指定要搜索的 html 源码
     * @param tag
     *            指定标签名
     * @param attr
     *            指定属性名
     * @param valueRegex
     *            指定获取属性值的正则
     * 
     * @return 返回属性的值的集合
     */
    public static List<String> listValOfAttr(String html, String tag, String attr, String valueRegex) {
    	if(valueRegex == null){
    		valueRegex = "[^>]*";
    	}
        StringBuilder regex = new StringBuilder('<').append(tag).append("[^>]*").append(attr).append("\\s*=\\s*(['\"])\\s*(").append(valueRegex).append("[^\\s])\\s*\\1");
        Pattern pattern = Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE);
        List<String> list = new ArrayList<String>();
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            list.add(matcher.group(2));
        }
        return list;
    }
}
