<%@ page contentType="text/html;charset=utf-8"%>
<%@ page isErrorPage="true" %>
<%@ page import="com.tooyoung.common.util.ApiLogger"%>
<%
    response.setStatus(500);
    String orginal_uri = (String)request.getAttribute("javax.servlet.forward.request_uri");
    ApiLogger.warn(orginal_uri+" Error 500: ",exception);

    if(orginal_uri.endsWith("json")){
        response.getWriter().print("{\"request\":\""+orginal_uri+"\",\"error_code\":\"500\",\"error\":\"50001:Error: system error!\"}");
    }else{
        response.getWriter().print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><hash><request>"+orginal_uri+"</request><error_code>500</error_code><error>50001:Error: system error!</error></hash>");
    }

%>