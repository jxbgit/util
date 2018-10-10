package com.jimlp.util.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

/**
 * 
 *
 * 修改时间 2018年10月10日下午6:13:10<br>
 * {@link Map} 实现从{@link HashMap}改用{@link IdentityHashMap}。支持重复子元素<br>
 * 
 * @author jxb
 *
 */
public class XmlUtils {

    private static final String ENCODING = "UTF-8";

    /**
     * Xml 转 Map，忽略元素属性。（默认禁止引用外部实体，UTF-8编码）
     * 
     * @param xmlFile
     * @return
     * @throws DocumentException
     * @throws FileNotFoundException
     */
    public static Map<String, Object> XmlToMap(File xmlFile) throws DocumentException, FileNotFoundException {
        return XmlToMap(new FileInputStream(xmlFile));
    }

    /**
     * Xml 转 Map，忽略元素属性。（默认禁止引用外部实体）
     * 
     * @param xmlFile
     * @param encoding
     * @return
     * @throws DocumentException
     * @throws FileNotFoundException
     */
    public static Map<String, Object> XmlToMap(File xmlFile, String encoding) throws DocumentException, FileNotFoundException {
        return XmlToMap(new FileInputStream(xmlFile), encoding);
    }

    /**
     * Xml 转 Map，忽略元素属性。当xmlStr不包含声明或声明中未指定编码格式时，默认为UTF-8编码。（默认禁止引用外部实体）
     * 
     * @param xmlStr
     *            XML字符串
     * @return
     * @throws DocumentException
     * @throws UnsupportedEncodingException
     */
    public static Map<String, Object> XmlToMap(String xmlStr) throws DocumentException, UnsupportedEncodingException {
        return XmlToMap(xmlStr, ENCODING);
    }

    /**
     * Xml 转 Map，忽略元素属性。（默认禁止引用外部实体）
     * 
     * @param xmlStr
     *            XML字符串
     * @param encoding
     *            指定xmlStr的编码格式。（注意：当xmlStr包含声明时，将优先使用声明中指定的编码格式解析字符串）
     * @return
     * @throws DocumentException
     * @throws UnsupportedEncodingException
     */
    public static Map<String, Object> XmlToMap(String xmlStr, String encoding) throws DocumentException, UnsupportedEncodingException {
        String _encoding = getEncoding(xmlStr);
        _encoding = _encoding == null ? encoding : _encoding;
        byte[] b = xmlStr.getBytes(_encoding);
        InputStream inputStream = new ByteArrayInputStream(b);
        return XmlToMap(inputStream, encoding);
    }

    /**
     * Xml 转 Map，忽略元素属性。（默认禁止引用外部实体，UFT-8编码）
     * 
     * @param inputStream
     * @return
     * @throws DocumentException
     */
    public static Map<String, Object> XmlToMap(InputStream inputStream) throws DocumentException {
        return XmlToMap(inputStream, ENCODING);
    }

    /**
     * Xml 转 Map，忽略元素属性。（默认禁止引用外部实体）
     * 
     * @param inputStream
     * @param encoding
     * @return
     * @throws DocumentException
     */
    public static Map<String, Object> XmlToMap(InputStream inputStream, String encoding) throws DocumentException {
        Map<String, Object> map = new IdentityHashMap<String, Object>();
        SAXReader reader = new SAXReader();
        reader.setEncoding(encoding);
        setFeature(reader);
        Document doc = reader.read(inputStream);
        Element root = doc.getRootElement();
        map = ElementToMap(root);
        return map;
    }

    /**
     * Xml 转 Map，忽略元素属性，且只解析根元素的直接子元素。（默认禁止引用外部实体，UFT-8编码）
     * 
     * @param inputStream
     * @return
     * @throws DocumentException
     */
    public static Map<String, String> XmlToMapOnlyChild(InputStream inputStream) throws DocumentException {
        return XmlToMapOnlyChild(inputStream, ENCODING);
    }

    /**
     * Xml 转 Map，忽略元素属性，且只解析根元素的直接子元素。（默认禁止引用外部实体）
     * 
     * @param inputStream
     * @param encoding
     * @return
     * @throws DocumentException
     */
    public static Map<String, String> XmlToMapOnlyChild(InputStream inputStream, String encoding) throws DocumentException {
        SAXReader reader = new SAXReader();
        reader.setEncoding(encoding);
        setFeature(reader);
        Document doc = reader.read(inputStream);
        Element root = doc.getRootElement();
        Map<String, String> map = ElementToMapOnlyChild(root);
        return map;
    }

    /**
     * XML element 转 Map，忽略元素属性，只解析当前元素的直接子元素
     * 
     * @param ele
     * @return
     */
    public static Map<String, String> ElementToMapOnlyChild(Element ele) {
        Map<String, String> map = new IdentityHashMap<>();
        @SuppressWarnings("unchecked")
        List<Element> eleList = ele.elements();
        int size = eleList.size();
        if (size != 0) {
            Element innerEle;
            for (Iterator<Element> iter = eleList.iterator(); iter.hasNext();) {
                innerEle = iter.next();
                map.put(innerEle.getName(), innerEle.getTextTrim());
            }
        }
        return map;
    }

