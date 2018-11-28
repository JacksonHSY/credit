package com.ymkj.credit.common.constants;

public enum ContractEnum {

	contract_type_01(1,new String[]{ContractType.contract_type_00208.getKey(),ContractType.contract_type_00210.getKey()},
					new String[]{ContractType.contract_type_00208.getValue(),ContractType.contract_type_00210.getValue()}),//01指以人为维度
	contract_type_02(2,new String[]{ContractType.contract_type_00209.getKey()},new String[]{ContractType.contract_type_00209.getValue()}),//02指以借款编号为维度
	;
	
	private Integer type;
	
	private String[] code;
	
	private String[] names;
	
	private ContractEnum(Integer type,String[] code,String[] names){
		this.type = type;
		this.code = code;
		this.names = names;
	}
	
	/**
	 * 获取code
	 * @param type
	 * @return
	 */
	public static String[] getCode(Integer type){
		for (ContractEnum c : ContractEnum.values()) {
			if(c.getType().equals(type)){
				return c.code;
			}
		}
		return null;
	}
	/**
	 * 获取names
	 * @param type
	 * @return
	 */
	public static String[] getNames(Integer type){
		for (ContractEnum c : ContractEnum.values()) {
			if(c.getType().equals(type)){
				return c.names;
			}
		}
		return null;
	}
	
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String[] getCode() {
		return code;
	}
	public void setCode(String[] code) {
		this.code = code;
	}
	public String[] getNames() {
		return names;
	}
	public void setNames(String[] names) {
		this.names = names;
	}
	
}
