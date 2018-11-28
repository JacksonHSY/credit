package com.ymkj.credit.api.req;

import java.io.Serializable;

import com.ymkj.springside.modules.utils.ToStringBuilder;

/**
 * 请求消息体
 * 
 * @author Joshua
 *
 */
public abstract class RequestBody implements Serializable {

	private static final long serialVersionUID = -8209078483493126360L;

	@Override
	public String toString() {
		return ToStringBuilder.build(this);
	}
}
