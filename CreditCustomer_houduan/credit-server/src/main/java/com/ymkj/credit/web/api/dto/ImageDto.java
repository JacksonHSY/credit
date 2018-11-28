package com.ymkj.credit.web.api.dto;

import java.util.List;

import com.ymkj.credit.common.entity.Dictionary;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageDto extends BaseDTO{
	private static final long serialVersionUID = 1383793441301242364L;
	
	/**
	 * 链接地址
	 */
	private String url;
	
	/**
	 * 跳转链接地址
	 */
	private String  jumpUrl;
	
	/**
	 * 图片名
	 */
	private String  name;
	
	/**
	 * 广告0/活动1
	 */
	private String isActivityPage;	
	
	private String jumpType; //0: 跳网页(默认), 1: 跳本地
	
}
