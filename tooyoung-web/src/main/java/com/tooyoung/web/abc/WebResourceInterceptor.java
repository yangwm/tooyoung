package com.tooyoung.web.abc;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteAccessor;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.representation.Form;

/**
 * 
 * 
 */
public class WebResourceInterceptor extends RemoteAccessor implements MethodInterceptor, InitializingBean {

	private final static Logger log = Logger.getLogger(WebResourceInterceptor.class);

	private final static Random random = new Random();

	private WebResourceFactory webResourceFactory;

	private List<String> serviceUrlList;

	@Override
	public void afterPropertiesSet() {

		if (serviceUrlList == null || serviceUrlList.size() == 0) {
			throw new IllegalArgumentException("Property 'serviceUrlList' is required");
		}

		if (webResourceFactory == null) {
			throw new IllegalArgumentException("Property 'webResourceFactory' is required");
		}
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {

		Method method = invocation.getMethod();
		Object[] args = invocation.getArguments();

		WebResource resource = getWebResource().path(method.getName());

		Builder builder = resource.accept(MediaType.APPLICATION_JSON_TYPE)
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).header(LogUtils.HEADER_LOGID, LogUtils.getLogid());

		long start = System.currentTimeMillis();

		try {
			if (args == null) {
				return builder.post(method.getReturnType());
			}

			Form form = new Form();

			for (int i = 0; i < args.length; i++) {
				Object value = args[i];

				if (value == null)
					continue;

				if (value instanceof Collection) {
					Collection<?> col = (Collection<?>) value;

					for (Object o : col) {
						form.add("P" + i + "[]", o);
					}

				} else if (value instanceof Array) {
					int len = Array.getLength(value);

					for (int j = 0; j < len; j++) {
						form.add("P" + i + "[]", Array.get(value, j));
					}

				} else if (value instanceof Date) {
					Date date = (Date) value;

					form.add("P" + i, date.toGMTString());
				} else {
					form.add("P" + i, value);
				}
			}

			return builder.post(method.getReturnType(), form);

		} catch (UniformInterfaceException e) {

			int status = e.getResponse().getStatus();

			// empty content
			if (status == 204) {
				return null;
			}

			// FxRemoteException
			if (status >= 1000) {
				log.debug("FxRemoteException status: " + status);
				throw new FxBusinessException(status);
			}

			log.error("UniformInterfaceException: " + e.getMessage());
			throw new FxBusinessException(Status.INTERNAL_SERVER_ERROR);
		} catch (ClientHandlerException e) {
			log.error("Invoke " + resource + " " + e.getMessage(), e);
			throw new FxBusinessException(Status.INTERNAL_SERVER_ERROR);
		} finally {
			log.info("Invoke " + resource + " time(" + (System.currentTimeMillis() - start) + ")");
		}

	}

	private WebResource getWebResource() {

		int index = 0;

		if (serviceUrlList.size() > 1) {
			index = random.nextInt(serviceUrlList.size());
		}

		return webResourceFactory.create(serviceUrlList.get(index));
	}

	public void setWebResourceFactory(WebResourceFactory webResourceFactory) {
		this.webResourceFactory = webResourceFactory;
	}

	public void setServiceUrlList(List<String> serviceUrlList) {
		this.serviceUrlList = serviceUrlList;
	}

}
