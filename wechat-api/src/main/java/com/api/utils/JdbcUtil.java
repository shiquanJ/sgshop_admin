package com.api.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
@Component
public class JdbcUtil {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/***
	 * 
	 * @param sql 	= sql语句
	 * @param obj	= 参数
	 * @return
	 */
	public List<Map<String,Object>> getList(String sql, Object[] obj){
		
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql, obj);
		
		return list;
	}
	
	/***
	 * 
	 * @param sql	= 语句
	 * @param obj	= 参数
	 * @param Class<T> t = 返回类型 ， 例: String.class就返回String类型， Integer.class返回int类型
	 * @return
	 */
	public <T> Object getObject(String sql , Object[] obj ,  Class<T> t){
		
		return jdbcTemplate.queryForObject(sql, t, obj);
	}
	
	/***
	 * 
	 * @param sql	= 语句
	 * @param obj	= 参数
	 * @param 
	 * @return
	 */
	public Map<String,Object> queryForMap(String sql , Object[] obj){
		
		return jdbcTemplate.queryForMap(sql, obj);
	}
	
	/***
	 * 
	 * @param sql 	= sql语句
	 * @param obj	= 参数
	 * @return
	 */
	public int update(String sql, Object[] obj){
		return (Integer)jdbcTemplate.update(sql, obj);
	}
	
}
