package com.ymkj.credit.web.api.model.resp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @Description：核心银行卡信息响应对象
 * @ClassName: CustomerInfoModel.java
 * @Author：changj
 * @Date：2017年8月24日
 * -----------------变更历史-----------------
 * 如：who  2017年8月24日  修改xx功能
 */
@Getter
@Setter
public class BankInfoBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7516126195235232258L;

	private String account;//卡号码
	
	private String bankBranchName;//支行
	
	private String bankCode;//code
	
	private String bankName;//开户行
	
	private String checkCard;//是否验卡 00 未验 01  已验
	
	private String generalBankCode;//国内通用的银行code
	
	private String mobile;//手机号
	
	private String masterCard;//是否主卡00主卡，01非主卡
	private List<ChannelInfosBo> channelInfos;//渠道信息
}
