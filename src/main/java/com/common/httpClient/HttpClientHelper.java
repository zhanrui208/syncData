package com.common.httpClient;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


import org.apache.http.HttpEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.common.until.PropertiesUtils;



public final class HttpClientHelper {
	
	private static Logger logger = LoggerFactory.getLogger(HttpClientHelper.class);

    private static HttpClientHelper instance = null;
    private static Lock lock = new ReentrantLock();
    private CloseableHttpClient httpClient;

    private HttpClientHelper() {
        instance = this;
    }

    public static HttpClientHelper getHttpClient() {
    	
        if (instance == null) {
            lock.lock();
            try{
            	if (instance == null) {
                    instance = new HttpClientHelper();
                    instance.init();
                }
            }finally{
            	 lock.unlock();
            }
        }
        return instance;
    }
    private static int MAX_TOTEL = Integer.parseInt(PropertiesUtils.getInstance().getProperty("HTTP_MAX_CONNECTION", "1000"));
    private static int MAX_CONNECTION_PER_ROUTE = Integer.parseInt(PropertiesUtils.getInstance().getProperty("HTTP_MAX_CONNECTION_PER_ROUTE", "100"));
    private void init() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(MAX_TOTEL);
        cm.setDefaultMaxPerRoute(MAX_CONNECTION_PER_ROUTE);
        httpClient = HttpClientBuilder.create()
                .setConnectionManager(cm)
                .build();
    }

    public byte[] executeAndReturnByte(HttpRequestBase request) {
        HttpEntity entity = null;
        CloseableHttpResponse resp = null;
        byte[] rtn = new byte[0];
        if (request == null) 
        	return rtn;
        try {
        	lock.lock();
            try{
            	if(httpClient == null){
            		
            		init();        		
            	}
            }finally{
            	 lock.unlock();
            }        	
        	
        	if(httpClient == null){
        		
        		logger.error("{}\nreturn error {}",request.getURI().toString(),"httpClient获取异常！");
        		return rtn;        		
        	}
        	resp = httpClient.execute(request);
            entity = resp.getEntity();
            if (resp.getStatusLine().getStatusCode() == 200) {
                String encoding = ("" + resp.getFirstHeader("Content-Encoding")).toLowerCase();
                if (encoding.indexOf("gzip") > 0) {
                    entity = new GzipDecompressingEntity(entity);
                }
                rtn = EntityUtils.toByteArray(entity);
            } else  if (resp.getStatusLine().getStatusCode() == 400) {
            	rtn = EntityUtils.toByteArray(entity);
            	logger.error("{}\nreturn error httpstatus code:{}",request.getURI().toString(),resp.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        } finally {
            EntityUtils.consumeQuietly(entity);
            if (resp != null) {
                try {
                    resp.close();
                } catch (Exception ignore) {
                }
            }
        }
        return rtn;
    }

    public String execute(HttpRequestBase request,String charset) throws UnsupportedEncodingException {
        byte[] bytes = executeAndReturnByte(request);
        if (bytes == null || bytes.length == 0) 
        	return null;
        return new String(bytes,charset);
    }
    
    /** 
     * 该方法主要是为了解决上面的方便只能够处理200状态其他全部返回null的局限
     * 该方法可以返回状态吗以及服务器的响应信息，根据状态等信息自己再处理
     * @param request 请求参数
     * 
     * @return HashMap返回HTTP协议状态码以及服务器的响应信息
     * */
    public HashMap<String,Object> executeAndReturnMap(HttpRequestBase request,String charset) {
        HttpEntity entity = null;
        CloseableHttpResponse resp = null;
        HashMap<String, Object> returnMap=new HashMap<String, Object>();
        if (request == null) 
        	return null;
        try {
        	lock.lock();
            try{
            	if(httpClient == null){
            		
            		init();        		
            	}
            }finally{
            	 lock.unlock();
            }        	
        	
        	if(httpClient == null){
        		
        		logger.error("{}\nreturn error {}",request.getURI().toString(),"httpClient获取异常！");
        		return null;        		
        	}
        	resp = httpClient.execute(request);
            entity = resp.getEntity();
            if (resp.getStatusLine().getStatusCode() == 200) {
                String encoding = ("" + resp.getFirstHeader("Content-Encoding")).toLowerCase();
                if (encoding.indexOf("gzip") > 0) {
                    entity = new GzipDecompressingEntity(entity);
                }
                returnMap.put("statusCode", "200");
                returnMap.put("message", formatByte(EntityUtils.toByteArray(entity), charset));
            } else{
            	returnMap.put("statusCode",String.valueOf(resp.getStatusLine().getStatusCode()));
            	returnMap.put("message", formatByte(EntityUtils.toByteArray(entity), charset));
            	logger.error("{}\nreturn error httpstatus code:{}",request.getURI().toString(),resp.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        } finally {
            EntityUtils.consumeQuietly(entity);
            if (resp != null) {
                try {
                    resp.close();
                } catch (Exception ignore) {
                }
            }
        }
        return returnMap;
    }
    /** 
     * 该方法主要是为了解决上面的方便只能够处理200状态其他全部返回null的局限
     * 该方法可以返回状态吗以及服务器的响应信息，根据状态等信息自己再处理
     * @param request 请求参数
     * 
     * @return HashMap返回HTTP协议状态码以及服务器的响应信息
     * */
    public HashMap<String,Object> executeAndReturnMap(HttpRequestBase request) {
        return executeAndReturnMap(request,"UTF8");
    }
    
    /** 
     * @param bytes 服务器响应信息
     * @param charset 字符编码类型
     * @return String返回byte转换为String后的字符串
     * */
    public String formatByte(byte[] bytes,String charset) throws UnsupportedEncodingException {
        if (bytes == null || bytes.length == 0) 
        	return null;
        return new String(bytes,charset);
    }
}
