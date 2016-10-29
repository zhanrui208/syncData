package com.common.httpClient;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;






import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
import com.common.until.StringUtils;


public final class HttpHelper {
	static Logger logger = LoggerFactory.getLogger(HttpHelper.class);

	private static String UTF8 = "UTF-8";
	/**
	 * post接口的json封装
	 * 使用注意：（1） 只有json类型的param映射才被支持。
	 * 		   （2） post默认以x-www-form-urlencoded接收，并且，所有的参数都必须是用String接收，
	 * 				之后再解析为其它类型（json array需要先用String接收，之后用JSONArray解析）
	 * @param headers 可以为空，请求头(注意，当前版本不支持post为json格式)
	 * @param params post的参数
	 * @param url 请求URL 
	 * @return
	 * @throws Exception
	 */
	public static String postJson(Map<String,String> headers,Map<String,Object> params,String url) throws Exception{
		if(params!=null){
			Map<String,String> paraml=new HashMap<String,String>();
			Set<String> keySet=params.keySet();
			for(String key:keySet){
				Object obj=params.get(key);
				if(obj!=null){
					
					if(obj instanceof String){
						//String
						paraml.put(key,(String)obj);
					}else if(obj instanceof Map){
						//Json Object
						JSONObject jo=JSONObject.fromObject(obj);//换成阿里巴巴的json串
//						JSONObject jo= new JSONObject((Map<String,Object>) obj);
						
						paraml.put(key, jo.toString());
					}else if(obj instanceof List){
						//Json Array 
						JSONArray ja=JSONArray.fromObject(obj);//换成阿里巴巴的json串
//						JSONArray ja=new JSONArray((List<Object>) obj);
						paraml.put(key, ja.toString());
					}else{
						//Other (known)
						throw new Exception("com.kingdee.open.util.http.HttpHelper.postJson(Map<String, String>, "
								+ "Map<String, Object>, String)，未知json 参数类型!"+"["+obj.getClass().getName()+"]");
					}
				}else{
					paraml.put(key, null);
				}
			}
			return post(paraml,url,0);
		}else{
			return post(null,url,0);
		}
	}

	public static String post(Map<String,String> params,String url) throws Exception{
		return post(params,url,0);
	}
	/**
	 * 发出post请求，可以指定超时
	 * @author leon_yan
	 * @param params：参数
	 * @param url：
	 * @param timeOutInMillis：指定socket timeout 和 connection timeout ，单位是ms
	 * @return
	 * @throws Exception
	 */
	public static String post(Map<String,String> params,String url,int timeOutInMillis) throws Exception{
		return post(null,params,url,timeOutInMillis);
	}
	
	public static String post(Map<String,String>headers,Map<String,String>params,String url) throws Exception{
		return post(headers,params,url,0);
	}
	/**
	 * 发出http post请求 ，可以指定 headers ，params ,url,timeout
	 * @author leon_yan
	 * @param headers
	 * @param params
	 * @param url
	 * @param timeOutInMillis:指定socket timeout 和 connection timeout ，单位是ms，传入0或负数，代表不设置，默认为不超时
	 * @return
	 * @throws Exception
	 */
	public static String post(Map<String,String>headers,Map<String,String>params,String url,int timeOutInMillis) throws Exception{
		HttpPost post = null;
		try{
			post = getHttpPost(headers,params,url,timeOutInMillis);
			return HttpClientHelper.getHttpClient().execute(post,UTF8);
		}finally{
			if(post != null){
				post.abort();
			}
		}
	}
	/**
	 * 返回http返回结果，结果中包含code
	 * @param headers
	 * @param params
	 * @param url
	 * @param charset
	 * @param timeOutInMillis
	 * @return Map<String,Object>：key包含两个：statusCode：代表返回的httpcode,message:返回的值
	 * @throws Exception
	 */
	public static Map<String,Object> postWithReturnCode(Map<String,String>headers,Map<String,String>params,String url,int timeOutInMillis) throws Exception{
		HttpPost post = null;
		try{
			post = getHttpPost(headers,params,url,timeOutInMillis);
			return HttpClientHelper.getHttpClient().executeAndReturnMap(post,UTF8);
		}finally{
			if(post != null){
				post.abort();
			}
		}
	}
	private static HttpPost getHttpPost(Map<String,String>headers,Map<String,String>params,String url,int timeOutInMillis) throws Exception{
		HttpPost post = null;
		post = new HttpPost(url);
		
		if(headers != null){
			Set<String> set = headers.keySet();
			Iterator<String> it = set.iterator();		
			while(it.hasNext()){
				String key = it.next();
				post.addHeader(key, headers.get(key));					
			}
		}
		
		if(params != null){
			List<NameValuePair> uvp = new LinkedList<NameValuePair>();
			Set<String> set = params.keySet();
			Iterator<String> it = set.iterator();		
			while(it.hasNext()){
				String key = it.next();
				uvp.add(new BasicNameValuePair(key,params.get(key)));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(uvp,UTF8);
			post.setEntity(entity);
		}
		
		if(timeOutInMillis > 0){
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeOutInMillis)
					.setConnectTimeout(timeOutInMillis).build();
			post.setConfig(requestConfig);
		}
		return post;
	}
	public static String get(Map<String,String> params,String url) throws Exception{
		
		return get(null,params,url);		
	}
	
