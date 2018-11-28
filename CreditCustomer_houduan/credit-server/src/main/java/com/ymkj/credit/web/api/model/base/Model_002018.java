package com.ymkj.credit.web.api.model.base;



import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

import com.ymkj.credit.web.api.model.req.ReqParam;
/**
 * h5判断登录
 * 
 * 
 * @author changj@yuminsoft.com
 * @date2018年4月13日
 * @version 1.0
 */
@Getter
@Setter
public class Model_002018  extends ReqParam{

	private static final long serialVersionUID = 1110415818855632727L;
	
	 @NotBlank(message="身份证号不能为空")
	 @Pattern(regexp = "(^\\d{17}[\\w\\d]$)|(^\\d{15}$)", message = "身份证长度错误")
	 private String idCard;
	 
}
