package com.ymkj.credit.common.dto;

/**
 * 社保信息
 * @author YM10156
 *
 */
public class InsuranceInfoDto {
	
	private String socialInsuranceAccount; //账户
	
	private String socialInsurancePassword;//密码

	public String getSocialInsuranceAccount() {
		return socialInsuranceAccount;
	}

	public void setSocialInsuranceAccount(String socialInsuranceAccount) {
		this.socialInsuranceAccount = socialInsuranceAccount;
	}

	public String getSocialInsurancePassword() {
		return socialInsurancePassword;
	}

	public void setSocialInsurancePassword(String socialInsurancePassword) {
		this.socialInsurancePassword = socialInsurancePassword;
	}
	
	

}
