package com.ymkj.credit.web.api.model.base;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotBlank;

import com.ymkj.credit.web.api.model.req.ReqParam;
@Getter
@Setter
public class Model_004017 extends ReqParam{
	
private static final long serialVersionUID = 6059826933544124340L;
	
	@NotBlank (message = "productCd不能为空")
	private String productCd;
	
	@NotBlank (message = "applyLmt不能为空")
	private String applyLmt;
	
	@NotBlank (message = "applyTerm不能为空")
	private String applyTerm;
}
