/**
 * 
 */
package com.tooyoung.web.abc;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * 
 * 
 */
public class WebResourceClientFilter extends ClientFilter {

	private final static Logger log = Logger.getLogger(WebResourceClientFilter.class);

	private final static String DATE_HEADER = "Date";
	private final static String AUTH_HEADER = "Authorization";

	private String token;

	public WebResourceClientFilter(String token) {

		this.token = token;
	}

	@Override
	public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {

		addAuthHeader(cr);

		return getNext().handle(cr);
	}

	private void addAuthHeader(ClientRequest cr) {

		long time = System.currentTimeMillis();
		String path = cr.getURI().getPath();
		String method = cr.getMethod();

		String md5 = CodecUtils.md5Hex(path + method + time + token);

		cr.getHeaders().putSingle(DATE_HEADER, time);
		cr.getHeaders().putSingle(AUTH_HEADER, md5);
	}
}
