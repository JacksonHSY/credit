package com.ymkj.credit.common.beanvalidator;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class Validate<T> {

	private static Validate uniqueInstance = null;

	private static ValidatorFactory factory = null;

	private static Validator validator = null;


	/**
	 * 定义私有构造方法.
	 */
	private Validate() {

	}

	/**
	 * 单例模式.
	 * 
	 * @return
	 */
	public static Validate getInstance() {
		if (uniqueInstance == null) {
			factory = Validation.buildDefaultValidatorFactory();
			validator = factory.getValidator();
			uniqueInstance = new Validate();
		}
		return uniqueInstance;

	}

	/**
	 * 合法行校验
	 * 
	 * @param arg0
	 *            T
	 * @param systemCode
	 *            进行检验系统编码
	 * @param arg1
	 *            Class<T>
	 */
	public Set<ConstraintViolation<T>> validate(T arg0, Class<T>... arg1) {
		Set<ConstraintViolation<T>> constraintViolations = validator.validate(
				arg0, arg1);
		return constraintViolations;
	}
	
}
