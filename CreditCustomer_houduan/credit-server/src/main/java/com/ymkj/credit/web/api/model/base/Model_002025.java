package com.ymkj.credit.web.api.model.base;




import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

import com.ymkj.credit.web.api.model.req.ReqParam;
/**
 * 	客户签章
 * 
 * 
 * @author changj@yuminsoft.com
 * @date2018年4月13日
 * @version 1.0
 */
@Getter
@Setter
public class Model_002025  extends ReqParam{

	private static final long serialVersionUID = 1110415818855632727L;
	
	  @NotBlank(message="借款编号不能为空")
	  private String loanNo;
	  @NotBlank(message="身份证号不能为空")
	  private String idCard;
}
