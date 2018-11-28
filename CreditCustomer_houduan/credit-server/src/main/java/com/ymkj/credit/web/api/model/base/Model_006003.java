package com.ymkj.credit.web.api.model.base;

import javax.validation.constraints.NotNull;

import com.ymkj.credit.web.api.model.req.ReqParam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Model_006003 extends ReqParam{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7947405948650105600L;
	
	@NotNull(message = "用户编号不能为空")
	private String customerId;
	
	@NotNull(message ="银行卡号不能为空")
	private String bankCard;
	
	@NotNull(message = "手机号码不能为空")
	private String cellPhone;
	
	@NotNull(message = "银行卡code不能为空")
	private String bankCode;
	
	@NotNull(message = "支行不能为空")
	private String applyBankBranch;
	
	@NotNull(message = "借款编号不能为空")
	private String loanNo;
	
}
