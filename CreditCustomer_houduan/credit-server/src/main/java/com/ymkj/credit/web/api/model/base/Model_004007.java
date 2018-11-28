package com.ymkj.credit.web.api.model.base;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

import com.ymkj.credit.web.api.model.req.ReqParam;

@Getter
@Setter
public class Model_004007 extends ReqParam{

	private static final long serialVersionUID = -7349472640650650176L;
	
	@NotNull(message="客户ID不能为空")
    private Long customerId;
	
	@NotBlank(message="手势密码开关不能为空")
    private String gestureSwitch;
	
}
