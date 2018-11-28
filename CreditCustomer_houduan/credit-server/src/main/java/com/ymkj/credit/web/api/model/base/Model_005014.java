package com.ymkj.credit.web.api.model.base;

import javax.validation.constraints.NotNull;

import com.ymkj.credit.web.api.model.req.ReqParam;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Model_005014 extends ReqParam{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2880920595980205836L;
	
	/**图片id*/
	@NotNull(message="借款编号不能为空")
	private String loanNo;
	
	@NotNull(message = "合同签章")
	private String isNotSign;

}
