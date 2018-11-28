package com.ymkj.credit.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ymkj.springside.modules.orm.AbstractEntity;

/**
 * 设备信息表 EquipmentInfo
 * 
 * @author huangsy
 *
 */
@Table(name = "equipment")
@Entity
public class EquipmentInfo extends AbstractEntity<Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6503142748806253189L;
	/** 主键PK **/
	@Id
	private Long id;
	/** 设备号 **/
	private String deviceId;
	/** 账号 **/
	private String account;
	private String accountBak;
	/** 登入时间 **/
	private Date loginDate;
	
	public String getAccountBak() {
		return accountBak;
	}
	public void setAccountBak(String accountBak) {
		this.accountBak = accountBak;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public Date getLoginDate() {
		return loginDate;
	}
	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}
	

}
