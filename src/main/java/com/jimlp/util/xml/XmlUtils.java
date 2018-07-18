package com.jimlp.util.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

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
    public static Map<String, Object> simpleXmlToMap(File xmlFile) throws DocumentException, FileNotFoundException {
        return simpleXmlToMap(new FileInputStream(xmlFile));
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
    public static Map<String, Object> simpleXmlToMap(File xmlFile, String encoding) throws DocumentException, FileNotFoundException {
        return simpleXmlToMap(new FileInputStream(xmlFile), encoding);
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
    public static Map<String, Object> simpleXmlToMap(String xmlStr) throws DocumentException, UnsupportedEncodingException {
        return simpleXmlToMap(xmlStr, ENCODING);
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
    public static Map<String, Object> simpleXmlToMap(String xmlStr, String encoding) throws DocumentException, UnsupportedEncodingException {
        String _encoding = getEncoding(xmlStr);
        _encoding = _encoding == null ? encoding : _encoding;
        byte[] b = xmlStr.getBytes(_encoding);
        InputStream inputStream = new ByteArrayInputStream(b);
        return simpleXmlToMap(inputStream, encoding);
    }

    /**
     * Xml 转 Map，忽略元素属性。（默认禁止引用外部实体，UFT-8编码）
     * 
     * @param inputStream
     * @return
     * @throws DocumentException
     */
    public static Map<String, Object> simpleXmlToMap(InputStream inputStream) throws DocumentException {
        return simpleXmlToMap(inputStream, ENCODING);
    }

    /**
     * Xml 转 Map，忽略元素属性。（默认禁止引用外部实体）
     * 
     * @param inputStream
     * @param encoding
     * @return
     * @throws DocumentException
     */
    public static Map<String, Object> simpleXmlToMap(InputStream inputStream, String encoding) throws DocumentException {
        Map<String, Object> map = new HashMap<String, Object>();
        SAXReader reader = new SAXReader();
        reader.setEncoding(encoding);
        setFeature(reader);
        Document doc = reader.read(inputStream);
        Element root = doc.getRootElement();
        map = simpleElementToMap(root);
        return map;
    }

    /**
     * Xml 转 Map，忽略元素属性，且只解析根元素的直接子元素。（默认禁止引用外部实体，UFT-8编码）
     * 
     * @param inputStream
     * @return
     * @throws DocumentException
     */
    public static Map<String, String> simpleXmlToMapOnlyChild(InputStream inputStream) throws DocumentException {
        return simpleXmlToMapOnlyChild(inputStream, ENCODING);
    }

    /**
     * Xml 转 Map，忽略元素属性，且只解析根元素的直接子元素。（默认禁止引用外部实体）
     * 
     * @param inputStream
     * @param encoding
     * @return
     * @throws DocumentException
     */
    public static Map<String, String> simpleXmlToMapOnlyChild(InputStream inputStream, String encoding) throws DocumentException {
        SAXReader reader = new SAXReader();
        reader.setEncoding(encoding);
        setFeature(reader);
        Document doc = reader.read(inputStream);
        Element root = doc.getRootElement();
        Map<String, String> map = simpleElementToMapOnlyChild(root);
        return map;
    }

    /**
     * XML element 转 Map，忽略元素属性，只解析当前元素的直接子元素
     * 
     * @param ele
     * @return
     */
    public static Map<String, String> simpleElementToMapOnlyChild(Element ele) {
        Map<String, String> map = new HashMap<>();
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
    public static Map<String, Object> simpleElementToMap(Element ele) {
        Map<String, Object> map = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Element> eleList = ele.elements();
        String name = ele.getName();
        int size = eleList.size();
        if (size == 0) {
            map.put(name, ele.getTextTrim());
            return map;
        }

        Map<String, Object> childMap = new HashMap<>();
        for (Iterator<Element> iter = eleList.iterator(); iter.hasNext();) {
            Element innerEle = iter.next();
            childMap.putAll(simpleElementToMap(innerEle));
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
    public static String mapToSimpleXmlOnlyOneLayer(String rootEle, Map<String, String> childMap) {
        return mapToSimpleXmlOnlyOneLayer(rootEle, childMap, ENCODING, false);
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
    public static String mapToSimpleXmlOnlyOneLayer(String rootEle, Map<String, String> childMap, String encoding, boolean userCDATA) {
        StringBuilder xml = new StringBuilder();
        if (encoding != null) {
            xml.append("<?xml version=\"1.0\" encoding=\"");
            xml.append(encoding);
            xml.append("\"?>");
        }
        xml.append("<");
        xml.append(rootEle);
        xml.append('>');
        Set<Entry<String, String>> entrySet = childMap.entrySet();
        String key = null;
        Object val = null;
        for (Entry<String, String> entry : entrySet) {
            key = entry.getKey();
            xml.append("<");
            xml.append(key);
            xml.append('>');
            val = entry.getValue();
            if (userCDATA) {
                xml.append("<![CDATA[");
            }
            xml.append(val);
            if (userCDATA) {
                xml.append("]]>");
            }
            xml.append("</");
            xml.append(key);
            xml.append('>');
        }
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
    public static String mapToSimpleXml(Map<String, ?> map) {
        return mapToSimpleXml(map, null, false);
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
    public static String mapToSimpleXml(Map<String, ?> map, String encoding) {
        return mapToSimpleXml(map, encoding, false);
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
    public static String mapToSimpleXml(Map<String, ?> map, String encoding, boolean userCDATA) {
        StringBuilder xml = new StringBuilder();
        if (encoding != null) {
            xml.append("<?xml version=\"1.0\" encoding=\"");
            xml.append(encoding);
            xml.append("\"?>");
        }
        xml.append("<");
        String root = map.keySet().iterator().next();
        xml.append(root);
        xml.append('>');
        Set<Entry<String, Object>> entrySet = ((Map<String, Object>) map.get(root)).entrySet();
        String key = null;
        Object val = null;
        for (Entry<String, Object> entry : entrySet) {
            key = entry.getKey();
            xml.append("<");
            xml.append(key);
            xml.append('>');
            val = entry.getValue();
            if (val instanceof Map) {
                val = mapToSimpleXml((Map<String, Object>) val, null, userCDATA);
                xml.append(val);
            } else {
                if (userCDATA) {
                    xml.append("<![CDATA[");
                }
                xml.append(val);
                if (userCDATA) {
                    xml.append("]]>");
                }
            }
            xml.append("</");
            xml.append(key);
            xml.append('>');
        }
        xml.append("</");
        xml.append(root);
        xml.append('>');
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
