package com.ymkj.credit.common.entity;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.springframework.format.annotation.DateTimeFormat;

import com.ymkj.springside.modules.orm.AbstractEntity;


/**
* RateCalculation
* <p/>
* Author: 
* Date: 2017-08-24 17:17:04
* Mail: 
*/
@Table(name = "rateCalculation")
@Getter
@Setter
public class RateCalculation extends AbstractEntity<Long> {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -5828000359776542472L;

	@Id
    private Long id;

	private String operater;

	private String productName;
	
	private String minAmount;
	
	private String maxAmount;
	
	private String term;
	
	private String rate;
	
	private String channel;
	
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

}