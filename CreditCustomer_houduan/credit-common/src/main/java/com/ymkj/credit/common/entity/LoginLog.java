package com.ymkj.credit.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ymkj.springside.modules.orm.AbstractEntity;

/**
 * 记录登入信息表 LoginLog
 * 
 * @author huangsy
 *
 */
@Table(name = "loginLog")
@Entity
public class LoginLog extends AbstractEntity<Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7120694831518117744L;
	/** 主键PK **/
	@Id
	private Long id;
	/** 设备号 **/
	private String deviceId;
	/** 设备类型 **/
	private String type;
	/** 登入时间 **/
	private Date loginDate;
	/** 客户姓名 **/
	private String customerName;
	/** 用户的手机号 **/
	private String phone;
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getLoginDate() {
		return loginDate;
	}
	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
}
