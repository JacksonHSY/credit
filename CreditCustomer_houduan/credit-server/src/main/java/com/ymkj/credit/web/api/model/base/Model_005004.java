package com.ymkj.credit.web.api.model.base;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import com.ymkj.credit.web.api.model.req.ReqParam;
@Getter
@Setter
public class Model_005004 extends ReqParam{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2116854746422550442L;
	
	/**个人信息保存入参*/
	@NotNull(message="借款编号不能为空")
	private String loanNo;
	
	@NotNull(message="不能为空")
    private String fieldKey;
	
    private String fieldValue;
    
	private String tab;
	
	private String pictureValue;

}
