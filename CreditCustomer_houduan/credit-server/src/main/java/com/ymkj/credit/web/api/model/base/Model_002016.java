package com.ymkj.credit.web.api.model.base;


import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

import com.ymkj.credit.web.api.model.req.ReqParam;
/**
 * tpp验证码
 * 
 * 
 * @author changj@yuminsoft.com
 * @date2018年4月13日
 * @version 1.0
 */
@Getter
@Setter
public class Model_002016  extends ReqParam{

	private static final long serialVersionUID = 1110415818855632727L;
	
	//String bankCode,String bankName,String accountNo,String accountName,String idNum,String phone
	 @NotBlank(message="身份证号不能为空")
	 @Pattern(regexp = "(^\\d{17}[\\w\\d]$)|(^\\d{15}$)", message = "身份证长度错误")
     private String idCard;
	 
	 @NotBlank(message="持卡人姓名不能为空")
	 private String accountName;
	 
	 @NotBlank(message="银行code不能为空")
	 private String bankCode;
	 
	 @NotBlank(message="银行账号不能为空")
	 private String account;
	 
	 @NotBlank(message="开户行名称不能为空")
	 private String bankName;
	 
	 
	 @NotBlank(message="预留手机号不能为空")
	 @Size(max = 11,min = 11, message = "手机号长度错误")
	 @Pattern(regexp = "^1[\\d]{10}", message = "手机号格式错误")
	 private String reservedMobile;
	 
	 
//	 @NotBlank(message="手机号不能为空")
//	 @Size(max = 11,min = 11, message = "手机号长度错误")
//	 @Pattern(regexp = "^1[\\d]{10}", message = "手机号格式错误")
//	 private String phone;
	 
}
