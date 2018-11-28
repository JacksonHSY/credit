package com.ymkj.credit.mapper;

import com.ymkj.credit.common.entity.Dictionary;
import com.ymkj.credit.web.api.dto.ImageDto;
import com.ymkj.springside.modules.orm.mybatis.JdMapper;

import java.util.List;
import java.util.Map;

/**
* DictionaryMapper
* <p/>
* Author: 
* Date: 2017-08-22 10:57:55
* Mail: 
*/
public interface DictionaryMapper extends JdMapper<Dictionary, Long> {

  List<Dictionary> queryAllType();

  Dictionary getImageUrl(Map<String, Object> map);
  
}