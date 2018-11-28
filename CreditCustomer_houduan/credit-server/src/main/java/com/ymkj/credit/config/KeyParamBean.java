package com.ymkj.credit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyParamBean {

    @Value("${rsa.keyPath}")
    private String keyPath;

    @Value("${rsa.privateKeyFileName}")
    private String privateKeyFileName;

    @Value("${rsa.publicKeyFileName}")
    private String publicKeyFileName;

    @Value("${rsa.keyGeneratorRandom}")
    private String keyGeneratorRandom;

    @Value("${md5.signKey}")
    private String md5SignKey;

    @Value("${reqUrlValidTime}")
    private int reqUrlValidTime;
    
    @Value("${sys.appKey}")
	private String appKey;

    public String getKeyPath() {
        return keyPath;
    }

    public void setKeyPath(String keyPath) {
        this.keyPath = keyPath;
    }

    public String getPrivateKeyFileName() {
        return privateKeyFileName;
    }

    public void setPrivateKeyFileName(String privateKeyFileName) {
        this.privateKeyFileName = privateKeyFileName;
    }

    public String getPublicKeyFileName() {
        return publicKeyFileName;
    }

    public void setPublicKeyFileName(String publicKeyFileName) {
        this.publicKeyFileName = publicKeyFileName;
    }

    public String getKeyGeneratorRandom() {
        return keyGeneratorRandom;
    }

    public void setKeyGeneratorRandom(String keyGeneratorRandom) {
        this.keyGeneratorRandom = keyGeneratorRandom;
    }

    public String getMd5SignKey() {
        return md5SignKey;
    }

    public void setMd5SignKey(String md5SignKey) {
        this.md5SignKey = md5SignKey;
    }

    public int getReqUrlValidTime() {
        return reqUrlValidTime;
    }

    public void setReqUrlValidTime(int reqUrlValidTime) {
        this.reqUrlValidTime = reqUrlValidTime;
    }

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
    
}
