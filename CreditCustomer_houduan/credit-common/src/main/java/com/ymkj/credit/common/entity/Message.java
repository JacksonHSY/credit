package com.ymkj.credit.common.entity;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.ymkj.springside.modules.orm.AbstractEntity;

import lombok.Getter;
import lombok.Setter;

@Table(name = "message")
@Getter
@Setter
public class Message extends AbstractEntity<Long> {
	 /**
	 * 
	 */
	private static final long serialVersionUID = -1228519073903368559L;

	@Id
	 private Long id;
	 
	 private String type;
	 
	 private String content;
	 
	 private String status;
	 
	 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	 private Date create_time;
	 
	 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	 private Date update_time;

}
