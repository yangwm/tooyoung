/**
 * 
 */
package cc.tooyoung.common.util;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Convert String ip to uint32 and vise versa
 * 
 * IP should stored as integer to save space
 * 
 * just borrow the code from http://teneo.wordpress.com/2008/12/23/java-ip-address-to-integer-and-back/
 * 
 * @author yangwm
 *
 */
public class IpUtil {
	
	// ------- convert --------------- 
	
	public static String intToIp(int i) {
        return ((i >> 24 ) & 0xFF) + "." +
               ((i >> 16 ) & 0xFF) + "." +
               ((i >>  8 ) & 0xFF) + "." +
               ( i        & 0xFF);
    }

	public static int ipToInt(final String addr) {
		final String[] addressBytes = addr.split("\\.");

		int ip = 0;
		for (int i = 0; i < 4; i++) {
			ip <<= 8;
			ip |= Integer.parseInt(addressBytes[i]);
		}
		return ip;
	}
	
	public static long ipToLong(final String addr) {
		if (null == addr) {
			return 0L;
		}
		final String[] addressBytes = addr.split("\\.");

		long ip = 0L;
		try {
			for (int i = 0; i < 4; i++) {
				ip <<= 8;
				ip |= Long.parseLong(addressBytes[i]);
			}
		} catch (final NumberFormatException nfe) {
			return 0L;
		}
		
		return ip;
	}
	
	public static String longToIp(long i) {
		 return ((i >> 24 ) & 0xFF) + "." +
	            ((i >> 16 ) & 0xFF) + "." +
	            ((i >>  8 ) & 0xFF) + "." +
	            ( i         & 0xFF);
	}
	
	// ------- get info --------------- 
	
	private static final String ipRegix="((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
	private static final Pattern ipPattern =Pattern.compile(ipRegix);
	
	public static boolean isIp(String in){
		if(in==null){
			return false;
		}
		return ipPattern.matcher(in).matches();
	}
	
	 /**
	  * 获取本机所有ip 返回map key为网卡名 value为对应ip yuanming@staff
	  * 
	  * @return
	  */
	public static Map<String,String> getLocalIps(){
		try {
			Map<String,String> result = new HashMap<String,String>();
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				String name = ni.getName();
				String ip = "";
				Enumeration<InetAddress> ips = ni.getInetAddresses();
				while (ips.hasMoreElements()) {
					InetAddress address = ips.nextElement();
					if(address instanceof Inet4Address){
						ip = address.getHostAddress();
						break;
					}
				}
				result.put(name, ip);
			}
			return result;
		} catch (SocketException e) {
			ApiLogger.error("getLocalIP error",e);
			return Collections.emptyMap();
		}
	}
	 
	/**
	 * 获取服务器ip 判断规则  eth0 > eth1 > ... ethN > wlan > lo
	 * 
	 * yuanming@staff
	 * @return
	 */
	public static String getLocalIp() {
		
		Map<String,String> ips = getLocalIps();
		List<String> faceNames = new ArrayList<String>(ips.keySet());
		Collections.sort(faceNames);
		
		for(String name:faceNames){
			if("lo".equals(name)){
				continue;
			}
			String ip = ips.get(name);
			if(!StringUtils.isBlank(ip)){
				return ip;
			}
		}
		return "127.0.0.1";
	}
	
	private static String localIp = null;
	
	/**
	 * 只获取一次ip
	 * @return 
	 */
	public static String getSingleLocalIp(){
		if(localIp == null){
			localIp = getLocalIp();
		}
		return localIp;
	}
	
	
	private static final int MIN_USER_PORT_NUMBER = 1024;
	private static final int MAX_USER_PORT_NUMBER = 65536;
	
	/**
	 * 随机返回可用端口
	 * 
	 * @return
	 */
	public static int ramdomAvailablePort(){
		int port = 0;
		do{
			port = (int) ((MAX_USER_PORT_NUMBER-MIN_USER_PORT_NUMBER) * Math.random())+MIN_USER_PORT_NUMBER;
		}while(!availablePort(port));
		return port;
	}
	

	
	/**
	 * 检测该端口是否可用 <br/>
	 * 端口必须大于 0 小于 {@value #MAX_PORT_NUMBER}
	 * @param port
	 * @return
	 */
	public static boolean availablePort(int port) {
	    if (port < 0 || port > MAX_USER_PORT_NUMBER) {
	        throw new IllegalArgumentException("Invalid port: " + port);
	    }

	    ServerSocket ss = null;
	    DatagramSocket ds = null;
	    try {
	        ss = new ServerSocket(port);
	        ss.setReuseAddress(true);
	        ds = new DatagramSocket(port);
	        ds.setReuseAddress(true);
	        return true;
	    } catch (IOException e) {
	    } finally {
	        if (ds != null) {
	            ds.close();
	        }

	        if (ss != null) {
	            try {
	                ss.close();
	            } catch (IOException e) {
	                /* should not be thrown */
	            }
	        }
	    }

	    return false;
	}
	
	public static void main(String[] args) {
		int a = ipToInt("127.0.0.1");
		System.out.println(intToIp(a));
		
		String ip = null;
		long longIp = ipToLong(ip);
		System.out.println(longIp);
		System.out.println(longToIp(longIp));
		
	}
}
