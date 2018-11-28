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
public class DepartmentBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6219536793402918913L;

	private String deptName;//名称
	
	private String site;//地址
	
	private String telephone;//电话
	
	private String longitude;//经度
	
	private String latitude;//纬度
}
