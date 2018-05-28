package com.ctf.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ctf.entity.Customer;
import com.ctf.entity.CustomerVo;
import com.ctf.entity.PageBean;
import com.ctf.util.DateUtil;
import com.ctf.util.StringUtil;

public class CustomerDao{


	public int add(Connection con,Customer customer) throws Exception {
		String sql="INSERT INTO t_customer (id,khno,name,address,postCode,phone,fund,financing,companyID,gmt_create,gmt_modified) VALUES (null,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pstmt=con.prepareStatement(sql);
			pstmt.setString(1, customer.getKhno());
			pstmt.setString(2, customer.getName());
			pstmt.setString(3, customer.getAddress());
			pstmt.setString(4, customer.getPostCode());
			pstmt.setString(5, customer.getPhone());
			pstmt.setFloat(6, customer.getFund());
			pstmt.setFloat(7, customer.getFinancing());
			pstmt.setInt(8, customer.getCompanyID());
			pstmt.setString(9, DateUtil.getCurrentDate());
			pstmt.setString(10, DateUtil.getCurrentDate());
			return pstmt.executeUpdate();
	}

	public int update(Connection con,Customer customer) throws Exception {
		String sql="UPDATE t_customer  name=?, address=?, postCode=?, phone=?, fund=?, financing=?, companyID=?, gmt_modified=? WHERE id=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, customer.getName());
		pstmt.setString(2, customer.getAddress());
		pstmt.setString(3, customer.getPostCode());
		pstmt.setString(4, customer.getPhone());
		pstmt.setFloat(5, customer.getFund());
		pstmt.setFloat(6, customer.getFinancing());
		pstmt.setFloat(7, customer.getCompanyID());
		pstmt.setString(8, DateUtil.getCurrentDate());
		pstmt.setInt(9, customer.getId());
		return pstmt.executeUpdate();
	}

	public int delete(Connection con,Integer id) throws SQLException {
		String sql="delete from t_customer where id=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setInt(1, id);
		return pstmt.executeUpdate();
	}

	public Customer findById(Connection con,Integer id) throws SQLException {
		String sql="select * from t_customer where id=?";
		Customer customer=new Customer();
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setInt(1, id);
		ResultSet rs=pstmt.executeQuery();
		while(rs.next()){
			customer.setId(rs.getInt("id"));
			customer.setKhno(rs.getString("khno"));
			customer.setName(rs.getString("name"));
			customer.setFund(rs.getFloat("fund"));
			customer.setFinancing(rs.getFloat("financing"));
			customer.setPhone(rs.getString("phone"));
			customer.setAddress(rs.getString("address"));
			customer.setPostCode(rs.getString("postCode"));
			customer.setQrcode(rs.getString("qrcode"));
			customer.setBarcode(rs.getString("barcode"));
		}
		return customer;
	}

	public List<Customer> findByCompanyId(Connection con,int id) throws SQLException {
		List<Customer> customerList=new ArrayList<Customer>();
		String sql="select * from t_customer where companyID=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setInt(1,id);
		ResultSet rs=pstmt.executeQuery();
		while(rs.next()){
			Customer customer=new Customer();
			customer.setId(rs.getInt("id"));
			customer.setKhno(rs.getString("khno"));
			customer.setName(rs.getString("name"));
			customer.setFund(rs.getFloat("fund"));
			customer.setFinancing(rs.getFloat("financing"));
			customer.setPhone(rs.getString("phone"));
			customer.setAddress(rs.getString("address"));
			customer.setPostCode(rs.getString("postCode"));
			customer.setQrcode(rs.getString("qrcode"));
			customer.setBarcode(rs.getString("barcode"));
			customerList.add(customer);
		}
		return customerList;
	}

	public CustomerVo findByKhno(Connection con,String khno) throws SQLException {
		String sql="select t_customer.id,t_customer.khno,t_customer.name,t_customer.address,t_customer.phone,t_customer.postCode,"
				+ "t_customer.fund,t_customer.financing,t_customer.qrcode,t_company.name as companyName "
				+ " from t_customer LEFT JOIN t_company on t_customer.companyID=t_company.id where 1=1  and t_customer.khno=?";
		CustomerVo customervo=new CustomerVo();
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1,khno);
		ResultSet rs=pstmt.executeQuery();
		while(rs.next()){
			customervo.setId(rs.getInt("id"));
			customervo.setKhno(rs.getString("khno"));
			customervo.setName(rs.getString("name"));
			customervo.setFund(rs.getFloat("fund"));
			customervo.setFinancing(rs.getFloat("financing"));
			customervo.setPhone(rs.getString("phone"));
			customervo.setAddress(rs.getString("address"));
			customervo.setPostCode(rs.getString("postCode"));
			customervo.setQrcode(rs.getString("qrcode"));
			customervo.setBarcode(rs.getString("barcode"));
			customervo.setCompanyName(rs.getString("companyName"));
		}
		return customervo;
	}

	public List<CustomerVo> find(Connection con, PageBean pageBean, Customer s_customer) throws SQLException {
		List<CustomerVo> customerList=new ArrayList<CustomerVo>();
		StringBuffer sb=new StringBuffer("select t_customer.*,t_company.name as companyName  from t_customer left join t_company on t_customer.companyID=t_company.id where 1=1 ");
		if(StringUtil.isNotEmpty(s_customer.getName())){
			sb.append(" and t_customer.name like '"+StringUtil.formatLike(s_customer.getName())+"'");
		}
		if(StringUtil.isNotEmpty(s_customer.getKhno())){
			sb.append(" and t_customer.khno='"+s_customer.getKhno()+"'");
		}
		if(pageBean!=null){
			sb.append(" limit "+pageBean.getStart()+","+pageBean.getPageSize());
		}
		PreparedStatement pstmt=con.prepareStatement(sb.toString());
		ResultSet rs=pstmt.executeQuery();
		while(rs.next()){
			CustomerVo customervo=new CustomerVo();
			customervo.setId(rs.getInt("id"));
			customervo.setKhno(rs.getString("khno"));
			customervo.setName(rs.getString("name"));
			customervo.setFund(rs.getFloat("fund"));
			customervo.setFinancing(rs.getFloat("financing"));
			customervo.setPhone(rs.getString("phone"));
			customervo.setAddress(rs.getString("address"));
			customervo.setPostCode(rs.getString("postCode"));
			customervo.setQrcode(rs.getString("qrcode"));
			customervo.setBarcode(rs.getString("barcode"));
			customervo.setCompanyName(rs.getString("companyName"));
			customerList.add(customervo);
		}
		return customerList;
	}

	public int getTotal(Connection con, Customer s_customer) throws SQLException {
		int resultCount=0;
		StringBuffer sb=new StringBuffer("select count(*) as count  from t_customer left join t_company on t_customer.companyID=t_company.id where 1=1 ");
		if(StringUtil.isNotEmpty(s_customer.getName())){
			sb.append(" and t_customer.name like '"+StringUtil.formatLike(s_customer.getName())+"'");
		}
		if(StringUtil.isNotEmpty(s_customer.getKhno())){
			sb.append(" and t_customer.khno='"+s_customer.getKhno()+"'");
		}
		PreparedStatement pstmt=con.prepareStatement(sb.toString());
		ResultSet rs=pstmt.executeQuery();
		while(rs.next()){
			resultCount = rs.getInt("count");
		}
		return resultCount;
	}

}
