package com.ymkj.credit.web.api.model.resp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @Description：核心银行卡渠道信息响应对象
 * @ClassName: CustomerInfoModel.java
 * @Author：changj
 * @Date：2017年8月24日
 * -----------------变更历史-----------------
 * 如：who  2017年8月24日  修改xx功能
 */
@Getter
@Setter
public class ChannelInfosBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7516126195235232258L;

	private String bindChannel;//渠道
	
	private String cardUrl;//银行卡图片地址
	
}
