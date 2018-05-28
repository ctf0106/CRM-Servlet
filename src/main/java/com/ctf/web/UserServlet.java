package com.ctf.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ctf.dao.UserDao;
import com.ctf.entity.PageBean;
import com.ctf.entity.User;
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
	
	
	/**
	 * 分页条件查询用户
	 * @param page
	 * @param rows
	 * @param s_user
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String list(String page,String rows,User s_user,HttpServletResponse response)throws Exception{
		PageBean pageBean=new PageBean(Integer.parseInt(page),Integer.parseInt(rows));
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("userName", StringUtil.formatLike(s_user.getUserName()));
		map.put("start", pageBean.getStart());
		map.put("size", pageBean.getPageSize());
		List<User> userList=userDao.find(map);
		Long total=userDao.getTotal(map);
		JSONObject result=new JSONObject();
		JSONArray jsonArray=JSONArray.fromObject(userList);
		result.put("rows", jsonArray);
		result.put("total", total);
		ResponseUtil.write(response, result);
		return null;
	}
	
	/**
	 * @param user
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public String login(User user,HttpServletRequest request)throws Exception{
		return null;
		
	}
	
	public void isExistName(String name,HttpServletResponse response)throws Exception{
		User resultUser=userDao.getByUserName(name);
		if(resultUser==null){
			ResponseUtil.write(response,true);
		}else{
			ResponseUtil.write(response,false);
		}
	}
	public String save(User user,HttpServletResponse response)throws Exception{
		int resultTotal=0;
		if(user.getId()==null){
			resultTotal=userDao.add(user);
		}else{
			resultTotal=userDao.update(user);
		}
		JSONObject result=new JSONObject();
		if(resultTotal>0){
			result.put("success", true);
		}else{
			result.put("success", false);
		}
		ResponseUtil.write(response, result);
		return null;
	}
	
	public String modifyPassword(Integer id,String newPassword,HttpServletResponse response)throws Exception{
		User user=new User();
		user.setId(id);
		user.setPassword(newPassword);
		int resultTotal=userDao.update(user);
		JSONObject result=new JSONObject();
		if(resultTotal>0){
			result.put("success", true);
		}else{
			result.put("success", false);
		}
		ResponseUtil.write(response, result);
		return null;
	}
	
	public String logout(HttpSession session)throws Exception{
		return null;
		
	}
}
