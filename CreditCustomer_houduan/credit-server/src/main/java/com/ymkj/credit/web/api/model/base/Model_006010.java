package com.ymkj.credit.web.api.model.base;

import javax.validation.constraints.NotNull;

import com.ymkj.credit.web.api.model.req.ReqParam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Model_006010 extends ReqParam{

	/**
	 * 查询是否能到签约步骤
	 */
	private static final long serialVersionUID = -3947634472935402731L;
	
	@NotNull(message = "借款申请编号不能为空")
	private String loanNo;
}
