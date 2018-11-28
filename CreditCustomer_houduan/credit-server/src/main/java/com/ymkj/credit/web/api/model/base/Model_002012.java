package com.ymkj.credit.web.api.model.base;


import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

import com.ymkj.credit.web.api.model.req.ReqParam;

@Getter
@Setter
public class Model_002012  extends ReqParam{

	private static final long serialVersionUID = 1110415818855632727L;

    @NotBlank(message="身份证号不能为空")
    @Pattern(regexp = "(^\\d{17}[\\w\\d]$)|(^\\d{15}$)", message = "身份证长度错误")
    private String idCard;
    
    private String phone;
    
}
