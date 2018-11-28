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
public class CustomerDto extends BaseDTO{

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
	 * 身份证号明文
	 */
	private String idCardName;
	/**
	 * 手势密码开关
	 */
	private String gestureSwitch;
	
    /**
     * 是否已通过身份认证
     */
    private String isIdentityAuthentication;
	
	private String nextExpiry;//下一还款日
	
	private String showProductName;//试算显示的产品名称
	
	private String showProductCode;//试算显示的产品code
	
	private String 	applyTerm;//试算期限
	
	private String isAgreement;// true  已同意, false  未同意
	
	private String isLivenessOpen;//是否开启人脸识别
}
