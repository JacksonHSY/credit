package com.ymkj.credit.mapper;

import com.ymkj.credit.common.entity.BankInfo;
import com.ymkj.credit.common.entity.EquipmentInfo;
import com.ymkj.springside.modules.orm.mybatis.JdMapper;

public interface IEquipmentInfoMapper extends JdMapper<EquipmentInfo, String>{
	public int insertTable(EquipmentInfo equipmentInfo);

}
