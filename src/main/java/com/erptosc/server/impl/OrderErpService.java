package com.erptosc.server.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.httpClient.HttpHelper;
import com.common.until.PropertiesUtils;
import com.erptosc.control.orderControl;
import com.erptosc.server.iOrderErpService;

@Service
public class OrderErpService implements iOrderErpService{

//	@Autowired
//	orderControl order;
	
	@Override
	public String getOrder() {
//		order.getOrder(null);
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("id", "1001");
		params.put("name", "ERP传数据到商城！");
		String msg ="";
		String url = PropertiesUtils.getInstance().getProperty("SCURL");
		url += "/order/getfromsc";
		try {
			msg = HttpHelper.post(params, url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg= e.getMessage();
		}
		return msg;
	}
}
