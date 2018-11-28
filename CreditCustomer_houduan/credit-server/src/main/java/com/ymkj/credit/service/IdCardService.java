package com.ymkj.credit.service;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.untils.HttpKit;
import com.ymkj.springside.modules.exception.BusinessException;

@Slf4j
@Service
public class IdCardService {
	
	@Value("${urlUPIC}")
	private String urlUPIC;
	
	public void moveFile(Map<String,String> queryParas){
		log.info("文件移动操作开始 ,入参:"+queryParas.toString());
		HttpKit test = new HttpKit();
	    String result = test.post(urlUPIC+Constants.PIC_URL_MOVE, queryParas);
	    JSONObject json = JSONObject.fromObject(result);
	    if(!json.getBoolean("isOk")){
	        log.info("=============>>>>>文件移动异常："+json.getString("errormsg"));
            throw new BusinessException(json.getString("errormsg"));
	    }
		log.info("文件移动操作结束,返回内容:"+json.toString());
	}
}
