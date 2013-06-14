/**
 * 
 */
package com.tooyoung.web.auth;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 */
public interface AuthRequest {

	String getHeader(String name);

	List<String> getHeaders(String name);

	Iterable<String> getHeaderNames();
	
	/**
	 * 根据name获取参数
	 * 包括 query parameter，post parameter， delete中的body parameter,以及 multipart请求的body参数 
	 * @param name
	 * @return
	 */
	String getParameter(String name);

	String getSource();
	
	Object getAttribute(String name);
	
	void setAttribute(String name,Object value);

	/**
	 * 客户的真实ip 如果是通过代理，需要通过代理设置的Header获取客户端真实ip
	 * @return
	 */
	String getRemoteIp();

	List<NameValuePair<String, String>> getCookies();

	String getCookie(String name);

	/**
	 * request 的绝对路径，包括 scheme，host，port,path。不包括 query。
	 * @return
	 */
	String getURL();

	String getMethod();
	
	
    /**
     * Returns the part of this request's URL from the protocol
     * name up to the query string in the first line of the HTTP request.
     * The web container does not decode this String.
     * For example:
     *
     * <table summary="Examples of Returned Values">
     * <tr align=left><th>First line of HTTP request      </th>
     * <th>     Returned Value</th>
     * <tr><td>POST /some/path.html HTTP/1.1<td><td>/some/path.html
     * <tr><td>GET http://foo.bar/a.html HTTP/1.0
     * <td><td>/a.html
     * <tr><td>HEAD /xyz?a=b HTTP/1.1<td><td>/xyz
     * </table>
     *
     * <p>To reconstruct an URL with a scheme and host, use
     * {@link HttpUtils#getRequestURL}.
     *
     * @return a <code>String</code> containing the part of the URL
     * from the protocol name up to the query string
     *
     * @see HttpUtils#getRequestURL
     */
	String getRequestURI();
	
	/**
	 * api路径 从 request path 中排除版本前缀以及后缀
	 * @return
	 */
	String getApiPath();

	/**
	 * parameter map.
	 * 包括 query parameters 和 post 的 form parameter
	 * 如果是 delete method，同时包括 body中的 参数
	 * 不包括multipart中的参数
	 * @return
	 */
	Map<String, String[]> getParameterMap();
	
	/**
	 * 获取multipart中的 String类型参数
	 * 如果请求不是multipart，则返回空 Map
	 * @return
	 */
	Map<String,String[]> getMultipartParameterMap();

	String getCharacterEncoding();

	boolean isMultiPart();
	
    /**
     * Returns the name of the scheme used to make this request, 
     * for example,
     * <code>http</code>, <code>https</code>, or <code>ftp</code>.
     * Different schemes have different rules for constructing URLs,
     * as noted in RFC 1738.
     *
     * @return		a <code>String</code> containing the name 
     *			of the scheme used to make this request
     *
     */
	String getScheme();
	
	/**
	 * 判断Scheme是否为https。同时通过 Http头部的
	 * X-Proto: SSL 判断。
	 * 因为后端服务器和前端代理之间可能走的是http，前端代理和客户端之间是https。代理会在头部增加
	 * X-Proto: SSL 作为标志。
	 * @return
	 */
	boolean isHttps();

}
