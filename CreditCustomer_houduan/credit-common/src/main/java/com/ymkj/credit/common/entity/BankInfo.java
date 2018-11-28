package com.ymkj.credit.common.entity;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.springframework.format.annotation.DateTimeFormat;

import com.ymkj.springside.modules.orm.AbstractEntity;

/**
* BankInfo
* <p/>
* Author: 
* Date: 2018-02-28 10:23:46
* Mail: 
*/
@Table(name = "bank_info")
@Getter
@Setter
public class BankInfo extends AbstractEntity<String> {
    @Id
    private String id;

    /**
    * 
    */
    private Long cid;

    /**
    * 
    */
    private String bankName;

    /**
    * 
    */
    private String subbranch;

    /**
    * 
    */
    private String accName;

    /**
    * 
    */
    private String accNo;

    private String accNoBak;
    /**
    * 
    */
    private String phone;

    private String phoneBak;
    /**
    * 
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
    * 
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /**
     * 
     */
    private String simageUrl;
     /**
      * 
      */
    private String gimageUrl;
    /**
     * 
     */
     private String status;
    /**
    * 
    */
    private String memo;
    
    

}