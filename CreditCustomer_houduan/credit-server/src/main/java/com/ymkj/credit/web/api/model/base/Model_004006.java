package com.ymkj.credit.web.api.model.base;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

import com.ymkj.credit.web.api.model.req.ReqParam;

@Getter
@Setter
public class Model_004006 extends ReqParam{

	private static final long serialVersionUID = 6964678828133095263L;
	
	@NotNull(message="页号不能为空")
    private Integer pageNo;
	
	@NotNull(message="每页数量不能为空")
    private Integer pageSize;
	
	@NotNull(message="类型不能为空")
    private Integer type;
	
    private Long customerId;
}
