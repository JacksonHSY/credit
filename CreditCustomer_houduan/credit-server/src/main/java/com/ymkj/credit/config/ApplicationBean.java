package com.ymkj.credit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationBean {

	// 开关关闭
	public static final String SWITCH_OFF = "OFF";

	@Value("${url}")
	private String url;

	@Value("${md5.signKey}")
	private String md5SignKey;

	@Value("${ce_validate_switch}")
	private String ceValidateSwitch;
	
	@Value("${url4ZY}")
	private String url4ZY;
	
	@Value("${md5.signKey4ZY}")
	private String md5SignKey4ZY;
	
	@Value("${urlUPIC}")
	private String urlUPIC;
	
	
	@Value("${urlZs}")
	private String urlZs;
	
	@Value("${urlLcbLogin}")
	private  String urlLcbLogin;
	
	@Value("${urlLcbLogin_Pc}")
	private  String urlLcbLoginPc;
	

	@Value("${urlReport}")
	private String urlReport;
	
	@Value("${urlUploadReport}")
	private String urlUploadReport;
	
	@Value("${messagePushUrl}")
	private String messagePushUrl;

	@Value("${urlAppCore}")
	private String urlAppCore;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMd5SignKey() {
		return md5SignKey;
	}

	public void setMd5SignKey(String md5SignKey) {
		this.md5SignKey = md5SignKey;
	}

	public String getCeValidateSwitch() {
		return ceValidateSwitch;
	}

	public void setCeValidateSwitch(String ceValidateSwitch) {
		this.ceValidateSwitch = ceValidateSwitch;
	}

	public String getUrl4ZY() {
		return url4ZY;
	}

	public void setUrl4ZY(String url4zy) {
		url4ZY = url4zy;
	}

	public String getMd5SignKey4ZY() {
		return md5SignKey4ZY;
	}

	public void setMd5SignKey4ZY(String md5SignKey4ZY) {
		this.md5SignKey4ZY = md5SignKey4ZY;
	}

	public String getUrlUPIC() {
		return urlUPIC;
	}

	public void setUrlUPIC(String urlUPIC) {
		this.urlUPIC = urlUPIC;
	}

	public String getUrlZs() {
		return urlZs;
	}

	public void setUrlZs(String urlZs) {
		this.urlZs = urlZs;
	}

	public String getUrlLcbLogin() {
		return urlLcbLogin;
	}

	public void setUrlLcbLogin(String urlLcbLogin) {
		this.urlLcbLogin = urlLcbLogin;
	}

	public String getUrlLcbLoginPc() {
		return urlLcbLoginPc;
	}

	public void setUrlLcbLoginPc(String urlLcbLoginPc) {
		this.urlLcbLoginPc = urlLcbLoginPc;
	}

	public String getUrlReport() {
		return urlReport;
	}

	public void setUrlReport(String urlReport) {
		this.urlReport = urlReport;
	}

	public String getUrlUploadReport() {
		return urlUploadReport;
	}

	public void setUrlUploadReport(String urlUploadReport) {
		this.urlUploadReport = urlUploadReport;
	}

	public String getMessagePushUrl() {
		return messagePushUrl;
	}

	public void setMessagePushUrl(String messagePushUrl) {
		this.messagePushUrl = messagePushUrl;
	}

	public String getUrlAppCore() {
		return urlAppCore;
	}

	public void setUrlAppCore(String urlAppCore) {
		this.urlAppCore = urlAppCore;
	}

	
//	@Value("${urlCg}")
//	private String urlCg;
	
//	@Value("${cgKey}")
//	private  String cgKey;
//	
//	
//	public String getCgKey() {
//		return cgKey;
//	}
//
//	public void setCgKey(String cgKey) {
//		this.cgKey = cgKey;
//	}



}
