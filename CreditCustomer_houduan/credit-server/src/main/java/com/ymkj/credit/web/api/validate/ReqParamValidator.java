package com.ymkj.credit.web.api.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.ymkj.credit.web.api.model.req.ReqParam;

public class ReqParamValidator implements ConstraintValidator<ReqParamValidate, ReqParam> {

	public void initialize(ReqParamValidate param) {
	}

	public boolean isValid(ReqParam param, ConstraintValidatorContext context) {
		if(param == null)
			return false;
		
		return true;
	}

}
