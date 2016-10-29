package com.common.until;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

public class PropertiesUtils {

	static Logger logger = Logger.getLogger(PropertiesUtils.class);

	private static PropertiesUtils parse=new PropertiesUtils();

	private static Properties properties;

	static String path;
	public static void init(String path) {
		PropertiesUtils.path = path;
		System.out.println(path);
	}

	public static String getProperty(String key) {
		
		return properties.getProperty(key);
	}
	public String getProperty(String key,String defaultValue) {
		return properties.getProperty(key,defaultValue);
	}
	private PropertiesUtils() {

	}

	public static PropertiesUtils getInstance() {
		if (properties == null) {
			if (StringUtils.isEmpty(PropertiesUtils.path)){
				path = PropertiesUtils.class.getClassLoader().getResource("").getPath() + "conf/syncDataConfig.properties";
			}
			try {
				properties= new Properties();
				FileInputStream in = new FileInputStream(new File(path));
				properties.load(in);
			} catch (FileNotFoundException e) {
				logger.info(e.getMessage() + e.getCause());
			} catch (IOException e) {
				logger.info(e.getMessage() + e.getCause());
			}
		}
		return parse;
	}

	public static boolean isDev() {
		return Boolean.valueOf(parse.getProperty("openimport.isdev"));
	}
}