package com.ctf.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jfree.data.category.DefaultCategoryDataset;
import com.ctf.dao.CompanyDao;
import com.ctf.dao.CustomerDao;
import com.ctf.entity.Company;
import com.ctf.entity.Customer;
import com.ctf.entity.PageBean;
import com.ctf.util.ChartUtil;
import com.ctf.util.DbUtil;
import com.ctf.util.ResponseUtil;
import com.ctf.util.StringUtil;
import com.ctf.util.WordUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * @author Administrator
 *
 */

public class CompanyServlet extends HttpServlet {
	
	
	private static final long serialVersionUID = 1L;


	CompanyDao companyDao=new CompanyDao();
	CustomerDao customerDao=new CustomerDao();
	DbUtil dbUtil=new DbUtil();
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String action=request.getParameter("action");
		if("list".equals(action)){
			list(request,response);
		}else if("save".equals(action)){
			save(request,response);
		}else if("delete".equals(action)){
			delete(request,response);
		}else if("export".equals(action)){
			export(response, request);
		}else if("findById".equals(action)){
			findById(request, response);
		}else if("comboList".equals(action)){
			comboList(request,response);
		}
	}
	
	public String list(HttpServletRequest request,HttpServletResponse response){
		String page = request.getParameter("page");
		String rows = request.getParameter("rows");
		String name = request.getParameter("name");
		
		PageBean pageBean=new PageBean(Integer.parseInt(page),Integer.parseInt(rows));
		Company company=new Company();
		if(StringUtil.isNotEmpty(name)){
			company.setName(name);
		}
		Connection con = null;
		try {
			con = dbUtil.getCon();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		List<Company> companyList = null;
		try {
			companyList = companyDao.find(con,pageBean,company);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		int total = 0;
		try {
			total = companyDao.getTotal(con,company);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		JSONObject result=new JSONObject();
		JSONArray jsonArray=JSONArray.fromObject(companyList);
		result.put("rows", jsonArray);
		result.put("total", total);
		try {
			ResponseUtil.write(response, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public String save(HttpServletRequest request,HttpServletResponse response){
//		Company company,
		int resultTotal=0;
		String id = request.getParameter("id");
		String address = request.getParameter("address");
		String name = request.getParameter("name");
		
		Company  company=new Company();
		company.setAddress(address);
		company.setName(name);
		Connection con = null;
		try {
			con = dbUtil.getCon();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(id==null){
			company.setId(Integer.parseInt(id));
			try {
				resultTotal=companyDao.add(con,company);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else{
			try {
				resultTotal=companyDao.update(con,company);
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
	
	public String delete(HttpServletRequest request,HttpServletResponse response){
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
				companyDao.delete(con,Integer.parseInt(idsStr[i]));
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
		return null;
	}
	
	public String findById(HttpServletRequest request,HttpServletResponse response){
		String id=request.getParameter("id");
		Connection con = null;
		try {
			con = dbUtil.getCon();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Company company = null;
		try {
			company = companyDao.findById(con,Integer.parseInt(id));
		}catch (SQLException e1) {
			e1.printStackTrace();
		}
		JSONObject jsonObject=JSONObject.fromObject(company);
		try {
			ResponseUtil.write(response, jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void comboList(HttpServletRequest request,HttpServletResponse response){
		JSONArray jsonArray=new JSONArray();
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("id", "");
		jsonObject.put("name", "请选择....");
		jsonArray.add(jsonObject);
		Connection con = null;
		try {
			con = dbUtil.getCon();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		List<Company> companyList = null;
		try {
			companyList = companyDao.find(con, null, null);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		JsonConfig jsonConfig=new JsonConfig();
		jsonConfig.setExcludes(new String[]{" "});
		JSONArray rows=JSONArray.fromObject(companyList, jsonConfig);
		jsonArray.addAll(rows);
		try {
			ResponseUtil.write(response, jsonArray);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 导出word
	 * @param id
	 * @param response
	 * @param request
	 * @throws Exception
	 */
	public void export(HttpServletResponse response,HttpServletRequest request){
			String id = request.getParameter("id");
	        Map<String, Object> map = new HashMap<String, Object>();  
	        Connection con = null;
			try {
				con = dbUtil.getCon();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
	        Company company = null;
			try {
				company = companyDao.findById(con,Integer.parseInt(id));
			}  catch (SQLException e2) {
				e2.printStackTrace();
			}
	        List<Customer> customerList = null;
			try {
				customerList = customerDao.findByCompanyId(con, Integer.parseInt(id));
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
	        /**
	         * 统计图所需要的参数
	         */
	        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		       for (Customer customer : customerList) {
		    	   dataset.addValue(customer.getFund(),customer.getName(), customer.getName());
		       }
			
	        String Statistics = null;
			try {
				Statistics = ChartUtil.createChart(request,dataset, company.getId(),"员工基金统计图", "姓名","基金金额");
			} catch (Exception e) {
				e.printStackTrace();
			}
	        map.put("title",company.getName());
	        map.put("Statistics",Statistics);
	        map.put("customerList", customerList);
	        File file = null;
	        InputStream fin = null;  
	        ServletOutputStream out = null;  
        try {  
        	//生成word
        	 file = WordUtil.createDoc(map, company.getName());  
            //加载word
        	try {
				fin = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        	//response获取输出流，输出文件
            response.setCharacterEncoding("utf-8");  
            response.setContentType("application/msword");
            //解决中文乱码的情况
            String filename = null;
			try {
				filename = new String(company.getName().getBytes("utf-8"), "ISO_8859_1");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} 
            response.addHeader("Content-Disposition", "attachment;filename="+filename+".doc");  
            out = response.getOutputStream();  
            byte[] buffer = new byte[512]; 
            int bytesToRead = -1;  
            while((bytesToRead = fin.read(buffer)) != -1) {  
                out.write(buffer, 0, bytesToRead);  
            }  
        } catch (IOException e) {
			e.printStackTrace();
		} finally {  
            if(fin != null)
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}  
            if(out != null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}  
            if(file != null) file.delete(); 
        }  
    } 
	
}
