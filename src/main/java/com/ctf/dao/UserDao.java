package com.ctf.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ctf.entity.Company;
import com.ctf.entity.PageBean;
import com.ctf.entity.User;
import com.ctf.util.StringUtil;

public class UserDao{


	public User login(Connection con, User user) throws SQLException {
		User resultUser=null;
		String sql="select * from t_user where userName=? and password=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, user.getUserName());
		pstmt.setString(2, user.getPassword());
		ResultSet rs=pstmt.executeQuery();
		if(rs.next()){
			resultUser=new User();
			resultUser.setUserName(rs.getString("userName"));
			resultUser.setPassword(rs.getString("password"));
			resultUser.setTrueName(rs.getString("trueName"));
			resultUser.setPhone(rs.getString("phone"));
			resultUser.setEmail(rs.getString("email"));
		}
		return resultUser;
	}

	public int getTotal(Connection con,User user) throws SQLException {
		int resultCount = 0;
		StringBuffer sb=new StringBuffer("select count(*) as count  from t_user where 1=1 ");
		if(user!=null && StringUtil.isNotEmpty(user.getUserName())){
			sb.append(" and t_user.userName like '"+user.getUserName()+"'");
		}
		PreparedStatement pstmt=con.prepareStatement(sb.toString());
		ResultSet rs=pstmt.executeQuery();
		if(rs.next()){
			resultCount=rs.getInt("count");
		}
		return resultCount;
		
		
	}

	public int update(Connection con,User user) throws SQLException {
		String sql="update t_user set userName=?,password=?,trueName=?,email=?,phone=? where id=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, user.getUserName());
		pstmt.setString(2, user.getPassword());
		pstmt.setString(3, user.getTrueName());
		pstmt.setString(4, user.getEmail());
		pstmt.setString(5, user.getPhone());
		pstmt.setInt(6, user.getId());
		return pstmt.executeUpdate();
	}

	public int add(Connection con,User user) throws SQLException {
		String sql="insert into t_user values(null,?,?,?,?,?)";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, user.getUserName());
		pstmt.setString(2, user.getPassword());
		pstmt.setString(3, user.getTrueName());
		pstmt.setString(4, user.getEmail());
		pstmt.setString(5, user.getPhone());
		return pstmt.executeUpdate();
	}

	public int delete(Connection con,Integer id) throws SQLException {
		String sql="delete from t_user where id=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setInt(1, id);
		return pstmt.executeUpdate();
	}

	public User getByUserName(Connection con,String userName) throws SQLException {
		User resultUser=null;
		String sql="select * from t_user where userName=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, userName);
		ResultSet rs=pstmt.executeQuery();
		if(rs.next()){
			resultUser=new User();
			resultUser.setId(rs.getInt("userId"));
			resultUser.setUserName(rs.getString("userName"));
			resultUser.setPassword(rs.getString("password"));
		}
		return resultUser;
	}

	public List<User> find(Connection con, PageBean pageBean, User user) throws SQLException {
		List<User> customerList=new ArrayList<User>();
		StringBuffer sb=new StringBuffer("select *  from t_user where 1=1 ");
		if(user!=null && StringUtil.isNotEmpty(user.getUserName())){
			sb.append(" and t_user.userName like '"+user.getUserName()+"'");
		}
		if(pageBean!=null){
			sb.append(" limit "+pageBean.getStart()+","+pageBean.getPageSize());
		}
		 PreparedStatement pstmt = con.prepareStatement(sb.toString());
		ResultSet rs = pstmt.executeQuery();
		while(rs.next()){
			User userObj=new User();
			userObj.setId(rs.getInt("id"));
			userObj.setUserName(rs.getString("userName"));
			userObj.setEmail(rs.getString("email"));
			userObj.setPassword(rs.getString("password"));
			userObj.setPhone(rs.getString("phone"));
			userObj.setTrueName(rs.getString("trueName"));
			customerList.add(userObj);
		}
		return customerList;
	}
}
