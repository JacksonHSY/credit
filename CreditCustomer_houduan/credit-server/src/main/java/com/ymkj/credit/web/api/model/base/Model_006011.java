package com.ymkj.credit.web.api.model.base;

import javax.validation.constraints.NotNull;

import com.ymkj.credit.web.api.model.req.ReqParam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Model_006011 extends ReqParam{

	private static final long serialVersionUID = -6998379648741391590L;

	@NotNull(message = "银行卡号不能为空")
	private String bankCard;
}
