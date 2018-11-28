package com.ymkj.credit.web.api.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author liangj@tuminsoft.com
 *
 */
@Getter
@Setter
public class Customer4H5Dto extends BaseDTO{

	private static final long serialVersionUID = 1383793441301242364L;
	
	/**
	 * 客户ID
	 */
	private String id;
	
	/**
	 * 手机号
	 */
	private String phone;
	
	/**
	 * 客户姓名
	 */
	private String customerName;
	
	/**
	 * 身份证号
	 */
	private String idCard;
	
	/**
	 * 银行卡按钮显示 1.有，0.无
	 */
	private Integer addCard;
	
	private Boolean isAgreement;// true  已同意, false  未同意
}
