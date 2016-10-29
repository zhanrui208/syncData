package com.sctoerp.control;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

public class orderScControl {
	@RequestMapping(value="/getfromerp")
	public @ResponseBody Map<String,Object> getOrderFinishedDate(Map<String,String> params ){
		Map<String,Object> orderFinishedMap = new HashMap<String, Object>();
		int i= 0 ;
		for(String key : params.keySet()){
			i++;
			orderFinishedMap.put(i+"", "获取的第"+ i +"个KEY值为：" + key + ",value值为：" + params.get(key));
		}
		return orderFinishedMap;
	}
}
