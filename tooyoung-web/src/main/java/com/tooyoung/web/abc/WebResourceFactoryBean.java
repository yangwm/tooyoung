/**
 * 
 */
package com.tooyoung.web.abc;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * 
 * 
 */
public class WebResourceFactoryBean extends WebResourceInterceptor implements FactoryBean<Object> {

	private Object serviceProxy;

	public void afterPropertiesSet() {
		super.afterPropertiesSet();

		this.serviceProxy = new ProxyFactory(getServiceInterface(), this).getProxy(getBeanClassLoader());
	}

	@Override
	public Object getObject() throws Exception {

		return this.serviceProxy;
	}

	@Override
	public Class<?> getObjectType() {

		return getServiceInterface();
	}

	@Override
	public boolean isSingleton() {

		return true;
	}

}
