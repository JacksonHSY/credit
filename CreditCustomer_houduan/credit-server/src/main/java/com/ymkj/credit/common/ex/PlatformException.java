package com.ymkj.credit.common.ex;

public class PlatformException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public PlatformException() {
		super();
	}

	public PlatformException(String message) {
		super(message);
	}

	public PlatformException(Throwable cause) {
		super(cause);
	}

	public PlatformException(String message, Throwable cause) {
		super(message, cause);
	}

}
