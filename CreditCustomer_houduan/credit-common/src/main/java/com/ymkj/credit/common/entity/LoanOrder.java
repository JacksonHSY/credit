package com.ymkj.credit.common.entity;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.springframework.format.annotation.DateTimeFormat;

import com.ymkj.springside.modules.orm.AbstractEntity;

/**
* LoanOrder
* <p/>
* Author: 
* Date: 2018-01-04 16:54:06
* Mail: 
*/
@Table(name = "loan_order")
@Getter
@Setter
public class LoanOrder extends AbstractEntity<String> {
	
   
    private String id;
    /**
     * 银行卡id
     */
    private String bankInfoId;
    /**
    * 客户ID
    */
    private Long cid;

    /**
    * 
    */
    private String ordernum;

    /**
    * 所在城市
    */
    private String city;

    /**
     * 所在城市名
     */
     private String cityName;
    /**
    * 职业信息
    */
    private String profession;

    /**
    * 税前月收入
    */
    private String beforetaxsalary;

    /**
    * 申请额度
    */
    private String applylimit;

    /**
    * 借款期限
    */
    private String deadline;

    /**
    * 资金用途
    */
    private String purpose;
    
    /**
     * 资金用途名
     */
     private String purposeName;
    /**
    * 月负债
    */
    private String monthliabilities;

    /**
    * 业务员代码
    */
    private String operatorcode;

    /**
    * 所拥有的资产
    */
    private String property;

    /**
    * 0:未删除;1:已删除
    */
    private String status;

    /**
    * 创建时间
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;

    /**
    * 更新时间
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatetime;

    /**
    * 业务状态(0 未受理 1:已受理 2:结束 )
    */
    private String flowstatus;
    
    /**
     * 状态是否标红显示0:不是， 1：是'  NULL,
     */
    private String isRed;
    
    /**
     * 是否有产品0:无， 1：有'  NULL, 
     */
    private String isProject;
    
    /**
     * 产品名称
     */
    private String projectName;
    /**
     * 产品代码
     */
    private String projectCode;
    
    /**
     * 编号
     */
    private String serialNumber;
    
    /**
     * 备注
     */
    private String memo;
    /**
     * 绑定银行卡 开户银行
     */
    private String bankName;
    /**
     * 绑定银行卡支行
     */
    private String subbranch;
    
    /**
     * 绑定银行卡卡号
     */
    private String accNo;
    private String accNoBak;
    /*
     * 绑定银行卡预留手机号
     */
    private String phone;
    private String phoneBak;
    /**
     * 绑定银行卡图片
     */
    private String gimageUrl;
    
    @Transient
    private String applyDate;//申请时间
    
    private String reason;//拒绝放款原因
    
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
    

}