package com.erptosc.control;

import java.util.HashMap;
import java.util.Map;

import org.apache.jasper.tagplugins.jstl.core.ForEach;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
@RequestMapping("/order")
public class orderControl {
	
	@RequestMapping(value="/getfromsc")
	public @ResponseBody Map<String,Object> getOrder(Map<String,String> params ){
		Map<String,Object> orderFinishedMap = new HashMap<String, Object>();
		int i= 0 ;
		for(String key : params.keySet()){
			i++;
			orderFinishedMap.put(i+"", "获取的第"+ i +"个KEY值为：" + key + ",value值为：" + params.get(key));
		}
		return orderFinishedMap;
	}
	
    @RequestMapping("/hello")
    public String hello(){        
        return "hello";
    }
}
