package com.ymkj.credit.web.api.model.base;

import javax.validation.constraints.NotNull;

import com.ymkj.credit.web.api.model.req.ReqParam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Model_006008 extends ReqParam{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3947634472935402731L;
	
	@NotNull(message = "身份证不能为空")
	private String idCard;
	
	@NotNull(message = "用户ID")
	private String customerId;
}
