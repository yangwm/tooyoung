/**
 * 
 */
package com.tooyoung.web.abc;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * 
 */
public class FxBusinessException extends WebApplicationException {

	private static final long serialVersionUID = 4483288937094187478L;

	public FxBusinessException(int errno) {

		super(Response.ok(new Result(errno)).build());
	}

	public FxBusinessException(Status status) {

		super(Response.status(status).build());
	}
}
