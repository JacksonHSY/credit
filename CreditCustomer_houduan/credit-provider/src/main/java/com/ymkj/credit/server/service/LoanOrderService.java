package com.ymkj.credit.server.service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ymkj.credit.api.resp.Response;
import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.dto.LoanOrderDto;
import com.ymkj.credit.common.entity.LoanOrder;
import com.ymkj.credit.common.util.PageUtils;
import com.ymkj.credit.server.mapper.LoanOrderMapper;
import com.ymkj.springside.modules.orm.PageInfo;


/**
 * 
 * 
 * @author changj@yuminsoft.com
 * @date2018年1月5日
 * @version 1.0
 */
@Service
@Slf4j
public class LoanOrderService {

	@Autowired
	private LoanOrderMapper loanOrderMapper;
	/**
	 * 分页查询
	 * 
	 * @TODO
	 * @param map
	 * @return PageInfo<LoanOrderDto>
	 * @author changj@yuminsoft.com
	 * @date2018年1月5日
	 */
	public PageInfo<LoanOrderDto> getPage(Map<String, String> map) {
		PageHelper.startPage(Integer.parseInt(map.get("pageNo")),Integer.parseInt(map.get("pageSize")));
		Page<LoanOrderDto> page;
		if (StringUtils.isNotBlank(map.get("orderNum"))) {
			map.put("orderNum", map.get("orderNum"));
		}
		if (StringUtils.isNotBlank(map.get("accountManagerNo"))) {
			map.put("accountManagerNo", map.get("accountManagerNo"));
		}
		if (StringUtils.isNotBlank(map.get("customerName"))) {
			map.put("customerName", map.get("customerName"));
		}
//		Set<String> statusSet = new HashSet<String>();
//		statusSet.add("0");
//		statusSet.add("1");
//		statusSet.add("2,3");
//		statusSet.add("3");
//		statusSet.add("9");
//		if (statusSet.contains(map.get("orderStatus"))) {
//		map.put("orderStatus", map.get("orderStatus"));
//		}else{
//			map.remove("orderStatus");
//		}
		log.info("查询申请单入参:"+map.toString());
		page = (Page<LoanOrderDto>) loanOrderMapper.getPage(map);
		return PageUtils.convertPage(page);
	}
	
	/**
	 * updateLoanOrder
	 * @TODO
	 * @param map
	 * @return
	 * Response
	 * @author changj@yuminsoft.com
	 * @date2018年1月5日
	 */
	public Response updateLoanOrder(Map<String, String> map) {
		log.info("展业更新申请单入参=="+map.toString());
		LoanOrder order = new LoanOrder();
		if (StringUtils.isNotBlank(map.get("orderNum"))) {
			order.setOrdernum(map.get("orderNum"));
		}else{
			Response.fail("orderNum canNot null");
		}
//		if (StringUtils.isNotBlank(map.get("serialNumber"))) {
//			order.setSerialNumber(map.get("serialNumber"));
//		}
		if (StringUtils.isNotBlank(map.get("flowstatus"))) {
			order.setFlowstatus(map.get("flowstatus"));
		}
		if (StringUtils.isNotBlank(map.get("isRed"))) {
			order.setIsRed(map.get("isRed"));
		}
		if (StringUtils.isNotBlank(map.get("isProject"))) {
			order.setIsProject(map.get("isProject"));
		}
		if (StringUtils.isNotBlank(map.get("projectName"))) {
			order.setProjectName(map.get("projectName"));
		}
		if (StringUtils.isNotBlank(map.get("reason"))) {
			order.setReason(map.get("reason"));
		}
		if (StringUtils.isNotBlank(map.get("memo"))) {
			order.setMemo(map.get("memo"));
		}
		if (StringUtils.isNotBlank(map.get("deadline"))) {
			order.setDeadline(map.get("deadline"));
		}
		if (StringUtils.isNotBlank(map.get("applylimit"))) {
			order.setApplylimit(map.get("applylimit"));
		}
		if(Constants.ORDER_STATES_BANLIZHONG.equals(map.get("flowstatus"))){//同意放款后移动身份证信息至借款编号文件夹下
			order.setSerialNumber(map.get("orderNum"));
		}
		order.setUpdatetime(new Date());
		int count = loanOrderMapper.updateLoanOrder(order);
		if(count!=1){
			return Response.fail("更新失败");
		}
		return Response.success("操作成功");
	}
	public static void main(String []sss){
		System.out.println("".equals(""));
	}
}