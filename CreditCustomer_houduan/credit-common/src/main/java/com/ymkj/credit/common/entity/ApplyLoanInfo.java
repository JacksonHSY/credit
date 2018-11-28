package com.ymkj.credit.common.entity;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.ymkj.springside.modules.orm.AbstractEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 借款申请
 * @author YM10156
 *
 */
@Table(name = "apply_loan_info")
@Getter
@Setter
public class ApplyLoanInfo extends AbstractEntity<Long>{
	
	@Id
    private Long id;

    private String idCard;
    
    private String idCardBak;

    private String loanNo;
    
    private String fieldKey;
    
    private String fieldValue;
    
    private String fieldValueBak;

    private String status;

    private String applyStatus;
    
    private String type;
    
    private String zmScore;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    
    //适配借款进度字段
     @Transient
     private String ordernum;
     @Transient
     private String applylimit;
     @Transient
     private String deadline;
     @Transient
     private String isRed;
     @Transient
     private String flowstatus;
     @Transient
     private String isProject;
     @Transient
     private String projectName;
     @Transient
     private String projectCode;
     @Transient
     private String flowstatusValue;//借款进度单状态值
     @Transient
     private String btnStatus;//设置按钮的状态值
     @Transient
     private String isShowStatus;//是否显示状态值
     @Transient
     private String jumpUrl;//对应按钮状态的跳转url
     @Transient
     private String isShowModifyCard;//是否显示更改银行卡
     @Transient
     private String applyDate;//申请时间
     

}