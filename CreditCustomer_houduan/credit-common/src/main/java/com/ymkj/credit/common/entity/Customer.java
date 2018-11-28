package com.ymkj.credit.common.entity;

import java.util.Date;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import com.ymkj.springside.modules.orm.AbstractEntity;

/**
* Customer
* <p/>
* Author: 
* Date: 2017-08-25 14:35:03
* Mail: 
*/
@Table(name = "customer")
@Getter
@Setter
public class Customer extends AbstractEntity<Long> {
    @Id
    private Long id;

    /**
    * 客户姓名
    */
    private String customerName;

    /**
    * 手机号
    */
    private String phone;
    
    private String phoneBak;

    /**
    * 身份证
    */
    private String idCard;

    private String idCardBak;
    /**
    * 密码
    */
    private String password;

    /**
    * 手势密码
    */
    private String gesturePassword;

    /**
    * 客户经理工号
    */
    private String accountManagerNo;

    /**
    * 营业部ID
    */
    private Long busiId;

    /**
    * 业务状态（001001:新建;001002:身份校验通过;001003工号校验通过;001004:注册）
    */
    private String flowStatus;

    /**
    * 设备号
    */
    private String deviceNum;

    /**
     * 消息推送唯一标识
     */
    private String pushId;
    
    /**
     * 手势密码开关
     */
     private String gestureSwitch;

    /**
    * 0:未删除;1:已删除
    */
    private String status;

    /**
    * 创建时间
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
    * 更新时间
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
    * 备注
    */
    private String memo;

    /**
     * 平台
     */
    private String platform;
    
    /**
     * 捞财宝登录账号
     */
    private String lcbAccount;
    private String lcbAccountBak;
    /**
     * 身份证有效期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date idCardBeginDate;
    
    /**
     * 身份证有效期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date idCardEndDate;
    
    
    //临时字段
    @Transient
    private String beginDate;//开始时间
    @Transient
    private String endDate;//结束时间
}