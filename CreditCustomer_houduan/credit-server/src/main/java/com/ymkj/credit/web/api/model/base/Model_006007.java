package com.ymkj.credit.web.api.model.base;

import javax.validation.constraints.NotNull;

import org.jboss.logging.Message;

import com.ymkj.credit.web.api.model.req.ReqParam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Model_006007 extends ReqParam{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2916130442309607370L;
	
	@NotNull(message = "身份证号不能为空")
	private String idCard;
	
	@NotNull(message = "银行卡号不能为空")
	private String bankCard;

}
