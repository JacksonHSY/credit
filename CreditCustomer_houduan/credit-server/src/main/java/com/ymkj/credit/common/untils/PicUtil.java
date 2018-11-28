package com.ymkj.credit.common.untils;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.ymkj.credit.common.constants.Constants;

public class PicUtil {
	
	public static boolean uploadfile(String url,String oldNo, String newAppNo,String code){
		//log.info("图片上传入参为："+ model.getLoanNo()+","+model.getUploadFileList()+","+model.getFieldKey()+","+image+","+name);
		Map<String,  String>  queryParas  =  new  HashMap<>();
		queryParas.put("appNo",oldNo);
		queryParas.put("operator","zdqq");
		queryParas.put("jobNumber","zdqq");
		queryParas.put("nodeKey",  "loanApplication");//录单环节
		queryParas.put("sysName",  "app");
	    queryParas.put("dataSources", "0"); //1：pc端，0：app端
	    queryParas.put("subclassSort", code);
	    queryParas.put("newAppNo", newAppNo);
	    try {
	    	 String  result  =  HttpKit.post(url+Constants.PIC_URL_MOVE, queryParas);
	 	    String httpResult = URLDecoder.decode(result, "UTF-8");
	 	    //删除生成的图片
	 		com.alibaba.fastjson.JSONObject objs = com.alibaba.fastjson.JSONObject.parseObject(httpResult);
	 		if(!"000000".equals(objs.get("errorcode"))) {
	 			//log.info(Constants.RESP_CODE_ERROR+"PIC图片系统上传调用失败");
	 			return false;
	 		}
	 		return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	   return false;
	}
}
