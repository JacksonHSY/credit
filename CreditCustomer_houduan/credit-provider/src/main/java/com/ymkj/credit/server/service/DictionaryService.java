package com.ymkj.credit.server.service;

import java.util.HashMap;
import java.util.List;





import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.Dictionary;
import com.ymkj.credit.server.mapper.DictionaryMapper;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

/**
 * 
 * @Description：对类进行描述
 * @ClassName: DictionaryService.java
 * @Author：tianx
 * @Date：2017年8月24日
 * -----------------变更历史-----------------
 * 如：who  2017年8月24日  修改xx功能
 */
@Service
public class DictionaryService{

  @Autowired
  private DictionaryMapper dictionaryMapper;
  
  /**
   *
   */
  public Map<String,String>  queryByType(String type) {
		Example example = new Example(Dictionary.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("status",Constants.DATA_VALID);
		criteria.andEqualTo("dataType",type);
		List<Dictionary> dictionaryList = dictionaryMapper.selectByExample(example);
		Map<String,String> map = new HashMap<String, String>();
		if(dictionaryList!=null && dictionaryList.size()>0){
			for(Dictionary dic:dictionaryList){
				map.put(dic.getDataValue(), dic.getDataName());
			}
		}
		return map;
	}
}