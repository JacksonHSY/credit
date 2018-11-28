package com.ymkj.credit.common.constants;

public enum ContractInfoEnum {
	
	contract_info_01(new String[]{ContractType.contract_type_00208.getKey(),ContractType.contract_type_00210.getKey()},Constants.CONTRACT_INFO_TYPE_01),
	contract_Info_02(new String[]{ContractType.contract_type_00209.getKey(),},Constants.CONTRACT_INFO_TYPE_02),
	;
	
	private String[] key;
	private String value;
	
	private ContractInfoEnum(String[] key,String value){
		this.key = key;
		this.value = value;
	}
	
	public static String[] getKey(String value){
		for (ContractInfoEnum c : ContractInfoEnum.values()) {
			if(c.getValue().equals(value)){
				return c.getKey();
			}
		}
		return null;
	}

	public String[] getKey() {
		return key;
	}

	public void setKey(String[] key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
