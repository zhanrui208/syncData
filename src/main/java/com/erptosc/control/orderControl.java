package com.erptosc.control;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/order")
public class orderControl {
	
	@RequestMapping(value="/getOrderFinishedDate")
	public @ResponseBody Map<String,Object> getOrderFinishedDate(){
		Map<String,Object> orderFinishedMap = new HashMap<String, Object>();
		orderFinishedMap.put("result", "success");
		orderFinishedMap.put("value", "10001");
		return orderFinishedMap;
	}
	
}
