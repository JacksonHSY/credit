package com.ymkj.credit.common.entity;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.ymkj.springside.modules.orm.AbstractEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 资料基本信息表
 * @author YM10156
 *
 */
@Table(name = "apply_base_info")
@Getter
@Setter
public class ApplyBaseInfo extends AbstractEntity<Long> {
	@Id
    private Long id;

    private String loanNo;

    private String fieldKey;
    
    private String fieldObjValue;
    
    private String fieldObjValueBak;

    private Integer tab;

    private String fieldKeyParent;

    private String state;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;
}