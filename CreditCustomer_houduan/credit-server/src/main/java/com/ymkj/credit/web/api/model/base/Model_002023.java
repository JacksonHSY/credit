package com.ymkj.credit.web.api.model.base;




import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

import com.ymkj.credit.web.api.model.req.ReqParam;
/**
 * 发送短信验证码
 * 
 * 
 * @author changj@yuminsoft.com
 * @date2018年4月13日
 * @version 1.0
 */
@Getter
@Setter
public class Model_002023  extends ReqParam{

	private static final long serialVersionUID = 1110415818855632727L;
	
	  @NotBlank(message="手机号不能为空")
      @Size(max = 11,min = 11, message = "手机号长度错误")
      @Pattern(regexp = "^1[\\d]{10}", message = "手机号格式错误")
	  private String mobile;
	 
}
