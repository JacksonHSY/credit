package com.ymkj.credit.web.api.dto;

import org.springframework.beans.factory.annotation.Value;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author liangj@tuminsoft.com
 *
 */
@Getter
@Setter
public class CustomerInfoDto extends BaseDTO{

	private static final long serialVersionUID = 1383793441301242364L;

	/**
	 * 客户ID
	 */
	private Long id;
	
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
	 * 身份证号全称
	 */
	private String idCardName;
	
	/**
	 * 客户经理工号
	 */
	private String accountManagerNo;
	
	/**
	 * 客户经理姓名
	 */
	private String accountManagerName;
	
	/**
	 * 客户经理手机号
	 */
	private String accountManagerphone;
	
	/**
	 * 所属营业部
	 */
	private String salesOffice;
	
	/**
	 * 营业部电话
	 */
	private String officePhone;
	
	/**
	 * 营业部地址
	 */
	private String address;
	
	/**
	 * 手势密码开关
	 */
	private String gestureSwitch;
	 /**
     * 客服电话
     */
    private String serviceTelNumber;
    /**
     * 是否已通过身份认证
     */
    private String isIdentityAuthentication;
    
    
}
