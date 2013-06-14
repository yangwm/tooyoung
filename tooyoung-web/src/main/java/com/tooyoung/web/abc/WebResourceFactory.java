/**
 * 
 */
package com.tooyoung.web.abc;

import org.springframework.beans.factory.InitializingBean;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

/**
 * 
 * 
 */
public class WebResourceFactory implements InitializingBean {

	private int connectionTimeout = 1000;

	private int readTimeout = 1000;

	// private int threadPoolSize = 10;

	private String authKey = null;

	private Client client;

	@Override
	public void afterPropertiesSet() throws Exception {

		ClientConfig cc = new DefaultClientConfig();

		cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

		cc.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, connectionTimeout);
		cc.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, readTimeout);
		// cc.getProperties().put(ClientConfig.PROPERTY_THREADPOOL_SIZE, threadPoolSize);

		cc.getClasses().add(JacksonMapperProvider.class);

		client = Client.create(cc);

		if (authKey != null) {
			client.addFilter(new WebResourceClientFilter(authKey));
		}

	}

	public WebResource create(String serviceUrl) {

		return client.resource(serviceUrl);
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	// public void setThreadPoolSize(int threadPoolSize) {
	// this.threadPoolSize = threadPoolSize;
	// }

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

}
