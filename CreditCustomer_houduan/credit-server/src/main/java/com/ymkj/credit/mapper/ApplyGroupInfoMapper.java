package com.ymkj.credit.mapper;

import java.util.List;

import com.ymkj.credit.common.entity.ApplyGroupInfo;
import com.ymkj.springside.modules.orm.mybatis.JdMapper;

public interface ApplyGroupInfoMapper extends JdMapper<ApplyGroupInfo, Long>{
	
	public List<ApplyGroupInfo> queryByApplyGroupParentCode(ApplyGroupInfo applyGroupInfo);
	
	public String queryByApplyGroupInfoState(ApplyGroupInfo applyGroupInfo);
	
	public void updateByGroupId(ApplyGroupInfo applyGroupInfo);
	
	public ApplyGroupInfo queryById(ApplyGroupInfo applyGroupInfo);
	
	public List<ApplyGroupInfo> queryByLoan(ApplyGroupInfo applyGroupInfo);
	
	public void updateMsg(ApplyGroupInfo applyGroupInfo);
	
	public List<ApplyGroupInfo> queryApplyGroupInfoAll(ApplyGroupInfo applyGroupInfo);


}