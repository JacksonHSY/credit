package com.ymkj.credit.web.api.model.base;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotBlank;

import com.ymkj.credit.web.api.model.req.ReqParam;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class Model_002003 extends ReqParam{

	private static final long serialVersionUID = 6964678828133095263L;

    @NotBlank(message="手机号不能为空")
    @Size(max = 11,min = 11, message = "手机号长度错误")
    @Pattern(regexp = "^1[\\d]{10}", message = "手机号格式错误")
    private String phone;
    @NotBlank(message="密码不能为空")
    private String password;

	
}
