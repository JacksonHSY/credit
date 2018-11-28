package com.ymkj.credit.mapper;

import java.util.List;

import com.ymkj.credit.common.entity.ApplyGroup;
import com.ymkj.springside.modules.orm.mybatis.JdMapper;

public interface ApplyGroupMapper extends JdMapper<ApplyGroup, Long>{
	
	public List<String> queryByApplyCode(ApplyGroup applyGroup);
	public ApplyGroup selectByGroupCode(ApplyGroup applyGroup);
}