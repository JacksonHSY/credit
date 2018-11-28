package com.ymkj.credit.web.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto extends BaseDTO {

private static final long serialVersionUID = 1383793441301242364L;
	
	/**
	 * id
	 */
	private String title;
	
	/**
	 * 推送内容
	 */
	private String  content;
	/**
	 * 推送时间
	 */
	private String  createTime;
}
