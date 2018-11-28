package com.ymkj.credit.mapper;

import com.ymkj.credit.common.entity.ApplyPicInfo;
import com.ymkj.springside.modules.orm.mybatis.JdMapper;

public interface ApplyPicInfoMapper extends JdMapper<ApplyPicInfo, Long>{
	
	public  void deletePic(ApplyPicInfo info);
}