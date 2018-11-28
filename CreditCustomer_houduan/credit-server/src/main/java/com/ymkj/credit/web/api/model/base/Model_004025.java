package com.ymkj.credit.web.api.model.base;


import org.hibernate.validator.constraints.NotBlank;

import com.ymkj.credit.web.api.model.req.ReqParam;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Model_004025 extends ReqParam{
	
	private static final long serialVersionUID = 6059826933544124340L;
	
	 @NotBlank(message="合同编号不能为空")
	 private String contractNum;
	
	 @NotBlank(message="银行账号不能为空")
	 private String account;
	 
	
	@NotBlank (message = "amount不能为空")
	private String amount;
}
