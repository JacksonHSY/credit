package com.ymkj.credit.common.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class LoanOrderDto implements Serializable{
	
	private String orderNum;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createTime;
	
	private String customerName;
	
	private String applyLimit;
	
	private String serialNumber;
	
	private String flowStatus;
	
	private String idCard;
	
	private String mobile;
	
	private String city;
	
	private String cityName;
	
	private String beforeTaxSalary;
	
	private String profession;
	
	private String deadline;
	
	private String purpose;
	
	private String purposeName;
	
	private String projectName;
	
	private String applyDate;
}
