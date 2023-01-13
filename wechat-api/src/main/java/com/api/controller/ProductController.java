package com.api.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.api.service.ProductService;
import com.api.utils.HttpClientUtil;
import com.api.utils.JSONUtils;

import net.sf.json.JSONObject;
@Controller
@RequestMapping("/api")
public class ProductController {
	@Autowired
    private ProductService service;
	final String appid = "wx51f32dbc25c96f46";
	final String secret = "00357569ea481844317161625b8e45c3";


	//微信登录并获取session_key
	@RequestMapping("/wxlogin")
	@ResponseBody
	public Map<String,Object> wxlogin(HttpServletRequest req, HttpServletResponse res) {

		String code = req.getParameter("code");
		System.out.println("code::"+code);
		HashMap setMap = new HashMap();
		HashMap resMap = new HashMap();
		Map<String, Object> userInfo = new HashMap();

		try {

			//String token = getToken();
			String uri = "https://api.weixin.qq.com/sns/jscode2session?appid="+appid+"&secret="+secret+"&js_code="+code+"&grant_type=authorization_code";


			String result = HttpClientUtil.doGet(uri);

			JSONObject fromObject = JSONObject.fromObject(result);

			System.out.println("fromObject:::"+fromObject);

			String userid = fromObject.get("openid").toString();

			//获取用户信息
			setMap.put("user_id", userid);
			if(userid != null) {
				//查询用户信息
				 userInfo = service.getUserInfo(setMap);
				 if(userInfo != null) {
					 resMap.put("userInfo", userInfo);
				 }else {
					 resMap.put("userid", userid);
				 }
			}


			return resMap;
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}



	//把userInfo保存到db里
	@RequestMapping("/saveUserInfo")
	@ResponseBody
	public HashMap saveUserInfo(HttpServletRequest req, HttpServletResponse res){

		HashMap result = new HashMap  ();
		HashMap map = new HashMap  ();
		String  userInfo  = req.getParameter("userInfo");
		String  user_id  = req.getParameter("user_id");


		map.put("user_id", user_id);
		map.put("nickname", JSONUtils.getRes(userInfo ,"nickName"));
		map.put("gender", JSONUtils.getRes(userInfo ,"gender"));
		map.put("avatarurl", JSONUtils.getRes(userInfo ,"avatarUrl"));

		int cnt = 0 ;
		Map<String, Object> userInfoMap = service.getUserInfo(map);

		if(userInfoMap == null) {
			//保存个人信息
			cnt = service.insUserInfo(map);
			userInfoMap = service.getUserInfo(map);
		}
		result.put("userInfo", userInfoMap);

		return result;
	}
	//商品列表
	@RequestMapping("/list")
	@ResponseBody
	public HashMap getPrdList(HttpServletRequest req, HttpServletResponse res){

		HashMap reqMap = new HashMap();

		reqMap.put("store_id", req.getParameter("store_id"));

		HashMap result = new HashMap  ();

		List<Map<String,Object>> prdList = service.getPrdList(reqMap);	//商品列表
		List<Map<String,Object>> categoryList = service.getCategoryList();	//category 列表

		result.put("prdList", prdList);
		result.put("categoryList", categoryList);


		return result;
	}

	//生成临时订单列表
	@RequestMapping("/temp_order")
	@ResponseBody
	public HashMap tempOrderList(HttpServletRequest req, HttpServletResponse res){

		HashMap result = new HashMap  ();
		String  param  = req.getParameter("cartList");
		//list 格式： {"1":{"prd_id":1,"name":"经典至尊牛肉比萨","price":119,"number":2},"2":{"id":2,"name":"土豆比萨","price":74,"number":1}}

		ArrayList<HashMap> resFromJSON = JSONUtils.getResFromJSON(param);

		HashMap map = new HashMap();

		int insTempOrder = 0;
		//先获取今天订单号
		//String orderId_today = service.getOrderId_today();

		Calendar cal = Calendar.getInstance();

		Date time = cal.getTime();
		String temp_order_id = String.valueOf(time.getTime());

		for (int i = 0; i < resFromJSON.size(); i++) {
			map.put("temp_order_id", temp_order_id);
			map.put("prd_id", resFromJSON.get(i).get("prd_id"));
			map.put("order_cnt", resFromJSON.get(i).get("number"));

			insTempOrder = service.insTempOrder(map);
		}


		System.out.println(temp_order_id);
		result.put("temp_order_id", temp_order_id);


		return result;
	}

	//获取临时订单list
	@RequestMapping("/get_temp_order_list")
	@ResponseBody
	public HashMap getTempOrderList(HttpServletRequest req, HttpServletResponse res){

		HashMap result = new HashMap  ();
		String  temp_order_id  = req.getParameter("temp_order_id");
		String  user_id  = req.getParameter("user_id");
		String  is_self  = req.getParameter("is_self");

		HashMap map = new HashMap();
		map.put("user_id", user_id);


		int insTempOrder = 0;
		double total_price = 0 ;
		int total_cnt = 0 ;
		//获取临时订单列表
		List<Map<String,Object>> tempOrderList = service.getTempOrderList(temp_order_id);

		for (int i = 0; i < tempOrderList.size(); i++) {

			total_price += Double.valueOf(String.valueOf(tempOrderList.get(i).get("total_price")));
			total_cnt += Integer.valueOf(String.valueOf(tempOrderList.get(i).get("order_cnt")));
		}

		//外卖的时候 获取收获地址
		if(is_self.equals("false")) {
			Map<String, Object>  defaultMap = service.getDefaultAddr_temp(map);
			result.put("addr_id", defaultMap.get("a_id"));
			result.put("receipt_name", defaultMap.get("receipt_name"));
			result.put("gender", defaultMap.get("gender"));
			result.put("receipt_phone", defaultMap.get("receipt_phone"));
			result.put("receipt_detail", defaultMap.get("receipt_detail"));
			result.put("receipt_address_detail", defaultMap.get("receipt_address_detail"));
		}

		System.out.println("ok:::"+total_price);
		result.put("total_price", total_price);
		result.put("total_cnt", total_cnt);
		result.put("tempOrderList", tempOrderList);


		return result;
	}


	//获取订单详情
	@RequestMapping("/getOrderDetailList")
	@ResponseBody
	public HashMap getOrderDetailList(HttpServletRequest req, HttpServletResponse res){

		HashMap result = new HashMap  ();

		String  order_id  = req.getParameter("order_id");
		String  user_id  = req.getParameter("user_id");

		System.out.println("order_id:::"+order_id);
		System.out.println("user_id:::"+user_id);

		HashMap map = new HashMap();

		map.put("order_id", order_id);
		map.put("user_id", user_id);
		//获取订单详情
		List<Map<String,Object>> orderdetaillist = service.getOrderDetailList(map);
		System.out.println(orderdetaillist);

		double total_price = 0;
		String is_self = "";
		String order_sendtime = "";
		String order_number = "";
		String order_state = "";
		String user_comment = "";
		String is_express = "";
		String create_dt = "";


		for (int i = 0; i < orderdetaillist.size(); i++) {
			total_price = Double.valueOf(String.valueOf(orderdetaillist.get(i).get("total_price")));
			is_self = (String)orderdetaillist.get(i).get("is_self");
			order_sendtime = String.valueOf(orderdetaillist.get(i).get("order_sendtime"));
			order_number = String.valueOf(orderdetaillist.get(i).get("order_number"));
			order_state = (String)orderdetaillist.get(i).get("order_state");
			user_comment = (String)orderdetaillist.get(i).get("user_comment");
			is_express = (String)orderdetaillist.get(i).get("is_express");
			create_dt = String.valueOf(orderdetaillist.get(i).get("create_dt"));
			break;
		}

		//外卖的时候 获取收获地址
		if(is_self.equals("false")) {
			Map<String, Object>  defaultMap = service.getDefaultAddr(map);
			result.put("addr_id", defaultMap.get("a_id"));
			result.put("receipt_name", defaultMap.get("receipt_name"));
			result.put("gender", defaultMap.get("gender"));
			result.put("receipt_phone", defaultMap.get("receipt_phone"));
			result.put("receipt_detail", defaultMap.get("receipt_detail"));
			result.put("receipt_address_detail", defaultMap.get("receipt_address_detail"));
		}

		result.put("order_id", order_id);
		result.put("total_price", total_price);
		result.put("is_self", is_self);
		result.put("order_sendtime", order_sendtime);
		result.put("order_number", order_number);
		result.put("order_state", order_state);
		result.put("user_comment", user_comment);
		result.put("is_express", is_express);
		result.put("create_dt", create_dt);
		result.put("order_detail_list", orderdetaillist);

		return result;
	}


	//pages/order/list/list 订单页面
	@RequestMapping("/orderlist")
	@ResponseBody
	public HashMap getOrderlist(HttpServletRequest req, HttpServletResponse res){

		HashMap result = new HashMap  ();

		String  user_id  = req.getParameter("user_id");
		String  last_id  = req.getParameter("last_id");
		String  row  = req.getParameter("row");
		String  index  = req.getParameter("index");	// 0 是全部订单， 1是自取， 2是外卖

		System.out.println("user_id:::"+user_id);
		System.out.println("last_id:::"+last_id);
		System.out.println("row:::"+row);
		System.out.println("index:::"+index);

		HashMap map = new HashMap();

		map.put("user_id", user_id);
		map.put("index", index);
		map.put("last_id", Integer.valueOf(last_id));
		map.put("row", Integer.valueOf(last_id) + Integer.valueOf(row));
		//获取订单主表
		List<Map<String,Object>> orderlist = service.getOrderList(map);

		HashMap detailMap = new HashMap();
		ArrayList detailList = new ArrayList();
		for (int i = 0; i < orderlist.size(); i++) {
			detailMap = new HashMap();
			detailMap.put("order_id", orderlist.get(i).get("order_id").toString());
			List<Map<String,Object>> orderDetailList = service.getOrderDetailList(detailMap);
			detailList.add(orderDetailList);
		}

		System.out.println(detailList);
		result.put("last_id", map.get("row"));
		result.put("detail_list", detailList);
		result.put("order_list", orderlist);

		return result;
	}



	//pages/mine/newAddr/newAddr 订单页面
	@RequestMapping("/addrList")
	@ResponseBody
	public HashMap getaddrList(HttpServletRequest req, HttpServletResponse res){

		HashMap result = new HashMap  ();

		String  user_id  = req.getParameter("user_id");

		HashMap map = new HashMap();

		map.put("user_id", user_id);

		try {
			//获取收获地址
			List<Map<String,Object>> getAddrList = service.getAddrList(map);

			if(getAddrList.size() > 0 ) {
				result.put("hasAddrInfo", true);
				result.put("addrList", getAddrList);
			}else {
				result.put("hasAddrInfo", false);
			}


		} catch (Exception e) {
			result.put("hasAddrInfo", false);
		}


		return result;
	}

	//ID查询收获地址
	@RequestMapping("/getAddrInfo")
	@ResponseBody
	public HashMap getAddrInfo(HttpServletRequest req, HttpServletResponse res){

		HashMap result = new HashMap  ();

		String  user_id  = req.getParameter("user_id");
		String  addr_id  = req.getParameter("addr_id");

		HashMap map = new HashMap();

		map.put("user_id", user_id);
		map.put("addr_id", addr_id);

		//获取收获地址
		List<Map<String,Object>> getAddrList = service.getAddrList(map);

		for (int i = 0; i < getAddrList.size(); i++) {
			result.put("addr_id", getAddrList.get(i).get("a_id"));
			result.put("user_id", getAddrList.get(i).get("user_id"));
			result.put("receipt_name", getAddrList.get(i).get("receipt_name"));
			result.put("gender", getAddrList.get(i).get("gender"));
			result.put("receipt_phone", getAddrList.get(i).get("receipt_phone"));
			result.put("receipt_address_name", getAddrList.get(i).get("receipt_address_name"));
			result.put("receipt_address_detail", getAddrList.get(i).get("receipt_address_detail"));
			result.put("receipt_detail", getAddrList.get(i).get("receipt_detail"));
			result.put("default_addr", getAddrList.get(i).get("default_addr"));
		}

		return result;
	}

	/*
	 * 这里开始是insert 或 update
	 */

	//下单成功，生成订单号订单
		@RequestMapping("/save_order_info")
		@ResponseBody
		public HashMap saveOrderInfo(HttpServletRequest req, HttpServletResponse res){

			HashMap result = new HashMap  ();
			String  temp_order_id  = req.getParameter("temp_order_id");
			String  total_cnt  = req.getParameter("total_cnt");
			String  total_price  = req.getParameter("total_price");
			String  order_sendtime  = req.getParameter("order_sendtime");
			String  user_id  = req.getParameter("user_id");
			String  user_comment  = req.getParameter("user_comment");
			String  is_self  = req.getParameter("is_self");
			String  addr_id  = req.getParameter("addr_id");
			String  is_express  = req.getParameter("is_express");
			String orderNumber = ""; //订单号

			System.out.println("temp_order_id:::"+temp_order_id);
			System.out.println("total_cnt:::"+total_cnt);
			System.out.println("total_price:::"+total_price);
			System.out.println("user_id:::"+user_id);
			System.out.println("user_comment:::"+user_comment);
			HashMap map = new HashMap();

			map.put("temp_order_id", temp_order_id);
			map.put("total_cnt", total_cnt);
			map.put("total_price", total_price);
			map.put("order_sendtime", order_sendtime);
			map.put("user_id", user_id);
			map.put("user_comment", user_comment);
			map.put("is_self", is_self );
			map.put("addr_id", addr_id.equals("undefined")?null:addr_id );
			map.put("is_express", is_express);
			//生成订单号

			if(is_self.equals("true")) {
				orderNumber = service.getOrderNumber();
				if(orderNumber.length() == 1) {
					orderNumber = "000"+orderNumber;
				}else if(orderNumber.length() == 2){
					orderNumber = "00"+orderNumber;
				}
			}

			map.put("order_number", orderNumber );



			int resCnt = service.insOrderInfo(map);
			System.out.println(resCnt);

			if(resCnt > 0 ) {

				result.put("order_id", temp_order_id);
			}else {
				result.put("order_id", "");
			}

			return result;
		}


		@RequestMapping("/saveAddr")
		@ResponseBody
		public HashMap saveAddrInfo(HttpServletRequest req, HttpServletResponse res){

			HashMap result = new HashMap  ();
			String  addr_id  = req.getParameter("addr_id");
			String  is_new  = req.getParameter("is_new");
			String  user_id  = req.getParameter("user_id");
			String  receipt_name  = req.getParameter("receipt_name");
			String  gender  = req.getParameter("gender");
			String  phone  = req.getParameter("phone");
			String  detail  = req.getParameter("detail");	//手写的详细地址
			String  addressName  = req.getParameter("addressName");	//收货地址
			String  addressDetail  = req.getParameter("addressDetail");	//收货地址


			HashMap map = new HashMap();

			map.put("user_id", user_id);
			map.put("receipt_name", receipt_name);
			map.put("gender", gender);
			map.put("phone", phone);
			map.put("detail", detail);
			map.put("address_name", addressName);
			map.put("address_detail", addressDetail);

			int resCnt = 0;
			if(is_new.equals("true")) {	//新建
				resCnt = service.insAddrInfo(map);
			}else {						//修改收货地址
				map.put("addr_id", addr_id);
				resCnt = service.updAddrInfo(map);
			}
			//保存收货地址
			System.out.println(resCnt);

			if(resCnt > 0 ) {
				//获取收获地址
				List<Map<String,Object>> getAddrList = service.getAddrList(map);
				if(getAddrList.size() > 0 ) {

					result.put("addrList", getAddrList);
					result.put("status", "0");
				}
			}else {
				result.put("status", "1");
			}

			return result;
		}

		//设置默认地址
		@RequestMapping("/upd_addr_default")
		@ResponseBody
		public HashMap updAddrDefault(HttpServletRequest req, HttpServletResponse res){

			HashMap result = new HashMap  ();
			String  addr_id  = req.getParameter("addr_id");
			String  user_id  = req.getParameter("user_id");

			int resCnt = 0;
			HashMap map = new HashMap();

			map.put("addr_id", addr_id);
			map.put("user_id", user_id);

			resCnt = service.updDefaultAddr(map);
			//保存收货地址
			System.out.println(resCnt);

			if(resCnt > 0 ) {
				//不能带addr_id
				map = new HashMap();
				map.put("user_id", user_id);
				List<Map<String,Object>> getAddrList = service.getAddrList(map);
				if(getAddrList.size() > 0 ) {

					result.put("addrList", getAddrList);
					result.put("hasAddrInfo", true);
				}else {
					result.put("hasAddrInfo", false);
				}
			}else {
				result.put("hasAddrInfo", false);
			}

			return result;
		}





	// 获取 token
		public String getToken(){

			String uri = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx0dce03266ee2f937&secret=aa4e9777d3d5f1c4b270283416d770e0";
			String result = HttpClientUtil.doGet(uri);

			System.out.println("result:::"+result);

			JSONObject fromObject = JSONObject.fromObject(result);

			String token = fromObject.get("access_token").toString();
			return token;
		}
}
