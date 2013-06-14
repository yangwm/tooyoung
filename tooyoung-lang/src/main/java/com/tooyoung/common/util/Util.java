/**
 * 
 */
package com.tooyoung.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author yangwm May 18, 2013 4:27:34 PM
 */
public class Util {
    
    public static String toStr(byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error byte[] to String => " + e);
        }
    }
    public static byte[] toBytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Error serializing String:" + str + " => " + e);
        }
    }
	/**
	 * 将字符串转为double数字，如果格式不正常则为 0.0
	 * @param src
	 * @return
	 */
	public static double convertDouble(String src) {
		return convertDouble(src, 0.0);
	}

	/**
	 * 将字符串转为double数字，如果格式不正常则为 defaultValue
	 * @param src
	 * @param defaultValue
	 * @return
	 */
	public static double convertDouble(String src, double defaultValue) {
		try {
			return Double.parseDouble(src);
		} catch (Exception e) {
		}
		return defaultValue;
	}
	
	public static int convertInt(Object obj) {
		return convertInt(obj, 0);
	}
	
	public static int convertInt(Object obj, int defaultValue) {
		if(obj == null){
			return defaultValue;
		}
		try {
			return Integer.parseInt(obj.toString());
		} catch (Exception e) {
		}
		return defaultValue;
	}

	public static long convertLong(String src) {
		return convertLong(src, 0);
	}
	
	public static long convertLong(String src, long defaultValue) {
		try {
			return Long.parseLong(src, 10);
		} catch (Exception e) {
		}
		return defaultValue;
	}
	   
    public static void trim(StringBuffer sb,char c){
        if(sb==null){
            return;
        }
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == c) {
            sb.deleteCharAt(sb.length() - 1);
        }
    }

	public static String urlDecoder(String s, String charcoding) {
		if (s == null)
			return null;
		try {
			return URLDecoder.decode(s, charcoding);
		} catch (Exception e) {
		}
		return null;
	}
	
	public static String urlEncoder(String s, String charcoding) {
		if (s == null)
			return null;
		try {
			return URLEncoder.encode(s, charcoding);
		} catch (Exception e) {
		}
		return null;
	}

	public static String toEscChar(String s) {
		//已经转义的先恢复
		s = s.replaceAll("&amp;", "&");
		s = s.replaceAll("&lt;", "<");
		s = s.replaceAll("&gt;", ">");
		s = s.replaceAll("&quot;", "\"");
		
		s = s.replaceAll("&", "&amp;");
		s = s.replaceAll("<", "&lt;");
		s = s.replaceAll(">", "&gt;");
		s = s.replaceAll("\"", "&quot;");
		//和zhulei确认去掉单引号转义，避免显示有问题
		//s = s.replaceAll("'", "&apos;");
		return s;
	}
	/**
	 * 单向html转义下列四个字符： &、<、>、" 
	 * @param s
	 * @return
	 */
	public static String htmlEscapeOnce(String s) {
	    if (StringUtils.isBlank(s)){
	        return s;
	    }
	    s = s.replaceAll("&", "&amp;");
        s = s.replaceAll("<", "&lt;");
        s = s.replaceAll(">", "&gt;");
        s = s.replaceAll("\"", "&quot;");
        //去掉单引号转义，避免显示有问题
        //s = s.replaceAll("'", "&apos;");
        return s;
	}
	/**
     * 单向html反转义下列四个字符： &、<、>、" 
     * @param s
     * @return
     */
	public static String htmlUnEscapeOnce(String s) {
	    if (StringUtils.isBlank(s)){
            return s;
        }
	    s = s.replaceAll("&amp;", "&");
        s = s.replaceAll("&lt;", "<");
        s = s.replaceAll("&gt;", ">");
        s = s.replaceAll("&quot;", "\"");
        //去掉单引号转义，避免显示有问题
        //s = s.replaceAll("'", "&apos;");
        return s;
    }

	/**
	 * 从url获取picid
	 * @param url
	 * @return
	 */
	public static String getPicIdFromUrl(String url) {
		if (url == null)
			return null;
		try {
			if (url.startsWith("http://ww")) {
				int pos1 = url.lastIndexOf("/");
				int pos2 = url.lastIndexOf(".");
				if (pos1 != -1 && pos2 != -1)
					return url.substring(pos1 + 1, pos2);
			} else {
				int pos1 = url.lastIndexOf("/");
				int pos2 = url.lastIndexOf("&");
				if (pos1 != -1 && pos2 != -1)
					return url.substring(pos1 + 1, pos2);
			}
		} catch (Exception ex) {
			ApiLogger.warn("Exception invalid pic url " + url);
		}
		return null;
	}

	public static String getProfileImgUrl(long uid, int size, long iconver, byte gender){
		int genderFlag=(gender==1)?1:0; //男为1，女为0
		return new StringBuilder(32).append("http://tp").append(uid % 4 + 1).append(".img.cn/")
			.append(uid).append("/").append(size).append("/").append(iconver).append("/").append(genderFlag).toString();
	}

	public static long getVersionFromImgUrl(String imageUrl) {
		// http://tpx.sinaimg.cn/10503/50/12345/1  //男为1，女为0
		if (imageUrl == null)
			return 0;
		int len = imageUrl.length();
		int pos1 = imageUrl.lastIndexOf("/");
		if (pos1 == -1)
			return 0;
		try {
			if (len - pos1 == 2) { // old format, no gender field
				int pos2 = imageUrl.lastIndexOf("/",  pos1 - 1);
				String str = imageUrl.substring(pos2 + 1, pos1);
				return Long.parseLong(str);
			} else {
				String str = imageUrl.substring(pos1 + 1);
				return Long.parseLong(str);
			}
		} catch (Exception ex) {
			ApiLogger.info("getVersionFromImgUrl error: " + imageUrl);
		}
		return 0;
	}

	/**
	 * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters
	 * (U+0000 through U+001F).
	 * 
	 * @param s
	 * @return
	 */
	public static String toEscapeJson(String s) {
		if (s == null || s.length() < 1)
			return "";
		StringBuffer sb = new StringBuffer();
		escape(s, sb);
		return sb.toString();
	}

	/**
	 * @param s
	 *            - Must not be null.
	 * @param sb
	 */
	static void escape(String s, StringBuffer sb) {
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '/':
				sb.append("\\/");
				break;
			default:
				// Reference: http://www.unicode.org/versions/Unicode5.1.0/
				if ((ch >= '\u0000' && ch <= '\u001F')
						|| (ch >= '\u007F' && ch <= '\u009F')
						|| (ch >= '\u2000' && ch <= '\u20FF')) {
					String ss = Integer.toHexString(ch);
					sb.append("\\u");
					for (int k = 0; k < 4 - ss.length(); k++) {
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				} else {
					sb.append(ch);
				}
			}
		}// for
	}
	
}
