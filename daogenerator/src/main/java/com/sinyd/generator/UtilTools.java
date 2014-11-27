package com.sinyd.generator;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;


/**
 * 常用到的一些工具方法
 * */
public class UtilTools {
	/**
	 * 格式化处理表名和字段名：转为小写，去掉下划线，首字母大写
	 * @param initCap	true－整个名字首字母大写（类名,方法名）；false－整个名字首字母不大写（字段名）
	 */
	public static String format(String s, boolean initCap){
		StringBuffer name = new StringBuffer();
		String[] tmp = s.trim().toLowerCase().split("_");
		for(int i=0; i<tmp.length; i++)
			name.append(tmp[i].substring(0, 1).toUpperCase()).append(tmp[i].substring(1));

		if(initCap){
			return name.toString();
		}else{
			return name.substring(0, 1).toLowerCase() + name.substring(1);
		}
	}
	
	/**
	 * jdbc对象与java对象之间转换
	 * */
	public static String jdbcType2Java(String sqlType)throws Exception {
		String ret = sqlType.replaceAll("java\\.lang\\.", "");
		if (ret.equals("java.sql.Timestamp") || ret.equals("java.sql.Date")){
//			ret = "java.util.Date";
			ret = "String";
		}
		if(ret.equals("Object")){
			ret = "String";
		}
		return ret;
	}

	public static List<Map<String, String>> getSqlFromXml(File file) throws Exception{
		List<Map<String, String>> sqlList =new ArrayList<Map<String,String>>();
		getSqlFromXml(file, sqlList);
		return sqlList;
	}
	
	public static void getSqlFromXml(File file, List<Map<String, String>> sqlList) throws Exception{
		Map<String, String> singleMap;
		Document doc = null;
		SAXReader read = new SAXReader();
		doc = read.read(file);

		Element root = doc.getRootElement();
		String name,key,tableName;
		String nameSpace = root.attributeValue("namespace");
		if(StringUtils.isEmpty(nameSpace)){
			nameSpace="";
		}else{
			nameSpace=nameSpace+"@";
		}
		for (Iterator<Element> element = root.elementIterator(); element.hasNext();) {
			Element sql = element.next();
			if ("sqlElement".equals(sql.getName())) {
				singleMap = new HashMap<String, String>();
				key = sql.attribute("key").getValue();
				tableName = key.split("#")[0];
				name = nameSpace+key;
				singleMap.put("key", key);
				singleMap.put("tableName", tableName);
				singleMap.put("content", sql.getText().trim());
				singleMap.put("realName", name);
				singleMap.put("valName", UtilTools.replaceStr(name));
				sqlList.add(singleMap);
			}
		}
	}
	
	public static String replaceStr(String srcStr){
		String resStr = srcStr;
		resStr = resStr.replace("@", "_");
		resStr = resStr.replace("#", "_");
		return resStr.toUpperCase();
	}
	
	public static void makeDir(String path)throws Exception{
		File dir = new File(path);
		if (dir.exists() == false){
			dir.mkdirs();
		}
	}
	
	/**
	 * 字符串判断空与非空的方法
	 * */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

	/**
	 * get database connection
	 * */
	public static Connection getConn(Properties prop) throws Exception {
		Class.forName(prop.getProperty("DB.driver"));
		Connection conn = DriverManager.getConnection(prop.getProperty("DB.url"), 
				prop.getProperty("DB.username").trim(),
				prop.getProperty("DB.password").trim());

		System.out.println(StringUtils.center("Get Connection Success!", 40, "="));
		return conn;
	}
	
	/**
	 * init properties file
	 * */
	public static Properties getProperty()throws Exception {
		Properties prop = new Properties();
		prop.load(GeneratorDao.class.getResourceAsStream("/gen.properties"));
		return prop;
	}
	
	/**
	 * init freemarker configure
	 * */
	public static Configuration getFreeMarkerCfg(Properties prop) throws Exception {
		Configuration cfg = null;
		cfg = new Configuration();
		File file = new File(GeneratorDao.class.getResource("/").getPath());
		cfg.setDirectoryForTemplateLoading(file);
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		System.out.println(StringUtils.center("Init Freemarker Success!", 40, "="));
		return cfg;
	}
	
	public static Map<String, String> getDefaultByName(String tableName, Connection conn) throws Exception{
		Map<String, String> resMap = new HashMap<String, String>();
		ResultSet rs = conn.createStatement().executeQuery("SELECT a.attname, f.adsrc FROM pg_class c join pg_attribute a on a.attrelid = c.oid  join pg_attrdef f on  f.adrelid=c.oid and a.attnum=f.adnum  WHERE c.relname = '"+tableName+"' and a.attnum > 0 and a.attname!='id'");
		while(rs.next()){
			resMap.put(rs.getString("attname").toLowerCase(), dealVal(rs.getString("adsrc"), conn));
		}
		return resMap;
	}
	
	private static String dealVal(String val, Connection conn) throws SQLException{
		if(StringUtils.isBlank(val)){
			return null;
		}
		String resStr = val.trim();
		boolean isString = false;
		if(resStr.indexOf("bpchar")>=0 || resStr.indexOf("text")>=0){
			isString = true;
		}
		if(resStr.indexOf("nextval(")==-1){
			PreparedStatement pst = conn.prepareStatement("select  "+resStr);
			ResultSet rs = pst.executeQuery();
			if(rs.next()){
				resStr = rs.getString(1);
				if(isString){
					resStr = "\""+resStr+"\"";
				}
			}
			return resStr;
//		}else{
//			String[] arrVal = resStr.split("::");
//			if(arrVal.length==2 && (arrVal[1].equals("bpchar") || arrVal[1].equals("character varying"))){
//				resStr = arrVal[0].replaceAll("'", "");
//				resStr = "\""+resStr+"\"";
//				return resStr;
//			}
		}
		return null;
	}
}
