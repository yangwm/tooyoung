package com.tooyoung.common.db;

public class InvalidDBAccessException extends Exception {

	private static final long serialVersionUID = -1387516993124229949L;

    public InvalidDBAccessException() {
    	super();
    }

    public InvalidDBAccessException(Object message) {
    	super(message == null ? "" : message.toString());
    }


    public InvalidDBAccessException(Object message, Throwable cause) {
        super(message == null ? "" : message.toString(), cause);
    }

    public InvalidDBAccessException(Throwable cause) {
        super(cause);
    }
}
