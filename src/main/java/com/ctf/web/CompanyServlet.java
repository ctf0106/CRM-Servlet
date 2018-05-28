package com.ctf.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import com.ctf.entity.Company;
import com.ctf.entity.Customer;
import com.ctf.entity.PageBean;
import com.ctf.util.ChartUtil;
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
	
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}
	
	public String list(String page,String rows,Company s_company,HttpServletResponse response)throws Exception{
		PageBean pageBean=new PageBean(Integer.parseInt(page),Integer.parseInt(rows));
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("name", StringUtil.formatLike(s_company.getName()));
		map.put("start", pageBean.getStart());
		map.put("size", pageBean.getPageSize());
		List<Company> companyList=companyDao.find(map);
		Long total=companyDao.getTotal(map);
		JSONObject result=new JSONObject();
		JSONArray jsonArray=JSONArray.fromObject(companyList);
		result.put("rows", jsonArray);
		result.put("total", total);
		ResponseUtil.write(response, result);
		return null;
	}
	public String save(Company company,HttpServletResponse response)throws Exception{
		int resultTotal=0;
		if(company.getId()==null){
			resultTotal=companyDao.add(company);
		}else{
			resultTotal=companyDao.update(company);
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
	
	public String delete(String ids,HttpServletResponse response)throws Exception{
		String []idsStr=ids.split(",");
		for(int i=0;i<idsStr.length;i++){
			companyDao.delete(Integer.parseInt(idsStr[i]));
		}
		JSONObject result=new JSONObject();
		result.put("success", true);
		ResponseUtil.write(response, result);
		return null;
	}
	
	public String findById(String id,HttpServletResponse response)throws Exception{
		Company company=companyDao.findById(Integer.parseInt(id));
		JSONObject jsonObject=JSONObject.fromObject(company);
		ResponseUtil.write(response, jsonObject);
		return null;
	}
	
	public void comboList(HttpServletResponse response)throws Exception{
		JSONArray jsonArray=new JSONArray();
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("id", "");
		jsonObject.put("name", "请选择....");
		jsonArray.add(jsonObject);
		List<Company> companyList=companyDao.findAllList();
		JsonConfig jsonConfig=new JsonConfig();
		jsonConfig.setExcludes(new String[]{" "});
		JSONArray rows=JSONArray.fromObject(companyList, jsonConfig);
		jsonArray.addAll(rows);
		ResponseUtil.write(response, jsonArray);
	}
	
	/**
	 * 导出word
	 * @param id
	 * @param response
	 * @param request
	 * @throws Exception
	 */
	public void export(String id,HttpServletResponse response,HttpServletRequest request)throws Exception{
			request.setCharacterEncoding("utf-8");  
	        Map<String, Object> map = new HashMap<String, Object>();  
	        Company company = companyDao.findById(Integer.parseInt(id));
	        List<Customer> customerList = companyDao.findByCompanyId(Integer.parseInt(id));
	        /**
	         * 统计图所需要的参数
	         */
	        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		       for (Customer customer : customerList) {
		    	   dataset.addValue(customer.getFund(),customer.getName(), customer.getName());
		       }
			
	        String Statistics = ChartUtil.createChart(request,dataset, company.getId(),"员工基金统计图", "姓名","基金金额");
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
        	fin = new FileInputStream(file);
        	//response获取输出流，输出文件
            response.setCharacterEncoding("utf-8");  
            response.setContentType("application/msword");
            //解决中文乱码的情况
            String filename= new String(company.getName().getBytes("utf-8"), "ISO_8859_1"); 
            response.addHeader("Content-Disposition", "attachment;filename="+filename+".doc");  
            out = response.getOutputStream();  
            byte[] buffer = new byte[512]; 
            int bytesToRead = -1;  
            while((bytesToRead = fin.read(buffer)) != -1) {  
                out.write(buffer, 0, bytesToRead);  
            }  
        } finally {  
            if(fin != null) fin.close();  
            if(out != null) out.close();  
            if(file != null) file.delete(); 
        }  
    } 
	
}
