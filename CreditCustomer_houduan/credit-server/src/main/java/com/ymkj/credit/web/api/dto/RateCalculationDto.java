package com.ymkj.credit.web.api.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import com.ymkj.credit.common.entity.Dictionary;
@Getter
@Setter
public class RateCalculationDto {
	
    private static final long serialVersionUID = 1383793441301242364L;
	
	/**
	 * 费率计算结果列表
	 */
	private ArrayList<Map<String,Object>>  trialBeforeCreditListData;
	
	/**
	 * 期数
	 */
	private Number applyTermNo;
	/**
	 * 还款日
	 */
	private String paymentDate;
	/**
	 *还款金额
	 */
	private Number paymentMoney;
	/**
	 *当期一次性还款金额
	 */
	private Number PaymentAllMoney;
}
