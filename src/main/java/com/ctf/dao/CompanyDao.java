package com.ctf.dao;

import java.util.List;
import java.util.Map;

import com.ctf.entity.Company;
import com.ctf.entity.Customer;

public class CompanyDao{
	
	
	public List<Company> find(Map<String, Object> map) {
		return null;
		
	}

	public Long getTotal(Map<String, Object> map) {
		return null;
	}

	public int add(Company company) {
		return 0;
	}

	public int update(Company company) {
		return 0;
	}

	public int delete(Integer id) {
		return id;
	}

	public Company findById(Integer id) {
		return null;
	}

	public List<Company> findAllList() {
		return null;
	}

	public List<Customer> findByCompanyId(int parseInt) {
		return null;
	}


}
