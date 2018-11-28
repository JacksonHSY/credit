package com.ymkj.credit.mapper;

import com.ymkj.credit.common.entity.IdCardInfo;
import com.ymkj.springside.modules.orm.mybatis.JdMapper;
/**
* DictionaryMapper
* <p/>
* Author: 
* Date: 2017-08-22 10:57:55
* Mail: 
*/
public interface IdCardInfoMapper extends JdMapper<IdCardInfo, String> {
	
	void updateIdCardInfoByCId(IdCardInfo idCard);
	
}
