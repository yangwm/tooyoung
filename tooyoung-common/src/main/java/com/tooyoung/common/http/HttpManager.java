package com.tooyoung.common.http;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 *
 */
public class HttpManager {
	private static Set<String> blockResources = new HashSet<String>();
	
//	private static String r1 = "http://i.t.sina.com.cn";
//	private static String r2 = "http://data.i.t.sina.com.cn";
//	private static String r3 = "http://recom.i.t.sina.com.cn";

	public static void addBlockResource(String r){
		if(r == null || r.length()<6){//http://
			return;
		}
		blockResources.add(r);
	}
	public static void removeBlockResource(String r){
		blockResources.remove(r);
	}
	public static boolean isBlockResource(String url){
		if(url == null){
			return true;
		}
		for(String br : blockResources){
			if(url.startsWith(br)){
				return true;
			}
		}
		return false;
	}
	public static Set<String> getBlockResources(){
		return blockResources;
	}
	

}
