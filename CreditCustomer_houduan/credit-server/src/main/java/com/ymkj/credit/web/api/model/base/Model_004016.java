package com.ymkj.credit.web.api.model.base;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotBlank;

import com.google.gson.JsonObject;
import com.ymkj.credit.web.api.model.req.ReqParam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Model_004016 extends ReqParam{
	

			private static final long serialVersionUID = 6059826933544124340L;

			private String name;
			private String channelCode;
			@NotBlank (message = "productCode不能为空")
			private String productCd;
			
			@NotBlank (message = "applyLmt不能为空")
			private String applyLmt;
			
			@NotBlank (message = "applyTerm不能为空")
			private String applyTerm;
			
			//@NotBlank (message = "fristPaymentDate不能为空")
			private String fristPaymentDate;
			
			/*@NotBlank (message = "借款金额不能为空")
			private String money;
			
			@NotBlank (message = "借款期数不能为空")
			private String time;*/
			
			/*@NotBlank (message = "合同来源渠道不能为空")
			private String fundsSources;*/
			
			/*@NotBlank (message = "客户星级不能为空")
			private String custStarLevel;*/
			
			/*@NotBlank (message = "贷款类型编码不能为空")
			private String loanType;*/
			
			/*@NotBlank (message = "费率优惠客户不能为空")
			private String isRatePreferLoan;*/

}
