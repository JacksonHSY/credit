package com.ymkj.credit.web.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankInfoDto extends BaseDTO{
	
	private static final long serialVersionUID = 1383793441301242364L;
	
	/**
	 * 银行卡图片Url
	 */
	private String cardImgUrl;
	
	/**
	 * 持卡人
	 */
	private String ownerName;
	
	/**
	 * 银行卡卡号
	 */
	private String cardNo;
	/**
	 * 所属银行
	 */
	private String bankName;
	/**
	 * 所属银行支行
	 */
	private String branchBankName;
	
	/**
	 * 银行卡预留手机号
	 */
	private String phoneNum;
}
