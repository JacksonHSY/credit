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
public class CustomerInfoModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6059826933544124340L;

	private CustomerBo customer;//客户信息对象
	
	private EmployeeBo employee;//客户经理信息对象
	
	private DepartmentBo department;//营业部信息对象
}
