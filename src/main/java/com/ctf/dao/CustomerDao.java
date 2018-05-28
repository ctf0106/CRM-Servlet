package com.ctf.dao;

import java.util.List;
import java.util.Map;

import com.ctf.entity.Company;
import com.ctf.entity.Customer;
import com.ctf.entity.CustomerVo;

public class CustomerDao{

	
	
	
	public List<Customer> find(Map<String, Object> map) {
		return null;
	}

	public Long getTotal(Map<String, Object> map) {
		return null;
	}

	public int add(Customer customer) {
		return 0;
	}

	public int update(Customer customer) {
		return 0;
	}

	public int delete(Integer id) {
		return id;
	}

	public Customer findById(Integer id) {
		return null;
	}

	public List<Customer> findByCompanyId(int id) {
		return null;
	}

	public CustomerVo findByKhno(String khno) {
		return null;
	}

}
