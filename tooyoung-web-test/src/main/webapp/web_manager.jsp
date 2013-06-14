<%@ page contentType="text/html; charset=utf-8" language="java"%>

<%@ page import="com.tooyoung.common.mcq.McqBaseManager" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.apache.commons.lang.StringUtils"%>

<%!
	private static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
%>

<%
	String ip = getIpAddr(request);
	
	if (ip == null 
		|| !(ip.startsWith("192.") || ip.equals("127.0.0.1"))
		) {
		System.out.println(" bad ip:" + ip);
		return;
	}

	String type = request.getParameter("type");
	if ("start_queue".equals(type)) {
		McqBaseManager.startReadAll();
		response.getWriter().println(McqBaseManager.status());
    } else if ("stop_queue".equals(type)) {
		McqBaseManager.stopReadAll();
		response.getWriter().println(McqBaseManager.status());
	} 
	
	else {
		response.getWriter().println("other");
	}
 %>