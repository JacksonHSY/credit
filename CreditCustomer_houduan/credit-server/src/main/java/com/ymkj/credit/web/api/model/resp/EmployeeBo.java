package com.ymkj.credit.web.api.model.resp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description：信贷客户信息响应对象
 * @ClassName: CustomerInfoModel.java
 * @Author：tianx
 * @Date：2017年8月24日
 * -----------------变更历史-----------------
 * 如：who  2017年8月24日  修改xx功能
 */
@Getter
@Setter
public class EmployeeBo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8526233208234433628L;

	private String userCode;//工号
	
	private String empName;//姓名
	
	private String mobile;//手机号
	
	private String isInService;//
}
