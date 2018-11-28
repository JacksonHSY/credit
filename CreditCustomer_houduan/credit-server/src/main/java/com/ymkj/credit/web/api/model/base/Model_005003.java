package com.ymkj.credit.web.api.model.base;

import javax.validation.constraints.NotNull;

import com.ymkj.credit.web.api.model.req.ReqParam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Model_005003  extends ReqParam{

	private static final long serialVersionUID = 1L;
	
	@NotNull(message="身份证IdCard不能为空")
	private String idCard;
	
	private String fieldKey;
	
	private String loanNo;

}
