package com.ymkj.credit.common.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankCards {

	private String account;//*银行账户
	private String accounts;//银行帐号
	private String bankBranchName;//开户支行名
	private String bankCode;//银行code
	private String bankName;//开户行名
	//private String[] channelInfos;//银行验卡的渠道信息
	private String checkCard;//是否验卡 00 未校验 01 校验
	private String generalBankCode;//国内通用的银行code
	private String idCard;//*身份证号
	private String card; //身份证号
	private String phone;//手机号
	private String name;//持卡人
	private String createTime;//绑定时间
	private String flowId;//流水号
	private String verCode;//验证码
}
