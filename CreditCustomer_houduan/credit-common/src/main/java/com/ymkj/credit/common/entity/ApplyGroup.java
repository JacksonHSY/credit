package com.ymkj.credit.common.entity;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.ymkj.springside.modules.orm.AbstractEntity;

import lombok.Getter;
import lombok.Setter;

@Table(name = "apply_group")
@Getter
@Setter
public class ApplyGroup extends AbstractEntity<Long> {
	
	@Id
    private Long id;

    private String groupCode;

    private String groupName;

    private String groupParentCode;
    
    private String state;

    private String creator;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;
    
}