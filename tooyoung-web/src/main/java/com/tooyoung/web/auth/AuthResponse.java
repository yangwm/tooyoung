/**
 * 
 */
package com.tooyoung.web.auth;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * 
 * 
 * 
 */
public class AuthResponse implements Serializable{

	public static enum UserType{
		/**
		 * 普通用户
		 */
		normal,
		/**
		 * 内网用户
		 */
		internal
	}
	
	/**
	 * app 的 资源权限
	 *
	 */
	public static enum Permission{
		/**
		 * 读取帐号基本信息权限
		 */
		READ_ACCOUNT_BASIC,
		/**
		 * 更新帐号基本信息权限
		 */
		UPDATE_ACCOUNT_BASIC,
		/**
		 * 获取 帐号 联系方式权限
		 */
		READ_ACCOUNT_CONTACT,
		/**
		 * 更新帐号联系方式权限
		 */
		UPDATE_ACCOUNT_CONTACT,
		
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5453139780159342985L;
	
	private final AuthSource authSource;
	private final long uid;
	private final Map<String, Object> attributes;
	private final String ip;
	private final String authedBy;
	private final UserType userType;
	private int loginStat = 0 ;

	public AuthResponse(AuthSource authSource, long uid,String ip,UserType userType,String authedBy) {
		this.authSource = authSource;
		this.uid = uid;
		this.attributes = new HashMap<String, Object>();
		this.ip = ip;
		this.authedBy = authedBy;
		this.userType = userType;
	}

	public AuthSource getAuthSource() {
		return authSource;
	}

	public long getUid() {
		return uid;
	}

	public void setAttribute(String name, Object value) {
		this.attributes.put(name, value);
	}

	public Object getAttribute(String name) {
		return this.attributes.get(name);
	}

	public Set<Entry<String, Object>> getAttributes() {
		return attributes.entrySet();
	}
	
	public String getIp(){
		return ip;
	}

	public String getAuthedBy() {
		return authedBy;
	}

	public UserType getUserType(){
		return userType;
	}

	public int getLoginStat() {
		return loginStat;
	}

	public void setLoginStat(int loginStat) {
		this.loginStat = loginStat;
	}
	
	public int getPermissionLevel(Permission p){
		return 0; // TODO 
	}
}
