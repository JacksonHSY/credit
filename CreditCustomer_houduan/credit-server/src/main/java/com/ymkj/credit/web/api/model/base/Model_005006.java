package com.ymkj.credit.web.api.model.base;

import javax.validation.constraints.NotNull;

import com.ymkj.credit.web.api.model.req.ReqParam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Model_005006 extends ReqParam{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5827344449995140418L;
	
	/**图片id*/
	@NotNull(message="借款编号不能为空")
	private String loanNo;

}
