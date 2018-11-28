package com.ymkj.credit.web.api.dto;

import lombok.Getter;
import lombok.Setter;

import com.ymkj.credit.web.api.model.req.ReqParam;

@Getter
@Setter
public class NoticeDto extends ReqParam{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2617820508475889675L;

	private String title;
	
	private String content;
	
	private String createTime;
	
}
