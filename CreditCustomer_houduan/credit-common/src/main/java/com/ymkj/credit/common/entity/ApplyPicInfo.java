package com.ymkj.credit.common.entity;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.ymkj.springside.modules.orm.AbstractEntity;

import lombok.Getter;
import lombok.Setter;
/**
 * 图片信息表
 * @author YM10156
 *
 */
@Table(name = "apply_pic_info")
@Getter
@Setter
public class ApplyPicInfo extends AbstractEntity<Long>{
	
	@Id
    private Long id;
	
	private String loanNo;

    private String picId;

    private String picUrl;

    private String fieldKey;

    private String status;
    
    private String imgName;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}