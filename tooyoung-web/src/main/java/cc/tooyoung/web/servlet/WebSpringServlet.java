/**
 * 
 */
package cc.tooyoung.web.servlet;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import cc.tooyoung.common.util.ApiLogger;
import cc.tooyoung.web.exception.WebApiException;
import cc.tooyoung.web.exception.WebExceptionFormat;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.uri.UriComponent;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;

/**
 * Web Spring Servlet
 * 
 * @author yangwm Jul 3, 2013 10:52:41 PM
 */
public class WebSpringServlet extends SpringServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 5871570123782357252L;
    
    /*
    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        HttpServletRequest request;
        HttpServletResponse response;

        try {
            request = (HttpServletRequest) req;
            response = (HttpServletResponse) res;
        } catch (ClassCastException e) {
            throw new ServletException("non-HTTP request or response");
        }
        this.service(request, response);
    }
    
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (ApiLogger.isDebugEnabled()) {
            ApiLogger.debug("WebSpringServlet service(request, response)");
        }
        
        response.setHeader("Api-Server-IP", request.getLocalAddr());
        try {
            super.service(request, response);
        } catch (WebApiException e) {
            ApiLogger.error("WebSpringServlet service WebApiException warn:" + e.getMessage());
            int status = e.getFactor().getStatus().getStatusCode();
            response.sendError(status, WebExceptionFormat.formatException(e, request.getRequestURI(), "json"));
        } catch (RuntimeException e) {
            ApiLogger.error("WebSpringServlet service error:", e);
            response.sendError(500);
        } finally {
        }
    }
    */

    /**
     * Dispatches client requests to the {@link #service(java.net.URI, java.net.URI, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)  }
     * method.
     *
     * @param request the {@link HttpServletRequest} object that
     *        contains the request the client made to
     *        the servlet.
     * @param response the {@link HttpServletResponse} object that
     *        contains the response the servlet returns
     *        to the client.
     * @exception IOException if an input or output error occurs
     *            while the servlet is handling the
     *            HTTP request.
     * @exception ServletException if the HTTP request cannot
     *            be handled.
     */
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /**
         * There is an annoying edge case where the service method is
         * invoked for the case when the URI is equal to the deployment URL
         * minus the '/', for example http://locahost:8080/HelloWorldWebApp
         */
        final String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();
        StringBuffer requestURL = request.getRequestURL();
        String requestURI = request.getRequestURI();
        final boolean checkPathInfo = pathInfo == null || pathInfo.isEmpty() || pathInfo.equals("/");

        /**
         * The HttpServletRequest.getRequestURL() contains the complete URI
         * minus the query and fragment components.
         */
        UriBuilder absoluteUriBuilder = null;
        try {
            absoluteUriBuilder = UriBuilder.fromUri(requestURL.toString());
        } catch (IllegalArgumentException iae) {
            final Response.Status badRequest = Response.Status.BAD_REQUEST;
            response.sendError(badRequest.getStatusCode(), badRequest.getReasonPhrase());
            return;
        }

        if (checkPathInfo && !request.getRequestURI().endsWith("/")) {
            // Only do this if the last segment of the servlet path does not contain '.'
            // This handles the case when the extension mapping is used with the servlet
            // see issue 506
            // This solution does not require parsing the deployment descriptor,
            // however still leaves it broken for the very rare case if a standard path
            // servlet mapping would include dot in the last segment (e.g. /.webresources/*)
            // and somebody would want to hit the root resource without the trailing slash
            int i = servletPath.lastIndexOf("/");
            if (servletPath.substring(i + 1).indexOf('.') < 0) {
                //不启用自动重定向功能 see ResourceConfig.FEATURE_REDIRECT
//                if (webComponent.getResourceConfig().getFeature(ResourceConfig.FEATURE_REDIRECT)) {
//                    URI l = absoluteUriBuilder.
//                            path("/").
//                            replaceQuery(request.getQueryString()).build();
//
//                    response.setStatus(307);
//                    response.setHeader("Location", l.toASCIIString());
//                    return;
//                } else {
                    pathInfo = "/";
                    requestURL.append("/");
                    requestURI += "/";
//                }
            }
        }

        /**
         * The HttpServletRequest.getPathInfo() and
         * HttpServletRequest.getServletPath() are in decoded form.
         *
         * On some servlet implementations the getPathInfo() removed
         * contiguous '/' characters. This is problematic if URIs
         * are embedded, for example as the last path segment.
         * We need to work around this and not use getPathInfo
         * for the decodedPath.
         */
        final String decodedBasePath = (pathInfo != null)
                ? request.getContextPath() + servletPath + "/"
                : request.getContextPath() + "/";

        final String encodedBasePath = UriComponent.encode(decodedBasePath,
                UriComponent.Type.PATH);

        if (!decodedBasePath.equals(encodedBasePath)) {
            throw new ContainerException("The servlet context path and/or the " +
                    "servlet path contain characters that are percent encoded");
        }

        final URI baseUri = absoluteUriBuilder.replacePath(encodedBasePath).
                build();

        String queryParameters = request.getQueryString();
        if (queryParameters == null) {
            queryParameters = "";
        }

        final URI requestUri = absoluteUriBuilder.replacePath(requestURI).
                replaceQuery(queryParameters).
                build();

        service(baseUri, requestUri, request, response);
    }

    /**
     * Dispatch client requests to a resource class.
     *
     * @param baseUri the base URI of the request.
     * @param requestUri the URI of the request.
     * @param request the {@link HttpServletRequest} object that
     *        contains the request the client made to
     *        the Web component.
     * @param response the {@link HttpServletResponse} object that
     *        contains the response the Web component returns
     *        to the client.
     * @return the status code of the response.
     * @exception IOException if an input or output error occurs
     *            while the Web component is handling the
     *            HTTP request.
     * @exception ServletException if the HTTP request cannot
     *            be handled.
     */
    @Override
    public int service(URI baseUri, URI requestUri, final HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (ApiLogger.isDebugEnabled()) {
            ApiLogger.debug("WebSpringServlet service(baseUri, requestUri, request, response)");
        }
        
        response.setHeader("Api-Server-IP", request.getLocalAddr());
        try {
            return super.service(baseUri, requestUri, request, response);
        } catch (WebApiException e) {
            ApiLogger.warn("WebSpringServlet service WebApiException warn:" + e.getMessage());
            int status = e.getFactor().getStatus().getStatusCode();
            //response.sendError(status, WebExceptionFormat.formatException(e, request.getRequestURI(), "json"));
            response.getWriter().print(WebExceptionFormat.formatException(e, request.getRequestURI(), "json"));
            return status;
        } catch (RuntimeException e) {
            ApiLogger.error("WebSpringServlet service error:", e);
            response.sendError(500);
            return 500;
        } finally {
        }
    }

}
