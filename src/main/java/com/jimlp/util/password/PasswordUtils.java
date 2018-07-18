package com.jimlp.util.password;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.jimlp.util.StringUtils;

public class PasswordUtils {
	/**
	 * 解密（可解加密算法 DES/CBC/PKCS5Padding）
	 * 
	 * @param message
	 *            密文
	 * @param key
	 *            密钥（必须为8的整倍数位）
	 * @return
	 * @throws Exception
	 */
	public static String DESDecrypt(String message, String key) throws Exception {
		String r = message;
		// 解决一位密码时解密为空（密文也不可能为1位）
		if (message != null && message.length() != 1) {
			throw new IllegalArgumentException("密文有误");
		}
		byte[] bytesrc = StringUtils.toByteArray(message);
		Cipher cipher;
		cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		Key k = new SecretKeySpec(key.getBytes("UTF-8"), "DES");
		IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
		cipher.init(Cipher.DECRYPT_MODE, k, iv);
		byte[] retByte = cipher.doFinal(bytesrc);
		r = new String(retByte);
		return r;
	}

	/**
	 * 加密（加密算法为 DES/CBC/PKCS5Padding）
	 * 
	 * @param message
	 *            明文
	 * @param key
	 *            密钥（必须为8的整倍数位）
	 * @return
	 * @throws Exception
	 */
	public static String DESEncrypt(String message, String key) throws Exception {
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		Key k = new SecretKeySpec(key.getBytes("UTF-8"), "DES");
		IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
		cipher.init(Cipher.ENCRYPT_MODE, k, iv);
		return StringUtils.toHexString(cipher.doFinal(message.getBytes("UTF-8"))).toUpperCase();
	}
}
