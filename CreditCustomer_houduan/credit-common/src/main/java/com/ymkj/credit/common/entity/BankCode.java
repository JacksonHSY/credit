package com.ymkj.credit.common.entity;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.ymkj.springside.modules.orm.AbstractEntity;

import lombok.Getter;
import lombok.Setter;

@Table(name = "bank_code")
@Getter
@Setter
public class BankCode extends AbstractEntity<Long>{

	@Id
	private Long id;

    private String bankCode;

    private String bankName;

    private String bank;

    private Integer status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}