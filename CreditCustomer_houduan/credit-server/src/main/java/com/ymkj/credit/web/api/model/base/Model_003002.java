package com.ymkj.credit.web.api.model.base;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

import com.ymkj.credit.web.api.model.req.ReqParam;

@Getter
@Setter
public class Model_003002 extends ReqParam{

	/**
	 * 
	 */
	private static final long serialVersionUID = 638731188314076785L;
	
	@NotNull(message="客户ID不能为空")
    private Long customerId;

}
