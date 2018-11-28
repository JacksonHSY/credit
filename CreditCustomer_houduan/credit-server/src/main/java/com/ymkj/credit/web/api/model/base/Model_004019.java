package com.ymkj.credit.web.api.model.base;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

import com.ymkj.credit.web.api.model.req.ReqParam;
@Getter
@Setter
public class Model_004019 extends ReqParam{

	private static final long serialVersionUID = 6059826933544124340L;
	
	@NotBlank (message = "name不能为空")
	private String name;
	
	@NotBlank (message = "idCard不能为空")
	private String idCard;
	
	private String appNo;
}
