package com.ymkj.credit.common.entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerPicture {
	/**id*/
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
	
}
