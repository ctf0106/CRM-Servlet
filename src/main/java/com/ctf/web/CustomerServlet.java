package com.ctf.web;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ctf.dao.CompanyDao;
import com.ctf.dao.CustomerDao;
import com.ctf.entity.Company;
import com.ctf.entity.Customer;
import com.ctf.entity.CustomerVo;
import com.ctf.entity.PageBean;
import com.ctf.util.DateUtil;
import com.ctf.util.DbUtil;
import com.ctf.util.ResponseUtil;
import com.ctf.util.StringUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import sun.misc.BASE64Encoder;

/**
 * @author Administrator
 *
 */
public class CustomerServlet extends HttpServlet {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	
	CustomerDao customerDao=new CustomerDao();
	CompanyDao companyDao=new CompanyDao();
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
		if("list".equals(action)){
			list(request,response);
		}else if("save".equals(action)){
			save(request,response);
		}else if("save".equals(action)){
			save(request,response);
		}else if("delete".equals(action)){
			delete(request,response);
		}
	}
	

	public String list(HttpServletRequest request,HttpServletResponse response){
		String page = request.getParameter("page");
		String rows = request.getParameter("rows");
		String khno = request.getParameter("khno");
		String name = request.getParameter("name");
		
		PageBean pageBean=new PageBean(Integer.parseInt(page),Integer.parseInt(rows));
		Customer customer=new Customer();
		if(StringUtil.isNotEmpty(khno)){
			customer.setKhno(khno);	
		}
		if(StringUtil.isNotEmpty(name)){
			customer.setName(name);
		}
		Connection con = null;
		try {
			con = dbUtil.getCon();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		List<CustomerVo> customerList = null;
		try {
			customerList = customerDao.find(con,pageBean,customer);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		int total = 0;
		try {
			total = customerDao.getTotal(con,customer);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		JSONObject result=new JSONObject();
		JSONArray jsonArray=JSONArray.fromObject(customerList);
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
			int resultTotal=0; 
			String khno = request.getParameter("khno");
			String id=request.getParameter("id");
			String name=request.getParameter("name");
			String fund=request.getParameter("fund");
			String financing = request.getParameter("financing");
			String phone=request.getParameter("phone");
			String address = request.getParameter("address");
			String postCode = request.getParameter("postCode");
			String qrcode = request.getParameter("qrcode");
			String barcode = request.getParameter("barcode");
			String companyID = request.getParameter("companyID");
			
			Connection con = null;
			try {
				con = dbUtil.getCon();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Customer customer=new Customer();
			customer.setKhno(khno);
			customer.setName(name);
			customer.setFund(Float.parseFloat(fund));
			customer.setFinancing(Float.parseFloat(financing));
			customer.setPhone(phone);
			customer.setAddress(address);
			customer.setPostCode(postCode);
			customer.setQrcode(qrcode);
			customer.setBarcode(barcode);
			customer.setCompanyID(Integer.parseInt(companyID));
			if(id==null){
			try {
				customer.setKhno("KH"+DateUtil.getCurrentDateStr());
				customer.setGmt_create(DateUtil.getCurrentDate());
				customer.setGmt_modified(DateUtil.getCurrentDate());
				//新增记录
				resultTotal=customerDao.add(con, customer);
				String qrCode  = this.createQrCode(customer.getKhno(), BarcodeFormat.QR_CODE,request,300,300);
				String barCode = this.createQrCode(customer.getKhno(), BarcodeFormat.CODE_128,request,70,350);
				//更新条形码和二维码
				Customer updateObj=new Customer();
				updateObj.setQrcode(qrCode);
				updateObj.setBarcode(barCode);
				updateObj.setId(customer.getId());
				customerDao.update(con,updateObj);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			
		}else{
		
			try {
				customer.setGmt_modified(DateUtil.getCurrentDate());
				resultTotal=customerDao.update(con,customer);
			} catch (Exception e) {
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
		String filePath=request.getServletContext().getRealPath("/");
		Connection con = null;
		try {
			con = dbUtil.getCon();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		for(int i=0;i<idsStr.length;i++){
			Customer customer = null;
			try {
				customer = customerDao.findById(con, Integer.parseInt(idsStr[i]));
			} catch (NumberFormatException | SQLException e) {
				e.printStackTrace();
			}
			File qrCode = new File(filePath+"static/qrCodeImage/"+BarcodeFormat.QR_CODE+"_"+customer.getKhno()+".png");
			if(qrCode!=null){ 
				qrCode.delete();
			}
			File barCode = new File(filePath+"static/qrCodeImage/"+BarcodeFormat.CODE_128+"_"+customer.getKhno()+".png");
			if(barCode!=null){ 
				barCode.delete();
			}
			try {
				customerDao.delete(con, Integer.parseInt(idsStr[i]));
			} catch (NumberFormatException | SQLException e) {
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
	
	public String findById(String id,HttpServletResponse response){
		Connection con=null;
		Object customer = null;
		
		try {
			con = dbUtil.getCon();
			 customer=customerDao.findById(con, Integer.parseInt(id));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject jsonObject=JSONObject.fromObject(customer);
		try {
			ResponseUtil.write(response, jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public String findByKhno(String khno,HttpServletResponse response){
		Connection con=null;
		CustomerVo  customervo = null;
		try {
			con = dbUtil.getCon();
			customervo=customerDao.findByKhno(con, khno);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject jsonObject=JSONObject.fromObject(customervo);
		try {
			ResponseUtil.write(response, jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String createQrCode(String khno,BarcodeFormat codeFormat,HttpServletRequest request,int height,int width)throws Exception{
		if(!khno.isEmpty()){
			String filePath=request.getServletContext().getRealPath("/");
			String qrCodeName=khno;
	        String format = "png";  
	        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();  
	        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");  
	        BitMatrix bitMatrix = null;  
	        try {  
	            bitMatrix = new MultiFormatWriter().encode(khno, codeFormat, width, height, hints);  
	        } catch (WriterException e) {  
	            e.printStackTrace();  
	        }  
	        File outputFile = new File(filePath+"static/qrCodeImage/"+codeFormat+"_"+qrCodeName+"."+format);
	        MatrixToImageWriter.writeToFile(bitMatrix, format, outputFile);
	        //读取二维码
	        OutputStream os1 = new FileOutputStream(filePath+"static/qrCodeImage/"+codeFormat+"_"+qrCodeName+"."+format);  
	        MatrixToImageWriter.writeToStream(bitMatrix, format, os1);  
	        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);  
	        ByteArrayOutputStream os = new ByteArrayOutputStream();
	        ImageIO.write(image, format, os);
	        byte b[] = os.toByteArray();
	        String str = new BASE64Encoder().encode(b);  
	        return str;	
		}
		return null;
	}
}
