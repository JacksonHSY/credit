package com.ymkj.credit.web.api.model.base;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import com.ymkj.credit.web.api.model.req.ReqParam;
@Getter
@Setter
public class Model_005013 extends ReqParam{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2395799303948661662L;
	
	/**图片id*/
	@NotNull(message="借款编号不能为空")
	private String loanNo;
	
	@NotNull(message="图片id不能为空")
	private String picId;

}
