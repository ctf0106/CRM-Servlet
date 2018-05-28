package com.ctf.dao;

import java.util.List;
import java.util.Map;
import com.ctf.entity.User;

public class UserDao{


	public User login(User user) {
		return user;
	}

	public List<User> find(Map<String, Object> map) {
		return null;
	}

	public Long getTotal(Map<String, Object> map) {
		return null;
	}

	public int update(User user) {
		return 0;
	}

	public int add(User user) {
		return 0;
	}

	public int delete(Integer id) {
		return id;
	}

	public User getByUserName(String userName) {
		return null;
	}
}
