package com.ymkj.credit.web.api.model.base;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotBlank;

import com.ymkj.credit.web.api.model.req.ReqParam;

import javax.validation.constraints.Pattern;

@Getter
@Setter
public class Model_002001 extends ReqParam{

	private static final long serialVersionUID = 6964678828133095263L;
	
	@NotBlank(message="身份标识不能为空")
//    @Pattern(regexp = "^1//d{10}$", message = "身份标识格式错误")
    private String identityNo;

//    @NotBlank(message="密码不能为空")
    private String password;
    
    private String validateCode;
    
    @NotBlank(message="登录类型不能为空")
    private String loginType;
    
    /**
     * 推送唯一标识号
     */
    private String pushId;

	
}
