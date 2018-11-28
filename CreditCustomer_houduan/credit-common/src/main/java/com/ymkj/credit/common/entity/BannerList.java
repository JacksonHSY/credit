package com.ymkj.credit.common.entity;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

import com.ymkj.springside.modules.orm.AbstractEntity;
@Table(name = "picture")
@Getter
@Setter
public class BannerList extends AbstractEntity<Long>{

	/**主键*/
	@Id
	private Long id;
	
	/**缩略图*/
	private String picture;
	
	/**关键字*/
	private String codekey;
	
	/**链接地址*/
	private String url;
	
	/**是否启用*/
	private String status;
	
	/**序号*/
	private Long num;
	
	/**上传时间*/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date creatTime;
	
	/**更新时间*/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	
	@Transient
    private String  startDate;
    
    @Transient
    private String  endDate;
    @Transient
    private Long  numById;

}
