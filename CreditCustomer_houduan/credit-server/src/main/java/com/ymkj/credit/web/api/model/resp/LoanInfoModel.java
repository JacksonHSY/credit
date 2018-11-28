package com.ymkj.credit.web.api.model.resp;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import com.ymkj.credit.web.api.model.req.ReqParam;

@Getter
@Setter
public class LoanInfoModel extends ReqParam{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6059826933544124340L;

	private String contractNum;
	
	private BigDecimal pactMoney;
	
	private BigDecimal repayMoney;
	
	private BigDecimal curTermMoney;
	
	private String curRepayDate;
	
	private String applyTime;
	
	private Integer residualTerm;
	
	private Integer term;
	
	private Integer overdueDays;
	
	private String isOverdue;
	
	//1.5.1 新增
	
	private String repayCorpus; //应还本金		逾期本金 + 当期本金
	
	private String repayInterest; //应还利息     逾期利息 + 当期利息
	
	private String repayFine;//应还罚息  逾期罚息
	
	private String loanState;//借款状态  正常、逾期
	
	private String isOffer;//是否正在报盘中   1：是、0：否
	
	private String productName;
	
	private String deptName;//管理营业部名称
	
	private String deptTel;//管理营业部电话
	
	private String isValid;//管理营业部是否营业中
	
	private String longitude;//经度
	
	private String latitude;//纬度
	
	private String address;//经度
	
}
