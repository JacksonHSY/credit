package com.ymkj.credit.api.req;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.ymkj.credit.common.util.DateUtil;
import com.ymkj.springside.modules.utils.ToStringBuilder;

/**
 * 请求入参
 * 
 * @author longjw
 */
public class Request implements Serializable {

	private static final long serialVersionUID = 8331030139985346149L;

	/** 系统标识 */
	private String system;

	/** 请求序列 */
	private String sequence = DateUtil.format(new Date(), DateUtil.DATE_TIME_SSS_FORMAT)
			+ UUID.randomUUID().toString().substring(0, 8);

	/** 请求业务功能码 */
	private String functionId;

	/** 请求消息体 */
	private Object body;

	public Request() {
		super();
	}

	public Request(String system, String functionId, Object body) {
		super();
		this.system = system;
		this.functionId = functionId;
		this.body = body;
	}
	
	public static Request create(String system, String functionId){
		return new Request(system, functionId, null);
	}
	
	public static Request create(String functionId){
		return new Request(null, functionId, null);
	}
	
	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getFunctionId() {
		return functionId;
	}

	public void setFunctionId(String functionId) {
		this.functionId = functionId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.build(this);
	}
}
