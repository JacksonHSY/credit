package com.ymkj.credit.web.api.dto;

import java.util.Date;

import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadBannerRecord extends BaseDTO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2394965242916758271L;
	
	/**主键*/
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
    private Date  startDate;
    
    @Transient
    private Date  endDate;
	
	

}
