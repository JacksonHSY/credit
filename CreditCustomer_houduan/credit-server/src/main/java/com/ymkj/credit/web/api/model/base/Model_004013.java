package com.ymkj.credit.web.api.model.base;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotBlank;

import com.ymkj.credit.web.api.model.req.ReqParam;
@Setter
@Getter
public class Model_004013 extends ReqParam {

	private static final long serialVersionUID = 6964678828133095263L;
	
	@NotBlank (message = "customerId不能为空")
	private String customerId;
	
	@NotBlank (message = "idNoFrontImg不能为空")
	private String idNoFrontImg;
	
	@NotBlank (message = "idCard不能为空")
	private String idCard;
	
	@NotBlank (message = "customerName不能为空")
	private String customerName;
	
	@NotBlank (message = "idNoFrontInfo不能为空")
	private String idNoFrontInfo;
	
	@NotBlank (message = "idNoReverseSideImg不能为空")
	private String idNoReverseSideImg;
	
	@NotBlank (message = "idNoReverseSideInfo不能为空")
	private String idNoReverseSideInfo;
	
	private String score;
	
	@NotBlank (message = "beginDate不能为空")
	private String beginDate;
	
	@NotBlank (message = "endDate不能为空")
	private String endDate;
	
}
	
