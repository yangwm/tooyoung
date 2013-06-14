/**
 * 
 */
package com.tooyoung.web.abc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

/**
 * 
 */
public abstract class CodecUtils {

	public static class MD5 {
		public long high;
		public long low;
	}

	public static MD5 md5(String data) {

		byte[] b = DigestUtils.md5(StringUtils.getBytesUtf8(data));

		MD5 md5 = new MD5();

		for (int i = 0; i < 8; i++) {
			md5.high += b[i] << (1 << i);
		}

		for (int i = 8; i < 16; i++) {
			md5.low += b[i] << (1 << i - 8);
		}

		return md5;
	}

	public static String md5Hex(String data) {

		return DigestUtils.md5Hex(StringUtils.getBytesUtf8(data));
	}

	public static String md5Hex(InputStream input) throws IOException {

		return DigestUtils.md5Hex(input);
	}

	public static String md5Hex(File file) throws IOException {

		InputStream input = FileUtils.openInputStream(file);

		try {
			return md5Hex(input);
		} finally {
			input.close();
		}
	}

	public static byte[] base64Encode(byte[] data) {

		return Base64.encodeBase64URLSafe(data);
	}

	public static String base64EncodeString(byte[] data) {

		return Base64.encodeBase64URLSafeString(data);
	}

	public static byte[] base64Decode(byte[] data) {

		return Base64.decodeBase64(data);
	}

	public static byte[] base64Decode(String data) {

		return Base64.decodeBase64(data);
	}

}
