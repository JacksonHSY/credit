package com.ymkj.credit.mapper;

import java.util.List;

import com.ymkj.credit.common.entity.ApplyContractInfo;
import com.ymkj.springside.modules.orm.mybatis.JdMapper;

public interface ApplyContractInfoMapper  extends JdMapper<ApplyContractInfo, Long>{
	
	public List<ApplyContractInfo> queryByIdNos(ApplyContractInfo aci);
	
	public int updateByPrimaryKeyTable(ApplyContractInfo aci);
	
	public int insertTable(ApplyContractInfo aci);
	
	public ApplyContractInfo selectContactTableOne(ApplyContractInfo aci);
	
	public List<ApplyContractInfo> selectContactTable(ApplyContractInfo aci);

}