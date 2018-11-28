package com.ymkj.credit.common.entity;

import java.util.Date;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import com.ymkj.springside.modules.orm.AbstractEntity;

/**
* BusinessDepart
* <p/>
* Author: 
* Date: 2017-08-24 17:16:46
* Mail: 
*/
@Table(name = "business_depart")
@Getter
@Setter
public class BusinessDepart extends AbstractEntity<Long> {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 62612565177233081L;

	@Id
    private Long id;

    /**
    * 营业部名称
    */
    private String name;

    /**
    * 营业部电话
    */
    private String phone;

    /**
    * 营业部地址
    */
    private String address;

    /**
    * 营业部经度
    */
    private String longitude;

    /**
    * 营业部纬度
    */
    private String latitude;

    /**
    * 删除标识(0:已删除;1:未删除)
    */
    private String status;

    /**
    * 创建日期
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
    * 修改日期
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
    * 备注
    */
    private String memo;

}