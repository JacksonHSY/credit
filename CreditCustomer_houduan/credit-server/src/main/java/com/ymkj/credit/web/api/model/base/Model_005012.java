package com.ymkj.credit.web.api.model.base;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import com.alibaba.fastjson.JSONObject;
import com.mysql.fabric.xmlrpc.base.Array;
import com.ymkj.credit.common.entity.ImgPicture;
import com.ymkj.credit.web.api.model.req.ReqParam;
@Getter
@Setter
public class Model_005012 extends ReqParam{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6662393499964331151L;
	@NotNull(message="借款编号不能为空")
	private String loanNo;
	
	private String uploadFileList;
	
	private String fieldKey;

}
