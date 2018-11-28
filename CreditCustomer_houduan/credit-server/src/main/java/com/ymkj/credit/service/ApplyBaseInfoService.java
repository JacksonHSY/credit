package com.ymkj.credit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.ApplyBaseInfo;
import com.ymkj.credit.mapper.ApplyBaseInfoMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 借款获取节点信息
 * @author YM10156
 *
 */
@Slf4j
@Service
public class ApplyBaseInfoService {

	@Autowired
	ApplyBaseInfoMapper applyBaseInfoMapper;
	
	public List<ApplyBaseInfo> applyBaseInfoList(ApplyBaseInfo base){
		base.setState(Constants.DATA_VALID);
		return (List<ApplyBaseInfo>) applyBaseInfoMapper.selectBaseTable(base);
	}
	
	public void updateByappNo(ApplyBaseInfo base){
		applyBaseInfoMapper.updateByloanNoAndFiledKey(base);
	}
	public void deleteByFiledKey(ApplyBaseInfo base){
		applyBaseInfoMapper.deleteByFiledKey(base);
	}
	public ApplyBaseInfo selectByappNo(ApplyBaseInfo base){
		return applyBaseInfoMapper.selectByappNo(base);
	}
	public void insertByLoanAndKey(ApplyBaseInfo base){
		 applyBaseInfoMapper.insertByLoanAndKey(base);
	}
	
}
