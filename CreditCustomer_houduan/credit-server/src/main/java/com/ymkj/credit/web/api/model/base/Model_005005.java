package com.ymkj.credit.web.api.model.base;

import javax.validation.constraints.NotNull;

import com.ymkj.credit.web.api.model.req.ReqParam;

import lombok.Getter;
import lombok.Setter;

/**
 * 获取借款信息
 * @author YM10156
 *
 */
@Getter
@Setter
public class Model_005005 extends ReqParam{
	
	private static final long serialVersionUID = 1L;
	
	@NotNull(message="身份证不能为空")
	private String idCard;
	
	@NotNull(message="借款编号不能为空")
	private String loanNo;
	
	@NotNull(message ="借款环节")
	private String fieldKey;
	
}
