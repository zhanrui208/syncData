package com.common.until;


public class StringUtils {

	public static boolean isEmpty(String str){
		
		return str == null || str.trim().length() == 0;
	}
    public static String cnull(String str){
    	return str == null ? "" :str;
    }
    public static String toString(Object obj){
    	return obj == null ? "" : obj.toString();
    }
    public static void main(String[] args){
    	System.out.println(StringUtils.isEmpty(" "));
    }
    
    public static boolean hasText(String str){
		if(str!=null && str.trim().length()>0){
			return true;
		}
		return false;
	}
}
