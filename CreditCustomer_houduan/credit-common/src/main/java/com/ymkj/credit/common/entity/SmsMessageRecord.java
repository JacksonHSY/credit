package com.ymkj.credit.common.entity;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.springframework.format.annotation.DateTimeFormat;

import com.ymkj.springside.modules.orm.AbstractEntity;


/**
 * 短信发送记录
 * 
 * @author changj@yuminsoft.com
 * @date2018年6月5日
 * @version 1.0
 */
@Table(name = "sms_message_record")
@Getter
@Setter
public class SmsMessageRecord extends AbstractEntity<Long> {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -5828000359776542472L;

	@Id
    private Long id;

	private String operater;

	private String customerName;
	
	private String phone;
	
	private String phoneBak;
	
	private String content;
	
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Transient
    private String createTimeStr;
    
    @Transient
    private String  beginDate;
    
    @Transient
    private String  endDate;
}