package com.ymkj.credit.web.api.model.base;

import javax.validation.constraints.NotNull;

import com.ymkj.credit.web.api.model.req.ReqParam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Model_006013 extends ReqParam{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1537909566237985258L;
	private String loanNo;
}
