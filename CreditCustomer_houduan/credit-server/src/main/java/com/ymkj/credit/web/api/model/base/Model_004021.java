package com.ymkj.credit.web.api.model.base;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.ymkj.credit.web.api.model.req.ReqParam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Model_004021 extends  ReqParam{
	private static final long serialVersionUID = 6059826933544124340L;
	
	private String cardImgStr;
	
	@NotBlank (message = "ownerName不能为空")
	private String ownerName;
	
	@NotBlank (message = "cardNo不能为空")
	@Size(max = 20, message = "银行卡号长度不正确")
	private String cardNo;
	
	@NotBlank (message = "bankName不能为空")
	private String bankName;
	
	@NotBlank (message = "branchBankName不能为空")
	@Size(max = 18, message = "支行名称不能超过五十字")
	private String branchBankName;
	
	@NotBlank(message="手机号不能为空")
	@Size(max = 11,min = 11, message = "手机号长度错误")
	@Pattern(regexp = "^1[\\d]{10}", message = "手机号格式错误")
	private String phoneNum;
	
	@NotNull (message = "type不能为空" )
	//0:添加(默认),  1:编辑
	private int type;
	
	private String  id;
	
}
