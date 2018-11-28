package com.ymkj.springside.modules.exception;

/**
 * 业务公用的Exception.
 * 
 * 继承自RuntimeException, 从由Spring管理事务的函数中抛出时会触发事务回滚.
 * 
 */
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 3583566093089790852L;
	
	/**异常错误码 */
	private String code;

	private Object[] args;

	public BusinessException() {
		super();
	}

	public BusinessException(String msg) {
		super(msg);
	}

	public BusinessException(String msg, Object[] args) {
		super(msg);
		this.args = args;
	}

	public BusinessException(String code, String msg) {
		super(msg);
		this.code = code;
	}

	public BusinessException(Throwable cause) {
		super(cause);
	}

	public BusinessException(String code, String message, Throwable e) {
		super(message, e);
		this.code = code;
	}
	
	public String getCode(){
		return code;
	}

	public Object[] getArgs() {
		return args;
	}
}
