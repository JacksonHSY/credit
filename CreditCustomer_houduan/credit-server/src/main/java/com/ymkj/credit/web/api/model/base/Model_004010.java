package com.ymkj.credit.web.api.model.base;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotBlank;

import com.ymkj.credit.web.api.model.req.ReqParam;
@Getter
@Setter
public class Model_004010 extends ReqParam {
	private static final long serialVersionUID = 6964678828133095263L;
	
	@NotBlank(message="customerId不能为空")
	private String customerId;
	
	@NotBlank (message = "name不能为空")
	private String name;
	
	@NotBlank (message = "idNo不能为空")
	private String idNo;
	
	@NotBlank (message = "phoneNum不能为空")
	private String phoneNum;
	
	@NotBlank (message = "city不能为空")
	private String city;
	
	@NotBlank (message = "cityName不能为空")
	private String cityName;
	
	@NotBlank (message = "profession不能为空")
	private String profession;
	
	@NotBlank (message = "beforeTaxSalary不能为空")
	private String beforeTaxSalary;
	
	@NotBlank (message = "applyLimit不能为空")
	private String applyLimit;
	
	@NotBlank (message = "deadline不能为空")
	private String deadline;
	
	@NotBlank (message = "purpose不能为空")
	private String purpose;
	
	@NotBlank (message = "purposeName不能为空")
	private String purposeName;
	
	@NotBlank (message = "monthLiabilities不能为空")
	private String monthLiabilities;
	
	private String operatorCode;
	
	private String property;
	
}
