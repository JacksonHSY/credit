package com.ymkj.credit.web.api.model.req;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

public class ReqHeadParam implements Serializable {

	private static final long serialVersionUID = 1L;

	private String token;

    private String sessionToken;

    private String userAgent = "web";

    private String version = "1.0.0";
    
    //机构
    private String mechanism;

    //平台
    @NotBlank(message="平台类型不能为空")
    private String platform;

    //合作类型
    private String togatherType;
	//渠道
    private String openchannel;
    
    //设备号
    @NotBlank(message="设备号不能为空")
    private String deviceNum;
    
    private String customerId;
    
    public String getDeviceNum() {
		return deviceNum;
	}

	public void setDeviceNum(String deviceNum) {
		this.deviceNum = deviceNum;
	}
    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

	public String getMechanism() {
		return mechanism;
	}

	public void setMechanism(String mechanism) {
		this.mechanism = mechanism;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getTogatherType() {
		return togatherType;
	}

	public void setTogatherType(String togatherType) {
		this.togatherType = togatherType;
	}

	public String getOpenchannel() {
		return openchannel;
	}

	public void setOpenchannel(String openchannel) {
		this.openchannel = openchannel;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	
}