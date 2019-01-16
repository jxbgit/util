package com.jimlp.pay.weixin.sdk;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jimlp.pay.weixin.sdk.WXPayConstants.SignType;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

public class WXPayUtil {

	private static final String SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private static final Random RANDOM = new SecureRandom();

	private WXPayUtil() {
	}

	/**
	 * XML格式字符串转换为Map
	 *
	 * @param strXML
	 *            XML字符串
	 * @return XML数据转换后的Map
	 * @throws Exception
	 */
	public static Map<String, String> xmlToMap(String strXML) throws Exception {
		try {
			Map<String, String> data = new HashMap<String, String>();
			DocumentBuilder documentBuilder = newDocumentBuilder();
			InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
			Document doc = documentBuilder.parse(stream);
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getDocumentElement().getChildNodes();
			for (int idx = 0; idx < nodeList.getLength(); ++idx) {
				Node node = nodeList.item(idx);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					data.put(element.getNodeName(), element.getTextContent());
				}
			}
			try {
				stream.close();
			} catch (Exception ex) {
				// do nothing
			}
			return data;
		} catch (Exception ex) {
			throw ex;
		}

	}

	/**
	 * 将Map转换为XML格式的字符串
	 *
	 * @param data
	 *            Map类型数据
	 * @return XML格式的字符串
	 * @throws Exception
	 */
	public static String mapToXml(Map<String, String> data) throws Exception {
		Document document = newDocument();
		Element root = document.createElement("xml");
		document.appendChild(root);
		for (String key : data.keySet()) {
			String value = data.get(key);
			if (value == null) {
				value = "";
			}
			value = value.trim();
			Element filed = document.createElement(key);
			filed.appendChild(document.createTextNode(value));
			root.appendChild(filed);
		}
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		DOMSource source = new DOMSource(document);
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(source, result);
		String output = writer.getBuffer().toString(); // .replaceAll("\n|\r", "");
		try {
			writer.close();
		} catch (Exception ex) {
		}
		return output;
	}

	/**
	 * 判断签名是否正确，必须包含sign字段，否则返回false。若 xmlStr 和 signType 都未指定签名类型，将使用 {@link WXPayConstants.DEFAULT_SIGNTYPE}进行验签。
	 *
	 * @param xmlStr
	 *            XML格式数据
	 * @param key
	 *            API密钥
	 * @param signType
	 *            指定xmlStr参数未指定签名类型时所使用的签名类型
	 * @return 签名是否正确
	 * @throws Exception
	 */
	public static boolean isSignatureValid(String xmlStr, String key, SignType signType) throws Exception {
		return isSignatureValid(xmlToMap(xmlStr), key, signType);
	}

	/**
	 * 判断签名是否正确，必须包含sign字段，否则返回false。若 data 和 signType 都未指定签名类型，将使用 {@link WXPayConstants.DEFAULT_SIGNTYPE}进行验签。
	 *
	 * @param data
	 *            Map类型数据
	 * @param key
	 *            API密钥
	 * @param signType
	 *            指定data参数未指定签名类型时所使用的签名类型
	 * @return 签名是否正确
	 * @throws Exception
	 */
	public static boolean isSignatureValid(Map<String, String> data, String key, SignType signType) throws Exception {
		String sign = data.get(WXPayConstants.FIELD_SIGN);
		if (sign == null) {
			return false;
		}
		return generateSignature(data, WXPayConstants.FIELD_SIGN_TYPE, key, signType).equals(sign);
	}

	/**
	 * 向 入参 Map 中添加 appid、mch_id、nonce_str、sign_type、sign <br>
	 * 该函数适用于商户适用于统一下单等接口，不适用于红包、代金券接口
	 *
	 * @param reqData
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> fillRequestData(Map<String, String> reqData, WXPayConfig config) throws Exception {
		return fillRequestData(reqData, SignType.getByName(reqData.get(WXPayConstants.FIELD_SIGN_TYPE)), config);
	}

	/**
	 * 向 入参 Map 中添加 appid、mch_id、nonce_str、sign_type、sign <br>
	 * 该函数适用于商户适用于统一下单等接口，不适用于红包、代金券接口
	 *
	 * @param reqData
	 * @param signType
	 *            指定签名类型，若未指定则使用默认签名 {@link WXPayConstants.DEFAULT_SIGNTYPE}
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> fillRequestData(Map<String, String> reqData, SignType signType, WXPayConfig config) throws Exception {
		reqData.put("appid", config.getAppID());
		reqData.put("mch_id", config.getMchID());
		reqData.put("nonce_str", WXPayUtil.generateNonceStr());
		if (signType == null) {
			reqData.put(WXPayConstants.FIELD_SIGN_TYPE, WXPayConstants.DEFAULT_SIGNTYPE);
		} else {
			reqData.put(WXPayConstants.FIELD_SIGN_TYPE, signType.getName());
		}
		reqData.put(WXPayConstants.FIELD_SIGN, WXPayUtil.generateSignature(reqData, config.getKey(), signType));
		return reqData;
	}

	/**
	 * 微信支付标准签名算法。若 data 和 signType 都未指定签名类型，将使用 {@link WXPayConstants.DEFAULT_SIGNTYPE}进行签名。
	 *
	 * @param data
	 *            待签名数据
	 * @param key
	 *            API密钥
	 * @param signType
	 *            指定data参数未指定签名类型时所使用的签名类型
	 * @return 签名
	 */
	public static String generateSignature(final Map<String, String> data, String key, SignType signType) throws Exception {
		return generateSignature(data, WXPayConstants.FIELD_SIGN_TYPE, key, signType);
	}

	/**
	 * 微信支付标准签名算法。若 data 和 signType 都未指定签名类型，将使用 {@link WXPayConstants.DEFAULT_SIGNTYPE}进行签名。
	 *
	 * @param data
	 *            待签名数据
	 * @param signTypeField
	 *            签名类型字段名
	 * @param key
	 *            API密钥
	 * @param signType
	 *            指定data参数未指定签名类型时所使用的签名类型
	 * @return 签名
	 */
	public static String generateSignature(final Map<String, String> data, String signTypeField, String key, SignType signType) throws Exception {
		String st = data.get(signTypeField);
		if (st == null) {
			if (signType == null) {
				st = WXPayConstants.DEFAULT_SIGNTYPE;
			} else {
				st = signType.getName();
			}
		}
		Set<String> keySet = data.keySet();
		String[] keyArray = keySet.toArray(new String[keySet.size()]);
		Arrays.sort(keyArray);
		StringBuilder sb = new StringBuilder();
		for (String k : keyArray) {
			if (k.equals(WXPayConstants.FIELD_SIGN)) {
				continue;
			}
			if (data.get(k).trim().length() > 0) // 参数值为空，则不参与签名
				sb.append(k).append("=").append(data.get(k).trim()).append("&");
		}
		sb.append("key=").append(key);
		if (WXPayConstants.MD5.equals(st)) {
			return MD5(sb.toString()).toUpperCase();
		} else if (WXPayConstants.HMACSHA256.equals(st)) {
			return HMACSHA256(sb.toString(), key);
		} else {
			throw new Exception(String.format("Invalid sign_type: %s", st));
		}
	}

	/**
	 * 生成 MD5
	 *
	 * @param data
	 *            待处理数据
	 * @return 返回大写MD5加密结果
	 */
	public static String MD5(String data) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] array = md.digest(data.getBytes("UTF-8"));
		StringBuilder sb = new StringBuilder();
		for (byte item : array) {
			sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * 生成 HMACSHA256
	 * 
	 * @param data
	 *            待处理数据
	 * @param key
	 *            密钥
	 * @return 加密结果
	 * @throws Exception
	 */
	public static String HMACSHA256(String data, String key) throws Exception {
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
		sha256_HMAC.init(secret_key);
		byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
		StringBuilder sb = new StringBuilder();
		for (byte item : array) {
			sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * 退款通知 req_info 字段值解密
	 * 
	 * @param reqInfo
	 * @param key
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("restriction")
	public static String refundNotifyDecode(String reqInfo, String key) throws Exception {
		key = WXPayUtil.MD5(key).toLowerCase();
		SecretKeySpec _key = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, _key);
		sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
		return new String(cipher.doFinal(decoder.decodeBuffer(reqInfo)), "UTF-8");
	}

	/**
	 * 获取当前时间戳，单位秒
	 * 
	 * @return
	 */
	public static long getCurrentTimestamp() {
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * 获取随机字符串 Nonce Str
	 *
	 * @return String 随机字符串
	 */
	public static String generateNonceStr() {
		char[] nonceChars = new char[32];
		for (int index = 0; index < nonceChars.length; ++index) {
			nonceChars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
		}
		return new String(nonceChars);
	}

	private static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		documentBuilderFactory.setXIncludeAware(false);
		documentBuilderFactory.setExpandEntityReferences(false);

		return documentBuilderFactory.newDocumentBuilder();
	}

	private static Document newDocument() throws ParserConfigurationException {
		return newDocumentBuilder().newDocument();
	}
}
