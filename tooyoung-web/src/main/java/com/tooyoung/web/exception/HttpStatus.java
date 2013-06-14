package com.tooyoung.web.exception;

public enum HttpStatus {
	/**
	 * 200 OK, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.1">HTTP/1.1 documentation</a>}
	 * .
	 */
	OK(200, "OK"),
	/**
	 * 201 Created, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.2">HTTP/1.1 documentation</a>}
	 * .
	 */
	CREATED(201, "Created"),
	/**
	 * 202 Accepted, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.3">HTTP/1.1 documentation</a>}
	 * .
	 */
	ACCEPTED(202, "Accepted"),
	/**
	 * 204 No Content, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.5">HTTP/1.1 documentation</a>}
	 * .
	 */
	NO_CONTENT(204, "No Content"),
	/**
	 * 303 See Other, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.2">HTTP/1.1 documentation</a>}
	 * .
	 */
	MOVED_PERMANENTLY(301, "Moved Permanently"),
	/**
	 * 303 See Other, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.4">HTTP/1.1 documentation</a>}
	 * .
	 */
	SEE_OTHER(303, "See Other"),
	/**
	 * 304 Not Modified, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5">HTTP/1.1 documentation</a>}
	 * .
	 */
	NOT_MODIFIED(304, "Not Modified"),
	/**
	 * 307 Temporary Redirect, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.8">HTTP/1.1 documentation</a>}
	 * .
	 */
	TEMPORARY_REDIRECT(307, "Temporary Redirect"),
	/**
	 * 400 Bad Request, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.1">HTTP/1.1 documentation</a>}
	 * .
	 */
	BAD_REQUEST(400, "Bad Request"),
	/**
	 * 401 Unauthorized, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.2">HTTP/1.1 documentation</a>}
	 * .
	 */
	UNAUTHORIZED(401, "Unauthorized"),
	/**
	 * 403 Forbidden, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.4">HTTP/1.1 documentation</a>}
	 * .
	 */
	FORBIDDEN(403, "Forbidden"),
	/**
	 * 404 Not Found, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.5">HTTP/1.1 documentation</a>}
	 * .
	 */
	NOT_FOUND(404, "Not Found"),
	
	/**
	 *405 Method Not Allowed, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.6">HTTP/1.1 documentation</a>}
	 */
	METHOD_NOT_ALLOWED(405,"Method Not Allowed"),
	
	/**
	 * 406 Not Acceptable, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.7">HTTP/1.1 documentation</a>}
	 * .
	 */
	NOT_ACCEPTABLE(406, "Not Acceptable"),
	/**
	 * 409 Conflict, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.10">HTTP/1.1 documentation</a>}
	 * .
	 */
	CONFLICT(409, "Conflict"),
	/**
	 * 410 Gone, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.11">HTTP/1.1 documentation</a>}
	 * .
	 */
	GONE(410, "Gone"),
	/**
	 * 412 Precondition Failed, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.13">HTTP/1.1 documentation</a>}
	 * .
	 */
	PRECONDITION_FAILED(412, "Precondition Failed"),
	
	/**
	 * 413 Request Entity Too Large, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.14">HTTP/1.1 documentation</a>}
	 * .
	 */
	REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
	
	/**
	 * 415 Unsupported Media Type, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.16">HTTP/1.1 documentation</a>}
	 * .
	 */
	UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
	/**
	 * 500 Internal Server Error, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.1">HTTP/1.1 documentation</a>}
	 * .
	 */
	INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
	
	
	/**
	 * 502 Bad Gateway Error, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.3">HTTP/1.1 documentation</a>}
	 * .
	 */
	BAD_GATEWAY(502, "Bad Gateway"),
	
	/**
	 * 503 Service Unavailable, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.4">HTTP/1.1 documentation</a>}
	 * .
	 */
	SERVICE_UNAVAILABLE(503, "Service Unavailable"),
	
	
	/**
	 * 504 Gateway Timeout, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.5">HTTP/1.1 documentation</a>}
	 * .
	 */
	GATEWAY_TIMEOUT(504, "Gateway Timeout"),
		
	/**
	 * 505 HTTP Version Not Supported, see
	 * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.6">HTTP/1.1 documentation</a>}
	 * .
	 */
	 HTTP_VERSION_NOT_SUPPORTED(505, " HTTP Version Not Supported");

	private final int code;
	private final String reason;
	private Family family;

	/**
	 * An enumeration representing the class of status code. Family is used here
	 * since class is overloaded in Java.
	 */
	public enum Family {
		INFORMATIONAL, SUCCESSFUL, REDIRECTION, CLIENT_ERROR, SERVER_ERROR, OTHER
	};

	HttpStatus(final int statusCode, final String reasonPhrase) {
		this.code = statusCode;
		this.reason = reasonPhrase;
		switch (code / 100) {
		case 1:
			this.family = Family.INFORMATIONAL;
			break;
		case 2:
			this.family = Family.SUCCESSFUL;
			break;
		case 3:
			this.family = Family.REDIRECTION;
			break;
		case 4:
			this.family = Family.CLIENT_ERROR;
			break;
		case 5:
			this.family = Family.SERVER_ERROR;
			break;
		default:
			this.family = Family.OTHER;
			break;
		}
	}

	/**
	 * Get the class of status code
	 * 
	 * @return the class of status code
	 */
	public Family getFamily() {
		return family;
	}

	/**
	 * Get the associated status code
	 * 
	 * @return the status code
	 */
	public int getStatusCode() {
		return code;
	}

	/**
	 * Get the reason phrase
	 * 
	 * @return the reason phrase
	 */
	@Override
	public String toString() {
		return reason;
	}

	/**
	 * Convert a numerical status code into the corresponding Status
	 * 
	 * @param statusCode
	 *            the numerical status code
	 * @return the matching Status or null is no matching Status is defined
	 */
	public static HttpStatus fromStatusCode(final int statusCode) {
		for (HttpStatus s : HttpStatus.values()) {
			if (s.code == statusCode) {
				return s;
			}
		}
		return null;
	}
}