    /**
     * XML element 转 Map，忽略元素属性
     * 
     * @param ele
     * @return
     */
    public static Map<String, Object> ElementToMap(Element ele) {
        Map<String, Object> map = new IdentityHashMap<>();
        @SuppressWarnings("unchecked")
        List<Element> eleList = ele.elements();
        String name = new String(ele.getName());
        int size = eleList.size();
        if (size == 0) {
            map.put(name, ele.getTextTrim());
            return map;
        }

        Map<String, Object> childMap = new IdentityHashMap<>();
        for (Iterator<Element> iter = eleList.iterator(); iter.hasNext();) {
            Element innerEle = iter.next();
            childMap.putAll(ElementToMap(innerEle));
        }
        map.put(name, childMap);

        return map;
    }

    /**
     * 获取 XML字符串 的编码格式
     * 
     * @param xml
     * @return
     */
    public static String getEncoding(String xml) {
        String result = null;
        xml = xml.trim();
        if (xml.startsWith("<?xml")) {
            int end = xml.indexOf("?>");
            String sub = xml.substring(0, end);
            StringTokenizer tokens = new StringTokenizer(sub, " =\"\'");
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                if ("encoding".equals(token)) {
                    if (tokens.hasMoreTokens()) {
                        result = tokens.nextToken();
                    }
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Map转xml字符串（默认不包含声明、数据值不使用CDATA处理）
     * 
     * @param rootEle
     *            指定根元素
     * @param childMap
     *            子元素数据
     * @return
     */
    public static String mapToXml(String rootEle, Map<String, ?> childMap) {
        return mapToXml(rootEle, childMap, null, false);
    }

    /**
     * Map转xml字符串
     * 
     * @param rootEle
     *            指定根元素
     * @param childMap
     *            子元素数据
     * @param encoding
     *            指定xml声明中使用的编码（若不指定，将不包含声明部分）
     * @param userCDATA
     *            数据值是否使用CDATA处理
     * @return
     */
    public static String mapToXml(String rootEle, Map<String, ?> childMap, String encoding, boolean userCDATA) {
        StringBuilder xml = new StringBuilder();
        if (encoding != null) {
            xml.append("<?xml version=\"1.0\" encoding=\"");
            xml.append(encoding);
            xml.append("\"?>");
        }
        xml.append("<");
        xml.append(rootEle);
        xml.append('>');
        xml.append(mapToXml(childMap));
        xml.append("</");
        xml.append(rootEle);
        xml.append('>');
        return xml.toString();
    }

    /**
     * Map转xml字符串（默认不包含声明、数据值不使用CDATA处理）
     * 
     * @param map
     *            数据
     * @return
     */
    public static String mapToXml(Map<String, ?> map) {
        return mapToXml(map, null, false);
    }

    /**
     * Map转xml字符串（默认数据值不使用CDATA处理）
     * 
     * @param map
     *            数据
     * @param encoding
     *            指定xml声明中使用的编码（若不指定，将不包含声明部分）
     * @return
     */
    public static String mapToXml(Map<String, ?> map, String encoding) {
        return mapToXml(map, encoding, false);
    }

    /**
     * Map转xml字符串
     * 
     * @param map
     *            数据
     * @param encoding
     *            指定xml声明中使用的编码（若不指定，将不包含声明部分）
     * @param userCDATA
     *            数据值是否使用CDATA处理
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String mapToXml(Map<String, ?> map, String encoding, boolean userCDATA) {
        StringBuilder xml = new StringBuilder();
        if (encoding != null) {
            xml.append("<?xml version=\"1.0\" encoding=\"");
            xml.append(encoding);
            xml.append("\"?>");
        }
        for (String key : map.keySet()) {
            xml.append("<");
            xml.append(key);
            xml.append('>');
            Object rootEleVal = map.get(key);
            if (rootEleVal instanceof Map) {
                rootEleVal = mapToXml((Map<String, Object>) rootEleVal, null, userCDATA);
                xml.append(rootEleVal);
            } else {
                if (userCDATA) {
                    xml.append("<![CDATA[");
                }
                xml.append(rootEleVal);
                if (userCDATA) {
                    xml.append("]]>");
                }
            }
            xml.append("</");
            xml.append(key);
            xml.append('>');
        }
        return xml.toString();
    }

    /**
     * 禁止引用外部实体
     * 
     * @param reader
     */
    private static void setFeature(SAXReader reader) {
        try {
            reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        } catch (SAXException e) {
            e.printStackTrace();
        }
        try {
            reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        } catch (SAXException e) {
            e.printStackTrace();
        }
        try {
            reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        } catch (SAXException e) {
            e.printStackTrace();
        }
        try {
            reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}
