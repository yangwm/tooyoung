package com.tooyoung.web.base;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 *
 *  在 WebServlet的 service方法之前之后分别调用 before,after
 *  实现类必须在spring配置中注册自己的bean
 */
public interface WebServletFilter {
	/**
	 * 请求之前
	 * @param request
	 */
	void before(HttpServletRequest request);
	
	/**
	 * 请求之后
	 * @param request
	 */
	void after(HttpServletRequest request);
}
