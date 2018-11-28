package com.ymkj.credit.service;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.Customer;
import com.ymkj.credit.common.entity.EquipmentInfo;
import com.ymkj.credit.mapper.IEquipmentInfoMapper;
@Slf4j
@Service
public class EquipmentInfoService {
	@Autowired
	private IEquipmentInfoMapper iequipmentinfomapper;
	
	 public void insert(EquipmentInfo equipmentinfo) {
	        iequipmentinfomapper.insertTable(equipmentinfo);
	    }

}
