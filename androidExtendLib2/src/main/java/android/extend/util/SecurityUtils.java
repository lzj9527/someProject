package android.extend.util;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class SecurityUtils
{
	public static String toMD5(String value) throws Exception
	{
		return toMD5(value.getBytes());
	}

	// public static String toMD5(String value, Charset charset) throws UnsupportedCharsetException, Exception
	// {
	// return toMD5(value.getBytes(charset));
	// }

	public static String toMD5(String value, String charsetName) throws UnsupportedEncodingException, Exception
	{
		return toMD5(value.getBytes(charsetName));
	}

	public static String toMD5(byte[] bytes) throws Exception
	{
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.reset();
		md.update(bytes);
		bytes = md.digest();
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes)
		{
			if (Integer.toHexString(0xFF & b).length() == 1)
			{
				hexString.append("0").append(Integer.toHexString(0xFF & b));
			}
			else
			{
				hexString.append(Integer.toHexString(0xFF & b));
			}
		}
		return hexString.toString();
	}

	/**
	 * key必须为8位或者8的倍数
	 * */
	private static final byte[] DES_IV = { 1, 2, 3, 4, 5, 6, 7, 8 };
	private static final String DES_ALGORITHM = "DES/CBC/PKCS5Padding";

	public static byte[] encodeDes(String key, byte[] data) throws Exception
	{
		DESKeySpec dks = new DESKeySpec(key.getBytes());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		Key secretKey = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance(DES_ALGORITHM);
		IvParameterSpec iv = new IvParameterSpec(DES_IV);
		AlgorithmParameterSpec paramSpec = iv;
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
		return cipher.doFinal(data);
	}

	public static byte[] decodeDes(String key, byte[] data) throws Exception
	{
		DESKeySpec dks = new DESKeySpec(key.getBytes());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		Key secretKey = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance(DES_ALGORITHM);
		IvParameterSpec iv = new IvParameterSpec(DES_IV);
		AlgorithmParameterSpec paramSpec = iv;
		cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
		return cipher.doFinal(data);
	}
}
