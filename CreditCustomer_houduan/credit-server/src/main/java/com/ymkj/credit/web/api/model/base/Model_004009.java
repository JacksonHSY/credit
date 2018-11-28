package com.ymkj.credit.web.api.model.base;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotBlank;

import com.ymkj.credit.web.api.model.req.ReqParam;
@Getter
@Setter
public class Model_004009 extends ReqParam {
	private static final long serialVersionUID = 6964678828133095263L;
	
	@NotBlank (message = "customerId不能为空")
	private String customerId;
	
	@NotNull(message="当前页不能为空")
    private int pageNo;
    
    @NotNull(message="显示条数不能为空")
    private int pageSize;
}
