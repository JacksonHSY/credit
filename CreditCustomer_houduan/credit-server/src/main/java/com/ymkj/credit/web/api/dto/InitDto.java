package com.ymkj.credit.web.api.dto;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.ymkj.credit.common.entity.Dictionary;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class InitDto {
	
	private static final long serialVersionUID = 1383793441301242364L;
	
	/**
	 * 所在城市
	 */
	private Map<String,Object> city;
	
	/**
	 * 用户信息
	 */
	private CustomerDto customerDto;
	/**
	 * 贷款用途
	 */
	private JSONArray purposeList;
	/**
	 *资产列表
	 */
	private List<Dictionary> propertyList;
	/**
	 *职业信息列表
	 */
	private JSONArray professionList;
	
	private JSONArray deadlineList;//借款期限

	private JSONArray productListData;//产品
	
//	private JSONArray channelListData;//渠道
	
	private String isAuditOver;//标识是否审核通过, 默认为"0", 审核通过则为"1"
	
	private String nextExpiry;//下一还款日
	
	private String showProductName;//试算显示的产品名称
	
	private String showProductCode;//试算显示的产品code
	
	private String 	applyTerm;//试算期限
}
