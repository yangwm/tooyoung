package com.tooyoung.common.util.codec;


import java.security.MessageDigest;

public class MD5Utils {

	private static ThreadLocal<MessageDigest> MD5 = new ThreadLocal<MessageDigest>() {
		@Override
		protected MessageDigest initialValue() {
			try {
				return MessageDigest.getInstance("MD5");
			} catch (Exception e) {
			}
			return null;
		}
	};

	public static String md5(String src) {
		return md5(src.getBytes());
	}

	public static String md5(byte[] bytes) {
		MessageDigest md5 = MD5.get();
		md5.reset();
		md5.update(bytes);
		byte[] digest = md5.digest();
		return encodeHex(digest);
	}

	public static String encodeHex(byte[] bytes) {
		StringBuilder buf = new StringBuilder(bytes.length + bytes.length);
		for (int i = 0; i < bytes.length; i++) {
			if (((int) bytes[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) bytes[i] & 0xff, 16));
		}
		return buf.toString();
	}
	public static void main(String[] args){
		System.out.println(MD5Utils.md5("12312312312312"));
	}
	
	public static String md5(long[] array){
		return md5(long2Byte(array));
	}
	
	public static byte[] long2Byte(long[] longArray) {
		byte[] byteArray = new byte[longArray.length * 8];
		for (int i = 0; i < longArray.length; i++) {
			byteArray[0 + 8 * i] = (byte) (longArray[i] >> 56);
			byteArray[1 + 8 * i] = (byte) (longArray[i] >> 48);
			byteArray[2 + 8 * i] = (byte) (longArray[i] >> 40);
			byteArray[3 + 8 * i] = (byte) (longArray[i] >> 32);
			byteArray[4 + 8 * i] = (byte) (longArray[i] >> 24);
			byteArray[5 + 8 * i] = (byte) (longArray[i] >> 16);
			byteArray[6 + 8 * i] = (byte) (longArray[i] >> 8);
			byteArray[7 + 8 * i] = (byte) (longArray[i] >> 0);
		}
		return byteArray;
	}
}
