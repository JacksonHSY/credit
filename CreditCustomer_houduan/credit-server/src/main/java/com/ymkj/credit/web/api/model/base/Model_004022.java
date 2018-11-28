package com.ymkj.credit.web.api.model.base;

import org.hibernate.validator.constraints.NotBlank;

import com.ymkj.credit.web.api.model.req.ReqParam;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Model_004022 extends ReqParam{ 
	
	private static final long serialVersionUID = 6059826933544124340L;
	
	@NotBlank (message = "id不能为空")
	private String id;
}
