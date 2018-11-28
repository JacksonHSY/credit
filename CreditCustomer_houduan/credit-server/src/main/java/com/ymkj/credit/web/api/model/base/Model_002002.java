package com.ymkj.credit.web.api.model.base;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

import com.ymkj.credit.web.api.model.req.ReqParam;

@Getter
@Setter
public class Model_002002  extends ReqParam{

	private static final long serialVersionUID = 1110415818855632727L;
	
	@NotNull(message="客户ID不能为空")
    private Long customerId;
	
	@NotBlank(message="登录密码不能为空")
    private String password;

}
