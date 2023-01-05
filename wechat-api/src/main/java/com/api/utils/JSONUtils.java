package com.api.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;
/**
 * 
 * @Title: JsonUtils.java
 * @Package com.lee.utils
 * @Description: 自定义响应结构, 转换类
 * Copyright: Copyright (c) 2016
 * Company:Nathan.Lee.Salvatore
 * 
 * @author leechenxiang
 * @date 2016年4月29日 下午11:05:03
 * @version V1.0
 */
public class JSONUtils {

    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 将对象转换成json字符串。
     * <p>Title: pojoToJson</p>
     * <p>Description: </p>
     * @param data
     * @return
     */
    public static String objectToJson(Object data) {
    	try {
			String string = MAPPER.writeValueAsString(data);
			return string;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    /**
     * 将json结果集转化为对象
     * 
     * @param jsonData json数据
     * @param clazz 对象中的object类型
     * @return
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
        try {
            T t = MAPPER.readValue(jsonData, beanType);
            return t;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 将json数据转换成pojo对象list
     * <p>Title: jsonToList</p>
     * <p>Description: </p>
     * @param jsonData
     * @param beanType
     * @return
     */
    public static <T>List<T> jsonToList(String jsonData, Class<T> beanType) {
    	JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
    	try {
    		List<T> list = MAPPER.readValue(jsonData, javaType);
    		return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return null;
    }
    
    public static String getRes(String data, String param){
    	JSONObject jsonobject = JSONObject.fromObject(data);
    	
    	String res = jsonobject.get(param).toString();
		return res;
    }
    
    public static ArrayList<HashMap> getResFromJSON(String data){
    	JSONObject jsonobject = JSONObject.fromObject(data);
    	Iterator i = jsonobject.keys();
    	
    	ArrayList list = new ArrayList();
    	HashMap map = new HashMap();
    	
    	int index = 0;
		while(i.hasNext()) {
			String key = (String)i.next();
			String value = jsonobject.getString(key);
			JSONObject jsonobject2 = JSONObject.fromObject(value);
	    	Iterator j = jsonobject2.keys();
	    	map = new HashMap();
	    	while(j.hasNext()) {
	    		String key2 = (String)j.next();
	    		String value2 = jsonobject2.getString(key2);
	    		map.put(key2, value2);
	    	}
	    	list.add(index, map);
	    	index++;
		}
    	return list;
    }
    
}