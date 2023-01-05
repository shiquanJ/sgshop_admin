package com.api.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.api.dao.ProductDao;

@Component
public class ProductService {
	@Autowired
	private ProductDao dao;
	
	//查询用户信息
	public Map<String,Object> getUserInfo(HashMap map){
			
		return dao.getUserInfo(map);
	}
	//获取购物车的商品信息
	public List<Map<String,Object>> getPrdList(){
			
		return dao.getPrdList();
	}
	//获取category
	public List<Map<String,Object>> getCategoryList(){
		
		return dao.getCategoryList();
	}
	//获取临时订单列表
	public List<Map<String,Object>> getTempOrderList(String temp_order_id){
		
		return dao.getTempOrderList(temp_order_id);
	}
	//保存个人信息
	public int insUserInfo(HashMap hashMap){
		
		return dao.insUserInfo(hashMap);
	}
	//insert 临时order表
	public int insTempOrder(HashMap hashMap){
		
		return dao.insTempOrder(hashMap);
	}
	//生成订单号
	public int insOrderInfo(HashMap hashMap){
		
		return dao.insOrderInfo(hashMap);
	}
	//新建收货地址
	public int insAddrInfo(HashMap hashMap){
		
		return dao.insAddrInfo(hashMap);
	}
	//修改收货地址
	public int updAddrInfo(HashMap hashMap){
		
		return dao.updAddrInfo(hashMap);
	}
	//修改默认default收货地址
	public int updDefaultAddr(HashMap hashMap){
		
		return dao.updDefaultAddr(hashMap);
	}
	//获取订单主表
	public List<Map<String,Object>> getOrderList(HashMap map){
		
		return dao.getOrderList(map);
	}
	//获取临时订单列表
	public List<Map<String,Object>> getOrderDetailList(HashMap map){
		
		return dao.getOrderDetailList(map);
	}
	//获取收获地址
	public List<Map<String,Object>> getAddrList(HashMap map){
		
		return dao.getAddrList(map);
	}
	//获取默认收获地址
	public Map<String, Object> getDefaultAddr(HashMap map){
		
		return dao.getDefaultAddr(map);
	}
	//获取默认收获地址
	public Map<String, Object> getDefaultAddr_temp(HashMap map){
		
		return dao.getDefaultAddr_temp(map);
	}
	//获取默认收获地址
	public String getOrderNumber(){
		
		return dao.getOrderNumber();
	}
}
