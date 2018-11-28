package com.ymkj.credit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SwitchParamBean {

	// 开关关闭
	public static final String SWITCH_OFF = "OFF";
	
	@Value("${ce_validate_switch}")
	private String ceValidateSwitch;

	@Value("${app_version_switch}")
	private String appVersionSwitch;

	@Value("${session_validate_switch}")
	private String sessionValidateSwitch;

	@Value("${ID5_switch}")
	private String ID5Switch;

	@Value("${thirdparty_call_recordlog_switch}")
	private String thirdpartyCallRecordlogSwitch;
	
	@Value("${xxtea.encript_switch}")
	private String xxteaEncriptSwitch;
	
	@Value("${card_count_swith}")
	private String cardCountSwith;

	public String getCeValidateSwitch() {
		return ceValidateSwitch;
	}

	public void setCeValidateSwitch(String ceValidateSwitch) {
		this.ceValidateSwitch = ceValidateSwitch;
	}

	public String getAppVersionSwitch() {
		return appVersionSwitch;
	}

	public void setAppVersionSwitch(String appVersionSwitch) {
		this.appVersionSwitch = appVersionSwitch;
	}

	public String getSessionValidateSwitch() {
		return sessionValidateSwitch;
	}

	public void setSessionValidateSwitch(String sessionValidateSwitch) {
		this.sessionValidateSwitch = sessionValidateSwitch;
	}

	public String getID5Switch() {
		return ID5Switch;
	}

	public void setID5Switch(String iD5Switch) {
		ID5Switch = iD5Switch;
	}

	public String getThirdpartyCallRecordlogSwitch() {
		return thirdpartyCallRecordlogSwitch;
	}

	public void setThirdpartyCallRecordlogSwitch(
			String thirdpartyCallRecordlogSwitch) {
		this.thirdpartyCallRecordlogSwitch = thirdpartyCallRecordlogSwitch;
	}

	public String getXxteaEncriptSwitch() {
		return xxteaEncriptSwitch;
	}

	public void setXxteaEncriptSwitch(String xxteaEncriptSwitch) {
		this.xxteaEncriptSwitch = xxteaEncriptSwitch;
	}

	public String getCardCountSwith() {
		return cardCountSwith;
	}

	public void setCardCountSwith(String cardCountSwith) {
		this.cardCountSwith = cardCountSwith;
	}

	public static String getSwitchOff() {
		return SWITCH_OFF;
	}

}
