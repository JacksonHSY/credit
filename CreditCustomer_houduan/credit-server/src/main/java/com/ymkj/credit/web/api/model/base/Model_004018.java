package com.ymkj.credit.web.api.model.base;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import com.ymkj.credit.web.api.model.req.ReqParam;
@Setter
@Getter
public class Model_004018 extends ReqParam{
	
private static final long serialVersionUID = 6059826933544124340L;
	
	@NotBlank(message = "客户姓名不能为空")
	private String name;
	
	@NotBlank(message = "客户身份证号不能为空")
	private String idCard;
	
	@NotBlank (message = "htmlContent不能为空")
	private String htmlContent;
	
	private String appNo;
}
