package com.ctf.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ctf.dao.UserDao;
import com.ctf.entity.PageBean;
import com.ctf.entity.User;
import com.ctf.util.DbUtil;
import com.ctf.util.ResponseUtil;
import com.ctf.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 用户操作
 * @author C
 *
 */
public class UserServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	UserDao userDao=new UserDao();
	
	DbUtil dbUtil=new DbUtil();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String action=request.getParameter("action");
		if("login".equals(action)){
			login(request,response);
		}else if("save".equals(action)){
			save(request,response);
		}else if("isExistName".equals(action)){
			isExistName(request,response);
		}else if("list".equals(action)){
			list(request,response);
		}else if("delete".equals(action)){
			delete(request,response);
		}else if("modifyPassword".equals(action)){
			modifyPassword(request,response);
		}else if("logout".equals(action)){
			logout(request,response);
		}
	}
	
	private void logout(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		session.removeAttribute("curretUser");  
        //从定向到login.jsp  
        try {
			response.sendRedirect("login.jsp");
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}

	private void delete(HttpServletRequest request, HttpServletResponse response) {
		String ids = request.getParameter("ids");
		String []idsStr=ids.split(",");
		Connection con = null;
		try {
			con = dbUtil.getCon();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		for(int i=0;i<idsStr.length;i++){
			try {
				userDao.delete(con,Integer.parseInt(idsStr[i]));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		JSONObject result=new JSONObject();
		result.put("success", true);
		try {
			ResponseUtil.write(response, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void list(HttpServletRequest request, HttpServletResponse response) {
		String rows = request.getParameter("rows");
		String page = request.getParameter("page");
		String userName = request.getParameter("userName");
		
		PageBean pageBean=new PageBean(Integer.parseInt(page),Integer.parseInt(rows));
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("userName", StringUtil.formatLike(userName));
		map.put("start", pageBean.getStart());
		map.put("size", pageBean.getPageSize());
		User user=new User();
		Connection con = null;
		try {
			con = dbUtil.getCon();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		List<User> userList = null;
		int total = 0;
		try {
			user.setUserName(userName);
			userList = userDao.find(con,pageBean,user);
			total = userDao.getTotal(con,user);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		JSONObject result=new JSONObject();
		JSONArray jsonArray=JSONArray.fromObject(userList);
		result.put("rows", jsonArray);
		result.put("total", total);
		try {
			ResponseUtil.write(response, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void isExistName(HttpServletRequest request, HttpServletResponse response) {
		String name = request.getParameter("name");
		User resultUser=null;
		Connection con=null;
		try{
			con=dbUtil.getCon();
			resultUser=userDao.getByUserName(con, name);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(resultUser==null){
			try {
				ResponseUtil.write(response,true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			try {
				ResponseUtil.write(response,false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void login(HttpServletRequest request,HttpServletResponse response){
		HttpSession session=request.getSession();
		String userName=request.getParameter("userName");
		String password=request.getParameter("password");
		Connection con=null;
		try{
			con=dbUtil.getCon();
			User user=new User();
			user.setUserName(userName);
			user.setPassword(password);
			User currentUser=userDao.login(con, user);
			if(currentUser==null){
				request.setAttribute("user", user);
				request.setAttribute("error", "用户名或密码错误！");
				request.getRequestDispatcher("login.jsp").forward(request, response);
			}else{
				session.setAttribute("currentUser", currentUser);
				request.getRequestDispatcher("main.jsp").forward(request, response);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				dbUtil.closeCon(con);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public String save(HttpServletRequest request,HttpServletResponse response){
		User user=new User();
		String userName=request.getParameter("userName");
		String password=request.getParameter("password");
		String trueName=request.getParameter("trueName");
		String email=request.getParameter("email");
		String phone=request.getParameter("phone");
		String id=request.getParameter("id");
		user.setUserName(userName);
		user.setPassword(password);
		user.setTrueName(trueName);
		user.setEmail(email);
		user.setPhone(phone);
		int resultTotal=0;
		Connection con;
		try {
			con = dbUtil.getCon();
			if(id==null){
				resultTotal=userDao.add(con, user);
			}else{
				user.setId(Integer.parseInt(id));
				resultTotal=userDao.update(con, user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject result=new JSONObject();
		if(resultTotal>0){
			result.put("success", true);
		}else{
			result.put("success", false);
		}
		try {
			ResponseUtil.write(response, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String modifyPassword(HttpServletRequest request,HttpServletResponse response){
		String id=request.getParameter("id");
		String newPassword=request.getParameter("newPassword");
		int resultTotal = 0;
		User user=new User();
		user.setId(Integer.parseInt(id));
		user.setPassword(newPassword);
		Connection con;
		try {
			con = dbUtil.getCon();
			resultTotal=userDao.update(con, user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		JSONObject result=new JSONObject();
		if(resultTotal>0){
			result.put("success", true);
		}else{
			result.put("success", false);
		}
		try {
			ResponseUtil.write(response, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
