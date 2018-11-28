package com.ymkj.credit.web.api.model.base;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotBlank;

import com.ymkj.credit.web.api.model.req.ReqParam;
@Getter
@Setter
public class Model_004011 extends ReqParam{
	
    private static final long serialVersionUID = 6964678828133095263L;
	
	@NotBlank(message="客户经理邀请码")
	private String operatorCode;
}
