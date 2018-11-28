package com.ymkj.credit.web.api.model.base;


import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

import com.ymkj.credit.web.api.model.req.ReqParam;

@Getter
@Setter
public class Model_002010  extends ReqParam{

	private static final long serialVersionUID = 1110415818855632727L;

	@NotBlank(message = "身份标识不能为空")
	private String identityNo;

	@NotBlank(message = "密码不能为空")
	private String password;
	
}
