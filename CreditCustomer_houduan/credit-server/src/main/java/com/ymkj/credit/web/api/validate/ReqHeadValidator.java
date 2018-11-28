package com.ymkj.credit.web.api.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.ymkj.credit.web.api.model.req.ReqHeadParam;

public class ReqHeadValidator implements ConstraintValidator<ReqHeadValidate, ReqHeadParam> {

	public void initialize(ReqHeadValidate head) {
	}

	public boolean isValid(ReqHeadParam head, ConstraintValidatorContext context) {
		if(head == null)
			return false;
		
		return true;
	}

}
