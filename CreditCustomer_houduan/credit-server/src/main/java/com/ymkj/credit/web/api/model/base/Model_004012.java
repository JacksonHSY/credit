package com.ymkj.credit.web.api.model.base;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

import com.ymkj.credit.web.api.model.req.ReqParam;

@Getter
@Setter
public class Model_004012 extends ReqParam{
	
	private static final long serialVersionUID = 6964678828133095263L;
	
	@NotBlank(message="在线活体分数不能为空")
	private String score;
}
