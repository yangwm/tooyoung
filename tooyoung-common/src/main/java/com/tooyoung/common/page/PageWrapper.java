package com.tooyoung.common.page;

import java.io.Serializable;

/**
 * 分页包装类
 * 
 * @author suoyuan
 * 2011-02-14
 *
 */
public class PageWrapper<T> implements Serializable{
	private static final long serialVersionUID = 5622598643654003103L;

	/**
	 * 向前翻页的cursor
	 */
	public long previousCursor;

	/**
	 * 向后翻页的cursor
	 */
	public long nextCursor;
	
	/**
	 * 返回结果的id数组
	 */
	public T result;
	
	/**
	 * 返回的totalNumber
	 */
	public int totalNumber;
	
	/**
	 * is needPagination;
	 */
	public boolean needPagination;
	
	public PageWrapper(){
	}
	
	public PageWrapper(long previousCursor, long nextCursor, T resultIds){
		this.previousCursor = previousCursor;
		this.nextCursor = nextCursor;
		this.result = resultIds;
	}
	
	public void setCursor(long previousCursor, long nextCursor){
		this.previousCursor = previousCursor;
		this.nextCursor = nextCursor;
	}
}
