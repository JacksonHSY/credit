package com.ymkj.credit.web.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BankListInfoDto extends BaseDTO{
	private static final long serialVersionUID = 1383793441301242364L;
	
	/**
	 * 银行卡图片Url
	 */
	private String iconUrl;
	
	/**
	 * 银行卡所属银行
	 */
	private String bankName;
	
	/**
	 * 银行卡卡号
	 */
	private String cardNo;
	/**
	 * 银行卡信息的唯一标识
	 */
	private String id;

}
