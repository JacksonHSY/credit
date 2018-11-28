package com.ymkj.credit.common.dto;

/**
 * 公积金信息
 * @author YM10156
 *
 */
public class ProvidentInfoDto {
	
	private String accumulationFundAccount;//账户
	
	private String accumulationFundPassword;//密码

	public String getAccumulationFundAccount() {
		return accumulationFundAccount;
	}

	public void setAccumulationFundAccount(String accumulationFundAccount) {
		this.accumulationFundAccount = accumulationFundAccount;
	}

	public String getAccumulationFundPassword() {
		return accumulationFundPassword;
	}

	public void setAccumulationFundPassword(String accumulationFundPassword) {
		this.accumulationFundPassword = accumulationFundPassword;
	}
	
	
}
