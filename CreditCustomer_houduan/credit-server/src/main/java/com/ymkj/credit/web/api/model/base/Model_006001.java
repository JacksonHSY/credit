package com.ymkj.credit.web.api.model.base;

import javax.validation.constraints.NotNull;

import com.ymkj.credit.web.api.model.req.ReqParam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Model_006001 extends ReqParam{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3408939894909439774L;

	@NotNull(message="手机号不能为空")
	private String cellPhone;
}
