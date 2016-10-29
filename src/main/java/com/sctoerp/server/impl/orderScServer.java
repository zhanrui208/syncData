package com.sctoerp.server.impl;

import java.util.HashMap;
import java.util.Map;

import com.common.httpClient.HttpHelper;
import com.common.until.PropertiesUtils;

public class orderScServer {
	public String  getOrderFinishedDate(){
		Map<String,String> params = new HashMap<String,String>();
		params.put("id", "1001");
		params.put("name", "ERP传数据到商城！");
		String msg ="";
		String url = PropertiesUtils.getInstance().getProperty("ERPURL");
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
