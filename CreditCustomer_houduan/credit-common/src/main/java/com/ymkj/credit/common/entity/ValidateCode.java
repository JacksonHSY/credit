package com.ymkj.credit.common.entity;

import java.util.Date;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import com.ymkj.springside.modules.orm.AbstractEntity;

/**
* ValidateCode
* <p/>
* Author: 
* Date: 2017-08-24 17:17:04
* Mail: 
*/
@Table(name = "validate_code")
@Getter
@Setter
public class ValidateCode extends AbstractEntity<Long> {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -5828000359776542472L;

	@Id
    private Long id;

    /**
    * 手机号
    */
    private String phone;
    private String phoneBak;

    /**
    * 验证码
    */
    private String smsCode;

    /**
    *  验证码类型 1:注册;2:重置密码
    */
    private String smsType;

    /**
    * 发送次数
    */
    private Integer tryTime;

    /**
    * 0：未删除;1：已删除
    */
    private String status;

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

    /**
    * 
    */
    private String memo;

}