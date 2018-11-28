package com.ymkj.credit.common.constants;

public enum ConstantStatus {
	
	APPLY_INFO(Constants.APPLYINFO, Constants.APPLYINFO),//申请信息
	PERSION_INFO(Constants.PERSIONINFO,Constants.PERSIONINFO),//个人信息
	EMPITEM_INFO(Constants.EMPITEMINFO,Constants.EMPITEMINFO),//工作信息
	FRIENDS_INFO(Constants.FRIENDSINFO,Constants.FRIENDSINFO),
	CONTACTPERSON_INFO(Constants.FRIENDSINFO,Constants.CONTACTPERSONINFO),//联系人信息
	MATE_INFO(Constants.FRIENDSINFO,Constants.MATEINFO),//婚配信息
	//NODE_INFO(Constants.NODEINFO,Constants.NODEINFO),//第四步节点
	ASSETS_INFO(Constants.NODEINFO,Constants.ASSETSINFO),//	房产信息
	CAR_INFO(Constants.NODEINFO,Constants.CARINFO),//	车辆信息
	MERCHANTLOAN_INFO(Constants.NODEINFO,Constants.MERCHANTLOANINFO),//	淘宝账户信息表
	//PROVIDENT_INFO(Constants.NODEINFO,Constants.PROVIDENTINFO),//公积金信息表
	//INSURANCE_INFO(Constants.NODEINFO,Constants.INSURANCEINFO),//社保信息表
	SOCIALSECURITY_INFO(Constants.NODEINFO,Constants.SOCIALSECURITYINFO),//公积金/社保信息表
	CARDLOAN_INFO(Constants.NODEINFO,Constants.CARDLOANINFO),//	信用卡信息
	EDUCATION_INFO(Constants.NODEINFO,Constants.EDUCATIONINFO),//学历信息表
	POLICY_INFO(Constants.NODEINFO,Constants.POLICYINFO),//	寿险投保信息
	CREDITACCOUNT_INFO(Constants.NODEINFO,Constants.CREDITACCOUNTINFO),//	信用账户信息
	RESIDENCE_INFO(Constants.NODEINFO,Constants.RESIDENCEINFO),//居住证信息
	OTHER_INFO(Constants.NODEINFO,Constants.OTHERINFO),//其他信息
	SESAMECREDIT_INFO(Constants.NODEINFO,Constants.ZMSCORE)//芝麻分
	;
	
	private String fieldKey;
	
	private String fieldValue;
	
	private ConstantStatus(String fieldkey,String fieldValue){
		this.fieldKey = fieldkey;
		this.fieldValue = fieldValue;
	}
	
	public static String getFieldKey(String fieldValue){
		for (ConstantStatus cs :ConstantStatus.values()) {
			if(cs.fieldValue.equals(fieldValue)){
				return cs.fieldKey;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return this.fieldKey+"_"+this.fieldValue;
	}


	public String getFieldKey() {
		return fieldKey;
	}
	public void setFieldKey(String fieldKey) {
		this.fieldKey = fieldKey;
	}
	public String getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	
	
}
