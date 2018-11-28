package com.ymkj.credit.web.api.model.base;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotBlank;

import com.ymkj.credit.web.api.model.req.ReqParam;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class Model_002007 extends ReqParam{

	private static final long serialVersionUID = 6964678828133095263L;
	
	@NotBlank(message="手机号不能为空")
    @Size(max = 11,min = 11, message = "手机号长度错误")
	@Pattern(regexp = "^1[\\d]{10}", message = "手机号格式错误")
    private String phone;

 /*   @NotBlank(message="客户经理不能为空")
    private String accountManagerNo;

    @NotBlank(message="身份证号不能为空")
    @Pattern(regexp = "(^\\d{17}[\\w\\d]$)|(^\\d{15}$)", message = "手身份证格式错误")
    private String idCard;
    
    @NotBlank(message="客户姓名不能为空")
    private String customerName;*/
    
    @NotBlank(message="密码不能为空")
    @Size(max = 32,min = 32, message = "密码长度错误")
    private String password;
    
    private String pushId;
}