	public static String post(Map<String,String>headers,String jsonObject,String url) throws Exception{
		
		HttpPost post = null;
		try{
			post = new HttpPost(url);
			
			if(headers != null){
				
				Set<String> set = headers.keySet();
				Iterator<String> it = set.iterator();		
				while(it.hasNext()){
					String key = it.next();
					post.addHeader(key, headers.get(key));					
				}
			}
			
			if(StringUtils.isEmpty(jsonObject)){

				throw new Exception("json参数为空！");
			}			
			StringEntity entity = new StringEntity(jsonObject,UTF8);
			post.setEntity(entity);
			return HttpClientHelper.getHttpClient().execute(post,UTF8);
			
		}finally{
			if(post != null){
				post.abort();
			}
		}
	}
	public static String get(Map<String,String>headers,Map<String,String> params,String url,String charset) throws Exception{
		return get(headers,params,url,charset,-1);
	}
	public static String get(Map<String,String> params,String url,int timeOutInMillis) throws Exception{
		return get(null,params,url,UTF8,timeOutInMillis);
	}
	public static String get(Map<String,String>headers,Map<String,String> params,String url,String charset,int timeOutInMillis) throws Exception{
		HttpGet get = null;
		try{			
			get = getHttpGet(headers,params,url,charset,timeOutInMillis);
			return  HttpClientHelper.getHttpClient().execute(get,charset);
		}finally{
			if(get != null){
				get.abort();
			}
		}		
	}
	/**
	 * 返回http返回结果，结果中包含code
	 * @param headers
	 * @param params
	 * @param url
	 * @param charset
	 * @param timeOutInMillis
	 * @return Map<String,Object>：key包含两个：statusCode：代表返回的httpcode,message:返回的值
	 * @throws Exception
	 */
	public static Map<String,Object> getWithReturnCode(Map<String,String>headers,Map<String,String> params,String url,String charset,int timeOutInMillis) throws Exception{
		HttpGet get = null;
		try{			
			get = getHttpGet(headers,params,url,charset,timeOutInMillis);
			return  HttpClientHelper.getHttpClient().executeAndReturnMap(get,charset);
		}finally{
			if(get != null){
				get.abort();
			}
		}		
	}
	private static HttpGet getHttpGet(Map<String,String>headers,Map<String,String> params,String url,String charset,int timeOutInMillis) throws Exception{
		HttpGet get = null;
		if(params != null){
			StringBuffer uri = new StringBuffer(url);
			uri.append("?");
			Set<String> set = params.keySet();
			Iterator<String> it = set.iterator();		
			while(it.hasNext()){
				String key = it.next();
				String value = params.get(key);
				uri.append(urlEncode(key,charset)).append("=").append(urlEncode(value,charset)).append("&");
			}
			uri.deleteCharAt(uri.length()-1);
			get = new HttpGet(uri.toString());
		}else{
			get = new HttpGet(url);
		}
		
		if(headers != null){
			Set<String> set = headers.keySet();
			Iterator<String> it = set.iterator();		
			while(it.hasNext()){
				String key = it.next();
				get.addHeader(key, headers.get(key));					
			}
		}
		if(timeOutInMillis > 0){
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeOutInMillis)
					.setConnectTimeout(timeOutInMillis).build();
			get.setConfig(requestConfig);
		}
		return get;
	}
	public static String get(Map<String,String> headers,Map<String,String> params,String url) throws Exception{
		return get(headers,params,url,UTF8);
	}
	
	public static String get(Map<String,String> params,String url,String charset) throws Exception{
		return get(null,params,url,charset);
	}	
	
	public static String post(Map<String,String> basicParams,String url,Map<String,byte[]> fileParams) throws Exception{

		
		HttpPost post = null;
		try{
			post = new HttpPost(url);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();

			Set<String> set1 = basicParams.keySet();
			Iterator<String> it = set1.iterator();		
			while(it.hasNext()){
				String key = it.next();
				builder.addTextBody(key, basicParams.get(key));
			}
			
			Set<String> set2 = fileParams.keySet();
			Iterator<String> fit = set2.iterator();		
			while(fit.hasNext()){
				String key = fit.next();
				builder.addBinaryBody(key, fileParams.get(key));
			}
			HttpEntity entity = builder.build();
			post.setEntity(entity);
			return HttpClientHelper.getHttpClient().execute(post,UTF8);
			
		}finally{
			if(post != null){
				post.abort();
			}
		}	
	}
	
	public static String getClientIp(HttpServletRequest req){

		String ip = req.getHeader("x-forwarded-for");
		
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){ 
			
			ip = req.getHeader("Proxy-Client-IP"); 
		}	 

		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
			
			ip = req.getHeader("WL-Proxy-Client-IP"); 
		} 
 
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			
			ip = req.getRemoteAddr(); 
		} 

		if(ip.length()<5) {
			
			ip="0.0.0.0";
		}	
		return ip; 
	}
	
	private static String urlUTF8Encode(String str) throws UnsupportedEncodingException{
		
		return URLEncoder.encode(str,UTF8);
	}
	
	private static String urlEncode(String str,String charset) throws UnsupportedEncodingException{
		
		return URLEncoder.encode(str,charset);
	}
	
    /** 
     * 该方法主要是为了解决上面的方便只能够处理200状态其他全部返回null的局限
     * @return HashMap返回HTTP协议状态码以及服务器的响应信息
     * */
	public static HashMap<String,Object> postMap(Map<String,String> params,String url) throws Exception{
		
		HttpPost post = null;
		try{
			post = new HttpPost(url);
			List<NameValuePair> uvp = new LinkedList<NameValuePair>();
			
			Set<String> set = params.keySet();
			Iterator<String> it = set.iterator();		
			while(it.hasNext()){
				String key = it.next();
				uvp.add(new BasicNameValuePair(key,params.get(key)));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(uvp,UTF8);
			post.setEntity(entity);			
			return HttpClientHelper.getHttpClient().executeAndReturnMap(post);
			
		}finally{
			if(post != null){
				post.abort();
			}
		}
	}
	public static String postFile(String url,Map<String,Object> params) throws Exception {
		HttpPost post = null;
		try{
			post = new HttpPost(url);
			MultipartEntityBuilder entity = MultipartEntityBuilder.create();
			Set<String> set = params.keySet();
			Iterator<String> it = set.iterator();
			while(it.hasNext()){
				String key = it.next();
				if("file".equals(key)){
					File file = (File)params.get("file");
					FileBody body = new FileBody(file);
					entity.addPart("file", body);
				}else{
					StringBody body = new StringBody((String)params.get(key),ContentType.APPLICATION_JSON);
					entity.addPart(key, body);
				}
			}
			post.setEntity(entity.build());
			return HttpClientHelper.getHttpClient().execute(post,UTF8);
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		} finally{
			if(post != null){
				post.abort();
			}
		}
	}
	public static void main(String[] args){
//		String url = "http://localhost/openorg/monitor/getstatus";
//		try {
//			for(int i=0;i<1000;i++){
//				Thread.sleep(5);
//				String response = get(new HashMap<String,String>(),url);
//				logger.info("get:"+i);
//				logger.info("response:"+response);
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		String url = "http://localhost/openaccess/user/getPersonByEidAndOpenId";
		Map<String,String> params = new HashMap<String,String>();
		params.put("eid","10109");
		params.put("openId", "537d8a931a06b49a17249432");
		try {
			System.out.println(post(params,url,5000));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}