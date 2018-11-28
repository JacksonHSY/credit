package com.ymkj.credit.service;

import com.alibaba.fastjson.JSONObject;
import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.Dictionary;
import com.ymkj.credit.common.untils.CacherContainer;
import com.ymkj.credit.mapper.DictionaryMapper;
import com.ymkj.credit.web.api.model.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 
 * @Description：对类进行描述
 * @ClassName: DictionaryService.java
 * @Author：tianx
 * @Date：2017年8月24日
 * -----------------变更历史-----------------
 * 如：who  2017年8月24日  修改xx功能
 */
@Slf4j
@Service
public class DictionaryService{

  @Autowired
  private DictionaryMapper dictionaryMapper;

  /**
   *
   */
  public void init(){

    //获取所有分类data_type
    List<Dictionary> dateTypes=dictionaryMapper.queryAllType();
    if(null != dateTypes && dateTypes.size()>0){
      for (Dictionary dic : dateTypes){
        Dictionary query = new Dictionary();
        query.setStatus(Constants.DATA_VALID);
        query.setDataType(dic.getDataType());
        List<Dictionary> dics=dictionaryMapper.select(query);
        if(null != dics && dics.size()>0){
          CacherContainer.sysSysParameterMap.put(dic.getDataType(),dics);
        }
      }
    }

  }
  /**
   * 从文件中读取生成好的省市区数据
   * @author YM10159
   */
  public Map<String,Object> getProvicesList() {
    InputStream is = getClass().getResourceAsStream("/mapper/area.json");
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    StringBuffer sb = new StringBuffer();
    String lineStr = "";
    try {
      while ((lineStr = br.readLine()) != null)
        sb.append(lineStr.replaceAll("\\s", ""));
    } catch (Exception e) {
      log.error("APP读取文件省市区数据异常：", e);
    }finally {
      try {   
        is.close();
        br.close();
      } catch (IOException e) {}
    }
    JSONObject json = JSONObject.parseObject(sb.toString());
    return json;
  }
  
  /**
   * 从文件中读取协议文本
   */
  public Result getProtocol(){
	  InputStream is = getClass().getResourceAsStream("/messages/protocol.html");
	    BufferedReader br = new BufferedReader(new InputStreamReader(is));
	    StringBuffer sb = new StringBuffer();
	    String lineStr = "";
	    try {
	      while ((lineStr = br.readLine()) != null)
	        sb.append(lineStr.replaceAll("\\s", "").replaceAll("\"", "\\\""));
	    } catch (Exception e) {
	      log.error("APP读取协议文本数据异常：", e);
	    }finally {
	      try {   
	        is.close();
	        br.close();
	      } catch (IOException e) {}
	    }
	    Map<String,Object> map = new HashMap<String,Object>();
	    map.put("protocol", sb.toString());
	    return Result.success(map);
  }
}