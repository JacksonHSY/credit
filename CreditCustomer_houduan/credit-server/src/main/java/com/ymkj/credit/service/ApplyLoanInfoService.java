package com.ymkj.credit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.ApplyLoanInfo;
import com.ymkj.credit.mapper.ApplyLoanInfoMapper;

import lombok.extern.slf4j.Slf4j;
/**
 * 借款申请
 * @author YM10156
 *
 */
@Slf4j
@Service
public class ApplyLoanInfoService {

	@Autowired
	ApplyLoanInfoMapper applyLoanInfoMapper;
	
	public ApplyLoanInfo selectOne(ApplyLoanInfo info){
		info.setStatus(Constants.DATA_VALID);
		return applyLoanInfoMapper.selectLoanOne(info);
	}
	
	public List<ApplyLoanInfo> queryByApplyLoanInfo(ApplyLoanInfo info){
		info.setApplyStatus(Constants.IS_SUBMIT);
		info.setStatus(Constants.DATA_VALID);
		return applyLoanInfoMapper.queryByApplyLoanInfo(info);
	}
	
	public void updateByLoan (ApplyLoanInfo info){
		applyLoanInfoMapper.updateByLoan(info);
	}
}
