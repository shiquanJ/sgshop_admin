package com.api.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.api.utils.JdbcUtil;

@Component
public class ProductDao {
	@Autowired
	private JdbcUtil jdbc;
	//查询用户信息
	public Map<String,Object> getUserInfo(HashMap map){

		String sql ="select * from sg_member"
				+ "\n where  id = ? "
				+ "\n limit 0,1";

		//传空的参数

		  Object[] obj = new Object[]{1}; obj[0] = map.get("user_id");

		try {
			Map<String, Object> res = jdbc.queryForMap(sql, obj);
			return res;
		} catch (Exception e) {
			//没有数据
			return null;
		}
	}
	//获取商品
	public List<Map<String,Object>> getPrdList(HashMap map){

		String sql ="select a.prd_id, a.prd_name, a.price, a.image_url, a.category_id, b.category_name from prd_list a, category b"
				+ "\n where  a.store_id = ? "
				+ "\n 		and a.category_id = b.category_id"
				+ "\n 		order by a.prd_id";

		//传空的参数
		Object[] obj = new Object[]{1}; obj[0] = map.get("store_id");
		try {
			List<Map<String,Object>> list  = (List)jdbc.getList(sql, obj);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	//获取category
	public List<Map<String,Object>> getCategoryList(){

		String sql ="select category_id, category_name from category "
				+ "\n order by category_id";

		//传空的参数
		/*
		 * Object[] obj = new Object[]{1}; obj[0] = map.get("member_id");
		 */
		try {
			List<Map<String,Object>> list  = (List)jdbc.getList(sql, null);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	//获取临时订单列表
	public List<Map<String,Object>> getTempOrderList(String temp_order_id){

		String sql ="	SELECT m.* , m.order_cnt * m.price as total_price FROM "
				+ "\n	(select a.temp_order_id , a.prd_id, a.order_cnt "
				+ "\n		, b.prd_name, b.category_id, b.price, b.image_url from temp_order_list a , prd_list b  "
				+ "\n 	where a.temp_order_id = ? "
				+ "\n	 and a.prd_id = b.prd_id) m";

		//传空的参数

		  Object[] obj = new Object[]{1}; obj[0] = temp_order_id;

		try {
			List<Map<String,Object>> list = (List) jdbc.getList(sql,obj);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//insert 临时order表
	public int insUserInfo(HashMap map){

		int res = 0;
		try {

			String sql= "insert into sg_member ("
					+ "\n		id"
					+ "\n		,nick_name"
					+ "\n		,sex"
					+ "\n		,face"
					+ "\n		,custom_cl"
					+ "\n		,create_time"
					+ "\n		,update_time )"
					+ "\n		values("
					+ "\n		?, ?, ?, ? "
					+ "\n       ,  'client' "
					+ "\n       ,  SYSDATE()"
					+ "\n       ,  SYSDATE()"
					+ "\n		)" ;
			System.out.println(sql);
			Object[] insObj = new Object[4];
			insObj[0] = map.get("user_id");
			insObj[1] = map.get("nickname");
			insObj[2] = map.get("gender");
			insObj[3] = map.get("avatarurl");

			res = jdbc.update(sql, insObj);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}
	//insert 临时order表
	public int insTempOrder(HashMap map){

		int res = 0;
		try {

			String sql= "insert into temp_order_list ("
					+ "\n		temp_order_id"
					+ "\n		,prd_id"
					+ "\n		,order_cnt"
					+ "\n		,create_dt"
					+ "\n		,update_dt )"
					+ "\n		values"
					+ "\n		("
					+ "\n		?, ?, ?,  SYSDATE(), SYSDATE()"
					+ "\n		)" ;
			System.out.println(sql);
			Object[] insObj = new Object[3];
			insObj[0] = map.get("temp_order_id");
			insObj[1] = map.get("prd_id");
			insObj[2] = map.get("order_cnt");

			res = jdbc.update(sql, insObj);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}
	//生成订单号
	public int insOrderInfo(HashMap map){

		//先插入订单main表
		int res = 0;
		try {

			String sql= "insert into order_info ("
					+ "\n		order_id"
					+ "\n		,user_id"
					+ "\n		,total_cnt"
					+ "\n		,total_price"
					+ "\n		,order_sendtime"
					+ "\n		,user_comment"
					+ "\n		,is_self"
					+ "\n		,addr_id"
					+ "\n		,order_number"
					+ "\n		,order_state"
					+ "\n		,is_express"
					+ "\n		,create_dt"
					+ "\n		,update_dt )"
					+ "\n		values"
					+ "\n		("
					+ "\n		?, ?, ?, ?, ? ,?, ?, ? ,?,'0', ?,SYSDATE(), SYSDATE()"
					+ "\n		)" ;
			System.out.println(sql);
			Object[] insObj = new Object[10];
			insObj[0] = map.get("temp_order_id");
			insObj[1] = map.get("user_id");
			insObj[2] = map.get("total_cnt");
			insObj[3] = map.get("total_price");
			insObj[4] = map.get("order_sendtime");
			insObj[5] = map.get("user_comment");
			insObj[6] = map.get("is_self");
			insObj[7] = map.get("addr_id");
			insObj[8] = map.get("order_number");
			insObj[9] = map.get("is_express");

			res = jdbc.update(sql, insObj);

			if(res > 0 ) {
				//insert detail表
				String sql2= "insert into order_info_detail ("
						+ "\n		select temp_order_id , prd_id, order_cnt, now(), now() from temp_order_list where temp_order_id = ?"
						+ "\n		)";
				System.out.println(sql2);
				Object[] insObj2 = new Object[1];
				insObj2[0] = map.get("temp_order_id");

				res = jdbc.update(sql2, insObj2);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}
	//保存收货地址
	public int insAddrInfo(HashMap map){

		//先插入订单main表
		int res = 0;
		try {

			String sql= "insert into address_list ("
					+ "\n		user_id"
					+ "\n		,receipt_name"
					+ "\n		,gender"
					+ "\n		,receipt_phone"
					+ "\n		,receipt_address_name"
					+ "\n		,receipt_address_detail"
					+ "\n		,receipt_detail"
					+ "\n		,remark"
					+ "\n		,create_dt"
					+ "\n		,update_dt )"
					+ "\n		values"
					+ "\n		("
					+ "\n		?, ?, ?, ? , ?, ?, ? ,'', SYSDATE(), SYSDATE()"
					+ "\n		)" ;
			System.out.println(sql);
			Object[] insObj = new Object[7];
			insObj[0] = map.get("user_id");
			insObj[1] = map.get("receipt_name");
			insObj[2] = map.get("gender");
			insObj[3] = map.get("phone");
			insObj[4] = map.get("address_name");
			insObj[5] = map.get("address_detail");
			insObj[6] = map.get("detail");

			res = jdbc.update(sql, insObj);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}
	//修改收货地址
	public int updAddrInfo(HashMap map){

		//先插入订单main表
		int res = 0;
		try {

			String sql= "update address_list set "
					+ "\n		receipt_name = ?"
					+ "\n		,gender = ?"
					+ "\n		,receipt_phone = ?"
					+ "\n		,receipt_address_name = ?"
					+ "\n		,receipt_address_detail = ?"
					+ "\n		,receipt_detail = ? "
					+ "\n		,update_dt  = SYSDATE()"
					+ "\n		where a_id = ? and user_id = ?" ;
			System.out.println(sql);
			Object[] insObj = new Object[8];
			insObj[0] = map.get("receipt_name");
			insObj[1] = map.get("gender");
			insObj[2] = map.get("phone");
			insObj[3] = map.get("address_name");
			insObj[4] = map.get("address_detail");
			insObj[5] = map.get("detail");
			insObj[6] = map.get("addr_id");
			insObj[7] = map.get("user_id");

			res = jdbc.update(sql, insObj);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}
	//修改默认收货地址
	public int updDefaultAddr(HashMap map){

		//先插入订单main表
		int res = 0;
		try {
			String sql1= "update address_list set "
					+ "\n		default_addr = null "
					+ "\n		where  user_id = ?" ;
			Object[] insObj1 = new Object[1];
			insObj1[0] = map.get("user_id");

			res = jdbc.update(sql1, insObj1);

			if(res > 0) {
				String sql2= "update address_list set "
						+ "\n		default_addr = ?"
						+ "\n		where a_id = ? and user_id = ?" ;
				System.out.println(sql2);
				Object[] insObj2 = new Object[3];
				insObj2[0] = map.get("addr_id");
				insObj2[1] = map.get("addr_id");
				insObj2[2] = map.get("user_id");

				res = jdbc.update(sql2, insObj2);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	//获取订单主表
	public List<Map<String,Object>> getOrderList(HashMap map){

		String sql ="select a.order_id, a.user_id, a.total_cnt,a.total_price"
				+ "\n		 , a.is_self, a.order_sendtime, a.order_state"
				+ "\n		 , a.user_comment, a.create_dt  "
				+ "\n		FROM order_info a "
				+ "\n	 	WHERE a.user_id = ? ";
				if(Integer.valueOf(map.get("index").toString()) == 1) {
					sql += "\n and a.is_self = 'true'";
				}else if(Integer.valueOf(map.get("index").toString()) == 2) {
					sql += "\n and a.is_self = 'false'";
				}
				sql+= "\n		ORDER BY order_state ASC, create_dt DESC"
				+ "\n		LIMIT ?,? ";

		//传空的参数

		Object[] obj = new Object[3];
		obj[0] = map.get("user_id");
		obj[1] = map.get("last_id");
		obj[2] = map.get("row");

		try {
			List<Map<String,Object>> list  = (List)jdbc.getList(sql, obj);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	//获取临时订单列表
	public List<Map<String,Object>> getOrderDetailList(HashMap map){

		String sql ="select a.order_id, a.total_price, a.is_self,a.order_state,a.order_sendtime, a.order_number, a.is_express"
				+ "\n		, a.user_comment, date_format(a.create_dt,'%Y-%m-%d %T') as create_dt "
				+ "\n		,b.prd_id, b.order_cnt "
				+ "\n		,c.prd_name, c.price,c.image_url"
				+ "\n	 from order_info a, order_info_detail b "
				+ "\n		JOIN prd_list c "
				+ "\n		ON(b.prd_id = c.prd_id)"
				+ "\n where  a.order_id = ?"
				+ "\n	and a.order_id = b.order_id";

		//传空的参数

		  Object[] obj = new Object[]{1}; obj[0] = map.get("order_id");
		  System.out.println(sql);
		try {
			List<Map<String,Object>> list  = (List)jdbc.getList(sql, obj);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	//获取收获地址
	public List<Map<String,Object>> getAddrList(HashMap map){

		String sql ="select a_id, user_id ,gender,  receipt_name, receipt_phone "
				+ "\n		,receipt_address_name,receipt_address_detail, receipt_detail "
				+ "\n		,default_addr , remark, create_dt"
				+ "\n	 from address_list"
				+ "\n where  user_id = ?";
		if(map.get("addr_id") !=null ) {
			sql+="\n	and a_id ="+ map.get("addr_id");
		}

		//传空的参数
		System.out.println(sql);
		Object[] obj = new Object[]{1}; obj[0] = map.get("user_id");
		try {
			List<Map<String,Object>> list  = (List)jdbc.getList(sql, obj);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	//获取收获地址
	public Map<String, Object> getDefaultAddr(HashMap map){

		String sql ="SELECT b.* FROM  order_info a , address_list b "
				+ "\n		WHERE order_id = ?"
				+ "\n			AND IFNULL(a.addr_id,'') = b.a_id "
				+ "\n	 LIMIT 0,1";


		//传空的参数
		System.out.println(sql);
		Object[] obj = new Object[]{1}; obj[0] = map.get("order_id");
		try {
			Map<String, Object> queryForMap = jdbc.queryForMap(sql, obj);
			return queryForMap;
		} catch (Exception e) {

		}
		return null;
	}
	//获取收获地址
	public Map<String, Object> getDefaultAddr_temp(HashMap map){

		String sql ="SELECT b.* FROM  order_info a , address_list b "
				+ "\n		WHERE user_id = ?"
				+ "\n			AND IFNULL(a.addr_id,'') = b.a_id "
				+ "\n		ORDER BY a.create_Dt DESC"
				+ "\n	 LIMIT 0,1";

		String sql2 ="SELECT * FROM address_list where user_id = ? ORDER BY create_dt desc LIMIT 0,1 ";

		//传空的参数
		System.out.println(sql);
		Object[] obj = new Object[]{1}; obj[0] = map.get("user_id");
		try {
			Map<String, Object> queryForMap = jdbc.queryForMap(sql, obj);
			return queryForMap;
		} catch (Exception e) {
			try {
				//没结果就查询 address_list
				Map<String, Object> queryForMap2 = jdbc.queryForMap(sql2, obj);
				return queryForMap2;
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return null;
	}
	//获取收获地址
	public String getOrderNumber(){

		String sql ="SELECT IFNULL(COUNT(*), 0)+1 AS order_number FROM order_info WHERE date_format(create_dt,'%Y%m%d')  = date_format(NOW(),'%Y%m%d') AND order_number != null";


		//传空的参数
		System.out.println(sql);
		//Object[] obj = new Object[]{1}; obj[0] = map.get("user_id");
		try {
			String res= (String) jdbc.getObject(sql, null, String.class);
			return res;
		} catch (Exception e) {
		}
		return null;
	}
}
