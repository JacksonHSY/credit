package com.ymkj.credit.web.api.model.base;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import com.ymkj.credit.web.api.model.req.ReqParam;
@Getter
@Setter
public class Model_005002 extends ReqParam{
	
	/**
	 * 申请信息入参
	 */
	private static final long serialVersionUID = 765075394443208560L;

	@NotNull(message="身份证不能为空")
    private String idCard;
	
    private String fieldValue;
    
    private String loanNo;

}
