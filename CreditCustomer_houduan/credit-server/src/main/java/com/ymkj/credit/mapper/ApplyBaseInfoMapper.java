package com.ymkj.credit.mapper;

import java.util.List;

import com.ymkj.credit.common.entity.ApplyBaseInfo;
import com.ymkj.springside.modules.orm.mybatis.JdMapper;

public interface ApplyBaseInfoMapper extends JdMapper<ApplyBaseInfo, Long>{
	/**根据appNo查询个人信息信息*/
	public ApplyBaseInfo selectByappNo(ApplyBaseInfo applyBaseInfo);
	
	/**根据实体类查询基本信息*/
	public List<ApplyBaseInfo> selectByFiledKeyAndTab(ApplyBaseInfo applyBaseInfo);
	
	public void updateByloanNoAndFiledKey(ApplyBaseInfo applyBaseInfo);
	/**更新联系人和保单信息*/
	public void updateByloanNoAndFiledKeyForContact(ApplyBaseInfo applyBaseInfo);
	
	public void deleteByFiledKey(ApplyBaseInfo applyBaseInfo);
		
	public void insertByLoanAndKey(ApplyBaseInfo applyBaseInfo);
	
	public int updateToTable(ApplyBaseInfo applyBaseInfo);
	
	public List<ApplyBaseInfo> selectBaseTable(ApplyBaseInfo applyBaseInfo);
	
	public ApplyBaseInfo selectBaseOne(ApplyBaseInfo applyBaseInfo);
	
	public void deleteByLoan(ApplyBaseInfo applyBaseInfo);
}