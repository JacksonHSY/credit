package com.ymkj.credit.web.api.model.base;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotBlank;

import com.ymkj.credit.web.api.model.req.ReqParam;



@Getter
@Setter
public class Model_004020 extends ReqParam{
private static final long serialVersionUID = 6059826933544124340L;
	
	@NotBlank (message = "customerId不能为空")
	private String customerId;
	
	@NotNull (message = "pageNo不能为空")
	private int pageNo;
	
	@NotNull (message = "pageSize不能为空")
	private int pageSize;
}
