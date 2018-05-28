package com.ctf.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ctf.entity.Company;
import com.ctf.entity.PageBean;
import com.ctf.util.StringUtil;

public class CompanyDao{


	public List<Company> find(Connection con, PageBean pageBean, Company company) throws SQLException {
		List<Company> customerList=new ArrayList<Company>();
		StringBuffer sb=new StringBuffer("select *  from t_company where 1=1 ");
		if(company!=null && StringUtil.isNotEmpty(company.getName())){
			sb.append(" and t_company.name like '"+StringUtil.formatLike(company.getName())+"'");
		}
		if(pageBean!=null){
			sb.append(" limit "+pageBean.getStart()+","+pageBean.getPageSize());
		}
		 PreparedStatement pstmt = con.prepareStatement(sb.toString());
		ResultSet rs = pstmt.executeQuery();
		while(rs.next()){
			Company companObj=new Company();
			companObj.setId(rs.getInt("id"));
			companObj.setName(rs.getString("name"));
			companObj.setAddress(rs.getString("address"));
			customerList.add(companObj);
		}
		return customerList;
	}

	public int getTotal(Connection con, Company company) throws SQLException {
		int resultCount=0;
		StringBuffer sb=new StringBuffer("select count(*) as count  from t_company where 1=1 ");
		if(StringUtil.isNotEmpty(company.getName())){
			sb.append(" and t_company.name like '"+StringUtil.formatLike(company.getName())+"'");
		}
		PreparedStatement pstmt=con.prepareStatement(sb.toString());
		ResultSet rs=pstmt.executeQuery();
		while(rs.next()){
			resultCount = rs.getInt("count");
		}
		return resultCount;
	}

	public int add(Connection con, Company company) throws SQLException {
		String sql="INSERT INTO t_company (id, name, address) VALUES (null,?,?)";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, company.getName());
		pstmt.setString(2, company.getAddress());
		return pstmt.executeUpdate();
	}

	public int update(Connection con, Company company) throws SQLException {
		String sql="UPDATE t_company SET name=?, address=? WHERE id=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, company.getName());
		pstmt.setString(2, company.getAddress());
		pstmt.setInt(3, company.getId());
		return pstmt.executeUpdate();
	}

	public int delete(Connection con, int id) throws SQLException {
		String sql="delete from t_company where id=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setInt(1, id);
		return pstmt.executeUpdate();
		
	}

	public Company findById(Connection con, int id) throws SQLException {
		String sql="select * from t_company where id=?";
		Company company=new Company();
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setInt(1, id);
		ResultSet rs=pstmt.executeQuery();
		while(rs.next()){
			company.setId(rs.getInt("id"));
			company.setName(rs.getString("name"));
			company.setAddress(rs.getString("address"));
		}
		return company;
	}


}
