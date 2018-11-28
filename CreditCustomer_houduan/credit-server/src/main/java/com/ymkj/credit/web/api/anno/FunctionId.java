package com.ymkj.credit.web.api.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解被使用在一级功能service 中的功能处理方法上
 * 
 * @author 00237071
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface FunctionId {
	/**
	 * 功能码
	 * @return
	 */
	String value();
	
	/**
	 * 对于功能码的功能描述
	 * @return
	 */
	String desc() default "";
}
