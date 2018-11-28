package com.ymkj.credit.mapper;

import java.util.List;

import com.github.pagehelper.Page;
import com.ymkj.credit.common.entity.Customer;
import com.ymkj.springside.modules.orm.mybatis.JdMapper;

/**
* CustomerMapper
* <p/>
* Author: 
* Date: 2017-08-21 14:33:20
* Mail: 
*/
public interface CustomerMapper extends JdMapper<Customer, Long> {
	
	Page<Customer> selectPageByColumn(Customer customer);
	
	List<Customer> selectListColumn(Customer customer);
	public int insertTable(Customer customer);
	public int updateByPrimaryKeyTable(Customer customer);
	
	public List<Customer> selectByExampleTable(Customer customer);
	public List<Customer> selectByExampleByPhone(Customer cus);
	public Customer selectOneTable(Customer customer);
}