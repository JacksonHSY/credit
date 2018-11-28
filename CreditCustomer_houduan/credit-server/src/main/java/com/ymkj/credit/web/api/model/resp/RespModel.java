package com.ymkj.credit.web.api.model.resp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import com.ymkj.credit.web.api.model.req.ReqParam;
import net.sf.json.JSONObject;

@Getter
@Setter
public class RespModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2340330120901978600L;

	private String resCode;
	
	private String resMsg;
	
	private JSONObject attachment;
}
