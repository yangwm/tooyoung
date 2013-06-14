package com.tooyoung.web.auth;

import java.io.Serializable;

/**
 * 
 * 
 * @author yangwm May 24, 2013 11:05:11 AM
 */
public class AuthSource implements Serializable {
    
	/**
     * 
     */
    private static final long serialVersionUID = 4548937850181997459L;
    
    private int appId;
	private String appName;
	private String appUrl;

	private String appKey;
	private String appSecret;
	
    public int getAppId() {
        return appId;
    }
    public void setAppId(int appId) {
        this.appId = appId;
    }
    public String getAppName() {
        return appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }
    public String getAppUrl() {
        return appUrl;
    }
    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }
    public String getAppKey() {
        return appKey;
    }
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
    public String getAppSecret() {
        return appSecret;
    }
    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

}
