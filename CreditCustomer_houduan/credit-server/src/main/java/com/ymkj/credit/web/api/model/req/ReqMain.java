package com.ymkj.credit.web.api.model.req;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;















import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotBlank;

import com.ymkj.credit.common.untils.FileUploadUtil;
import com.ymkj.credit.common.untils.JSONUtils;
import com.ymkj.credit.web.api.model.base.Model_002015;
import com.ymkj.credit.web.api.model.base.Model_004009;
import com.ymkj.credit.web.api.model.base.Model_004010;
import com.ymkj.credit.web.api.model.base.Model_004011;
import com.ymkj.credit.web.api.model.base.Model_004012;
import com.ymkj.credit.web.api.model.base.Model_004013;
import com.ymkj.credit.web.api.model.base.Model_004018;
import com.ymkj.credit.web.api.model.base.Model_004019;
import com.ymkj.credit.web.api.model.base.Model_004020;
import com.ymkj.credit.web.api.model.base.Model_004021;
import com.ymkj.credit.web.api.model.base.Model_004022;
import com.ymkj.credit.web.api.model.base.Model_004023;
import com.ymkj.credit.web.api.model.base.Model_004024;
import com.ymkj.credit.web.api.validate.ReqHeadValidate;
import com.ymkj.credit.web.api.validate.ReqParamValidate;

public class ReqMain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Valid
	@ReqParamValidate
    private ReqParam reqParam;

    @Valid
	@ReqHeadValidate
    private ReqHeadParam reqHeadParam;

    @NotBlank(message="sn不能为空")
    private String sn;

    private String reqUrl;

    private String projectNo;

    @NotBlank(message="reqTimestamp不能为空")
    private String reqTimestamp;

    public static void main(String[] args) throws Exception {
    	ReqMain rm = new ReqMain();
//   	  	Model_004018 model = new Model_004018();
//	   	 model.setHtmlContent("<!DOCTYPE html><html lang='en'><head> <meta charset='UTF-8'> <title>用户协议</title></head>");
//	     model.setIdCard("610502199110052018");
//	     model.setName("常俊");
    	Model_002015 model = new Model_002015();
    	model.setIdCard("310106197910040017");
    	model.setAccount("6228481200290317812");
    	model.setBankBranchName("大拇指支行");
    	model.setBankName("中国农业银行");
    	model.setBankCode("103");
    	model.setReservedMobile("13524013730");
    	/*model.setCustomerId("2");
    	model.setPageNo(1);
    	model.setPageSize(15);*/
    	
   	    rm.setReqParam(model);
 	    rm.setReqHeadParam(new ReqHeadParam());
 	    
//    	rm.setSn("11111");
//    	rm.setReqTimestamp("2017-08-04");
//    	System.out.println(JSONUtils.toJSON(rm));
 /*       ReqMain rm = new ReqMain();
    	rm.setReqHeadParam(new ReqHeadParam());
    	rm.setSn("11111");
    	rm.setReqTimestamp("2017-08-08");*/
    	System.out.println(JSONUtils.toJSON(rm));
	}

    public ReqHeadParam getReqHeadParam() {
        return reqHeadParam;
    }

    public void setReqHeadParam(ReqHeadParam reqHeadParam) {
        this.reqHeadParam = reqHeadParam;
    }

    public ReqParam getReqParam() {
        return reqParam;
    }

    public void setReqParam(ReqParam reqParam) {
        this.reqParam = reqParam;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getReqUrl() {
        return reqUrl;
    }

    public void setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
    }

    public String getProjectNo() {
        return projectNo;
    }

    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }

    public String getReqTimestamp() {
        return reqTimestamp;
    }

    public void setReqTimestamp(String reqTimestamp) {
        this.reqTimestamp = reqTimestamp;
    }
}