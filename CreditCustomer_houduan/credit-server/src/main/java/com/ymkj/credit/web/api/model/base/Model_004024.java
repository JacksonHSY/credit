package com.ymkj.credit.web.api.model.base;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.ymkj.credit.web.api.model.req.ReqParam;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Model_004024 extends ReqParam{
	
	private static final long serialVersionUID = 6059826933544124340L;
	
	 @NotBlank(message="银行code不能为空")
	 private String bankCode;
	 
	 @NotBlank(message="银行账号不能为空")
	 private String account;
	 
	 @NotBlank(message="开户行名称不能为空")
	 private String bankName;
	 
	 /**
	  * 支行名称
	  */
	 private String bankBranchName;
	 
	 @NotBlank(message="预留手机号不能为空")
	 @Size(max = 11,min = 11, message = "手机号长度错误")
	 @Pattern(regexp = "^1[\\d]{10}", message = "手机号格式错误")
	 private String reservedMobile;
	
	@NotBlank (message = "loanNo不能为空")
	private String loanNo;
}
