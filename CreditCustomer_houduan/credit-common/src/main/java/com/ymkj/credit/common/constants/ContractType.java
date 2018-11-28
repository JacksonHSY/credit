package com.ymkj.credit.common.constants;

public enum ContractType {
	
	contract_type_00210("00210","电子签章授权委托书","18"),
	contract_type_00209("00209","征信查询授权书","20"),
	contract_type_00208("00208","电子认证服务协议","19"),
	;
	
	private String key;
	private String value;
	private String Type;
	
	private ContractType(String key,String value,String Type){
		this.key = key;
		this.value = value;
		this.Type = Type;
	}
	
	public static String getValue(String key){
		for (ContractType c : ContractType.values()) {
			if(c.getKey().equals(key)){
				return c.getValue();
			}
		}
		return null;
	}
	
	public static String getType(String key){
		for (ContractType c : ContractType.values()) {
			if(c.getKey().equals(key)){
				return c.getType();
			}
		}
		return null;
	}
	
	public static String getKey(String Type){
		for (ContractType c : ContractType.values()) {
			if(c.getType().equals(Type)){
				return c.getKey();
			}
		}
		return null;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}
	
}
