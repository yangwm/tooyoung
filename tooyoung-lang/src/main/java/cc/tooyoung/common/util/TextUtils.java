/**
 * 
 */
package cc.tooyoung.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class TextUtils {

	private static final Pattern urlPattern = Pattern
			.compile(
					new StringBuilder()
							.append("((?:(http|https):")
							.append("\\/\\/(?:(?:[a-z0-9\\$\\-\\_\\.\\+\\!\\*\\(\\)")
							.append("\\,\\;\\?\\&\\=]|(?:\\%[a-f0-9]{2})){1,64}(?:\\:(?:[a-z0-9\\$\\-\\_")
							.append("\\.\\+\\!\\*\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-f0-9]{2})){1,25})?\\@)?)")
							.append("(?:(?:[a-z0-9][a-z0-9\\-]{0,64}\\.)+")
							// named host
							.append(")")

							.append("(?:\\:\\d{1,5})?)")
							// plus option port number
							.append("((?:(?:[a-z0-9\\;\\/\\?\\:\\@\\&\\=\\#\\~")
							// plus option query params
							.append("\\-\\.\\+\\!\\*\\(\\)\\,\\_])|(?:\\%[a-f0-9]{2}))*)?")
							.append("(?:\\b|$)").append("?(?:\\/)*").toString(),
					Pattern.CASE_INSENSITIVE); // 不区分大小写
	private static final Pattern pattern1 = Pattern
			.compile(
					new StringBuilder()
							.append("((?:(http|https|Http|Https|rtsp|Rtsp):")
							.append("\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)")
							.append("\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_")
							.append("\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?")
							.append("((?:(?:[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}\\.)+")
							// named host
							.append("(?:")
							// plus top level domain
							.append("(?:aero|arpa|asia|a[cdefgilmnoqrstuwxz])")
							.append("|(?:biz|b[abdefghijmnorstvwyz])")
							.append("|(?:cat|com|coop|c[acdfghiklmnoruvxyz])")
							.append("|d[ejkmoz]")
							.append("|(?:edu|e[cegrstu])")
							.append("|f[ijkmor]")
							.append("|(?:gov|g[abdefghilmnpqrstuwy])")
							.append("|h[kmnrtu]")
							.append("|(?:info|int|i[delmnoqrst])")
							.append("|(?:jobs|j[emop])")
							.append("|k[eghimnrwyz]")
							.append("|l[abcikrstuvy]")
							.append("|(?:mil|mobi|museum|m[acdeghklmnopqrstuvwxyz])")
							.append("|(?:name|net|n[acefgilopruz])")
							.append("|(?:org|om)")
							.append("|(?:pro|p[aefghklmnrstwy])")
							.append("|qa")
							.append("|r[eouw]")
							.append("|s[abcdeghijklmnortuvyz]")
							.append("|(?:tel|travel|t[cdfghjklmnoprtvwz])")
							.append("|u[agkmsyz]")
							.append("|v[aceginu]")
							.append("|w[fs]")
							.append("|y[etu]")
							.append("|z[amw]))")
							.append("|(?:(?:25[0-5]|2[0-4]")
							// or ip address
							.append("[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(?:25[0-5]|2[0-4][0-9]")
							.append("|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1]")
							.append("[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}")
							.append("|[1-9][0-9]|[0-9])))")
							.append("(?:\\:\\d{1,5})?)")
							// plus option port number
							.append("((?:\\/)*(?:(?:[a-zA-Z0-9\\;\\/\\?\\:\\@\\&\\=\\#\\~")
							// plus option query params
							.append("\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?")
							.append("(?:\\b|$)").append("?(?:\\/)*").toString(),
					Pattern.CASE_INSENSITIVE); // 不区分大小写

	public static boolean containsUrl(String text) {
		return containsUrl(text, false);
	}

	public static boolean containsUrl(String text, boolean newRegex) {
		return collectUrl(text, newRegex).size() > 0;
	}

	public static List<String> collectUrl(String text) {
		return collectUrl(text, false);
	}

	public static List<String> collectUrl(String text, boolean newRegex) {
		List<String> ret = new ArrayList<String>();
		Matcher matcher = newRegex ? urlPattern.matcher(patchRegx(text))
				: pattern1.matcher(patchRegx(text));
		while (matcher.find()) {
			ret.add(matcher.group(0));
		}
		return ret;
	}

	private static String patchRegx(String src) {
		char[] chars = src.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] > 0x80) {
				chars[i] = ' ';
			}
		}
		return new String(chars);
	}
	/**
	 * 计算文本长度
	 * code  0~255之间的算1个字符，其他的算2个字
	 * @param text
	 * @return
	 */
	public static int getLength(String text){
		if (text == null || text.length() == 0) {
			return 0;
		}
		int len = text.length();
		int index = 0;
		char c;
		for (int i = 0; i < len; i++) {
			c = text.charAt(i);
			if ((c > 0xFF)) {
				index += 2;
			} else {
				index++;
			}
		}
		return index;
	}
	
	public static boolean isShortUrl(String url) {
		if (url == null) {
			return false;
		}
		if(!url.startsWith("http://")&&!url.startsWith("https://")){
			return true;
		}
		if (url.length()<=41
				&&(url.startsWith("http://t.sina.com/")
				||url.startsWith("http://sinaurl.cn/")
				||url.startsWith("http://t.sina.cn/")
				||url.startsWith("http://t.cn/")
				||url.startsWith("http://weibo.com/"))) {
			return true;
		}
		return false;
	}
}
