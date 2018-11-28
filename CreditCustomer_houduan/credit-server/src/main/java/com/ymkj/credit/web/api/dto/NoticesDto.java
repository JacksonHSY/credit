package com.ymkj.credit.web.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.ymkj.credit.web.api.model.req.ReqParam;

@Getter
@Setter
public class NoticesDto extends ReqParam{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3056848902860524292L;

	private Integer total;
	
	private Integer max;
	
	private Integer offset;
	
	private String type;
	
	private List<NoticeDto> noticeList;
}
