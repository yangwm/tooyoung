/**
 * 
 */
package com.tooyoung.web.abc;

/**
 * 
 */
public class Result {

	public final static Result SUCCESS = new Result(1); // new Result(ErrorCode) 

	public int errno;

	public Result(int errno) {
		this.errno = errno;
	}

}
