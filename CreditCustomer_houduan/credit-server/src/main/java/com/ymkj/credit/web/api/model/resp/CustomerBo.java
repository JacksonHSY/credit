package com.ymkj.credit.web.api.model.resp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

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
public class CustomerBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7516126195235232258L;

	private String idNum;//身份证号码
	
	private String userName;//姓名
	
	private List<String> telList;//手机号
}
