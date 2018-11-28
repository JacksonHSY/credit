package com.ymkj.credit.common.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.ymkj.springside.modules.orm.AbstractEntity;

import lombok.Getter;
import lombok.Setter;

@Table(name = "apply_contract_info")
@Getter
@Setter
public class ApplyContractInfo extends AbstractEntity<Long> {
	
	@Id
    private Long id;

    private String idNo;

    private String pdfId;

    private String status;

    private String pdfUrl;
    
    private String code;
    
    private String isNotSign;
    
    private String fileName;
    
    private String saveDirectory;
    
    private String contractName;
    
    private String imageUrl;
    
    private String idNoBak;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    
    @Transient
    private String[] idNos;

}