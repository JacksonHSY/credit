package com.ymkj.credit.server.mapper;

import com.ymkj.credit.common.entity.Dictionary;
import com.ymkj.springside.modules.orm.mybatis.JdMapper;

import java.util.List;

/**
* DictionaryMapper
* <p/>
* Author: 
* Date: 2017-08-22 10:57:55
* Mail: 
*/
public interface DictionaryMapper extends JdMapper<Dictionary, Long> {

  List<Dictionary> queryAllType();
}