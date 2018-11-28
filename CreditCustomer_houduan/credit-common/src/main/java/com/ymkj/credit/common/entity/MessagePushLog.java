package com.ymkj.credit.common.entity;


import com.ymkj.springside.modules.orm.AbstractEntity;

import lombok.Getter;
import lombok.Setter;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.util.Date;

/**
* MessagePushLog
* <p/>
* Author: 
* Date: 2017-09-11 14:34:27
* Mail: 
*/
@Table(name = "message_push_log")
@Getter
@Setter
public class MessagePushLog extends AbstractEntity<Long> {

    @Id
    private Long id;

    /**
    * 
    */
    private String idCard;
    private String idCardBak;

    /**
    * 
    */
    private String content;

    /**
    * 
    */
    private String batchNo;

    /**
    * 
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pushTime;

    /**
    * 
    */
    private String pushId;

    /**
    * 
    */
    private String returnResult;

    /**
    * 
    */
    private String returnMessage;
    /**
     * 
     */
     private String title;
     
     @Transient
     private String createTimeStr;
     
     @Transient
     private String customerName;
     
     @Transient
     private String phone;
     
     @Transient
     private String  beginDate;
     
     @Transient
     private String  endDate;
     
}