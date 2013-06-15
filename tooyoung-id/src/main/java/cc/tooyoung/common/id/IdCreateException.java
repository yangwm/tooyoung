package cc.tooyoung.common.id;

/**
 * Id Create Exception
 * 
 * @author yangwm May 29, 2013 5:03:17 PM
 */
public class IdCreateException extends RuntimeException {

	private static final long serialVersionUID = 121321343545426426l;
	
	public IdCreateException() {
		super();
	}

	public IdCreateException(String message) {
		super(message);
	}

	public IdCreateException(String message, Throwable cause) {
		super(message, cause);
	}

	public IdCreateException(Throwable cause) {
		super(cause);
	}

}
