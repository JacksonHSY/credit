package com.ymkj.credit.common.constants;

public enum BankCode {
	bank_code_00080001("00080001","524059"),
	bank_code_00080002("00080002","966504"),
	bank_code_00080003("00080003","865802"),
	bank_code_00080004("00080004","807118"),
	bank_code_00080005("00080005","579047"),
	bank_code_00080006("00080006","204491"),
	bank_code_00080009("00080009","751632"),
	bank_code_00080011("00080011","061555"),
	bank_code_00080012("00080012","600646"),
	bank_code_00080013("00080013","848418"),
	bank_code_00080014("00080014","681925"),
	bank_code_00080029("00080029","638348"),
	bank_code_00080168("00080168","075120"),
	;
	
	private BankCode(String key,String value){
		this.key = key;
		this.value =value;
	}
	
	public static String getValue(String key){
		for (BankCode cs :BankCode.values()) {
			if(cs.getKey().equals(key)){
				return cs.value;
			}
		}
		return null;
	}
	
	private String key;
	private String value;
	
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
	
	
}
