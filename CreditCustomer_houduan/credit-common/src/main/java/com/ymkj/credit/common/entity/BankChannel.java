package com.ymkj.credit.common.entity;

import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import com.ymkj.springside.modules.orm.AbstractEntity;

/**
* Customer
* <p/>
* Author: 
* Date: 2017-08-25 14:35:03
* Mail: 
*/
@Table(name = "bank_channel")
@Getter
@Setter
public class BankChannel extends AbstractEntity<Long> {
    @Id
    private Long id;
    /**
    * 身份证
    */
    private String idCard;
    
    private String bankCode;
    
    private String flowId;
    
    private String paySysNo;
    
    private String channelName;

}