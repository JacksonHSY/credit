package com.ymkj.credit.common.entity;


import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.ymkj.springside.modules.orm.AbstractEntity;

import lombok.Getter;
import lombok.Setter;

@Table(name = "apply_group_info")
@Getter
@Setter
public class ApplyGroupInfo extends AbstractEntity<Long>{
	
	@Id
    private Long id;

    private Long applyGroupId;
    
    private String loanNo;
    
    private String tipMsg;

    private String state;

    private String isEdit;

    private String isRollback;

    private String isRequested;
    
    @Transient
    private String groupCode;
    @Transient
    private String[] groupCodes;
}