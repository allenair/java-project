package com.sinyd.generator2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import com.sinyd.generator.GeneratorDao;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class UtilTools {
	private static Properties property;
	public static String path;
	public static String packageStr;
	public static String domainPackageStr;
	public static String domainVoPackageStr;
	public static String sqlPackageStr;
	public static String databasePackageStr;
	public static String modualPackageStr;
	public static Boolean deleteDirFlag;

	static {
		property = new Properties();
		deleteDirFlag = true;
		try {
			property.load(GeneratorDao.class.getResourceAsStream("/gen2.properties"));
			initPackagePath();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static String getProVal(String key) {
		if(StringUtils.isNotEmpty(property.getProperty(key))){
			return property.getProperty(key).trim();
		}
		return "";
	}
	
	private static void initPackagePath() {
		path = getProVal("dest.dir");
		packageStr = getProVal("dest.package");
		domainPackageStr = packageStr+"."+getProVal("gen.package")+".gen.domain";
		domainVoPackageStr = packageStr+"."+getProVal("gen.package")+".gen.vo";
		sqlPackageStr = packageStr+"."+getProVal("gen.package")+".gen";
		databasePackageStr = packageStr+"."+getProVal("gen.package")+".database";
		modualPackageStr = packageStr+"."+getProVal("modual.package");
	}

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
	
	public static String jdbcType2Java(String sqlType)throws Exception {
		String ret = sqlType.replaceAll("java\\.lang\\.", "");
		/*if (ret.equals("java.sql.Timestamp") || ret.equals("java.sql.Date")){
			ret = "String";
		}*/
		if(ret.equals("Object")){
			ret = "String";
		}
		if(ret.equals("[B")){
			ret = "byte[]";
		}
		return ret;
	}
	
	public static Connection getConn() throws Exception {
		Class.forName(getProVal("DB.driver"));
		Connection conn = DriverManager.getConnection(getProVal("DB.url"), 
				getProVal("DB.username").trim(), getProVal("DB.password").trim());

		System.out.println(StringUtils.center("Get Connection Success!", 40, "="));
		return conn;
	}
	
	public static Configuration getFreeMarkerCfg() throws Exception {
		Configuration cfg = new Configuration();
		File file = new File(UtilTools.class.getResource("/").getPath());
		cfg.setDirectoryForTemplateLoading(file);
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		System.out.println(StringUtils.center("Init Freemarker Success!", 40, "="));
		return cfg;
	}
	
	/**
	 * init properties file
	 * */
	public static Properties getProperty()throws Exception {
		Properties prop = new Properties();
		prop.load(GeneratorDao.class.getResourceAsStream("/gen.properties"));
		return prop;
	}
	
	public static Map<String, String> getDefaultByTableName(String tableName, Connection conn) throws Exception{
		Map<String, String> resMap = new HashMap<String, String>();
		StringBuilder sql = new StringBuilder();
		if("postgresql".equals(getProVal("DB.type"))){
			sql.append("SELECT a.attname, f.adsrc  ");
			sql.append("  FROM pg_class c  ");
			sql.append("  join pg_attribute a on a.attrelid = c.oid  ");
			sql.append("  join pg_attrdef f on  f.adrelid=c.oid and a.attnum=f.adnum   ");
			sql.append(" WHERE c.relname = '").append(tableName).append("' and a.attnum > 0 and a.attname!='id'");
			
		}else if("mssql".equals(getProVal("DB.type"))){
			sql.append("SELECT SC.NAME as attname, SM.TEXT as adsrc ");
			sql.append("  FROM dbo.sysobjects SO  ");
			sql.append("  INNER JOIN dbo.syscolumns SC ON SO.id = SC.id  ");
			sql.append("  LEFT JOIN dbo.syscomments SM ON SC.cdefault = SM.id   ");
			sql.append(" WHERE SO.xtype = 'U' AND SM.TEXT IS NOT NULL AND SO.NAME='").append(tableName).append("' ");
		}else{
			throw new Exception("DB type is ERROR!!!");
		}
		
		ResultSet rs = conn.createStatement().executeQuery(sql.toString());
		while(rs.next()){
			resMap.put(rs.getString("attname").toLowerCase(), rs.getString("adsrc"));
		}
		return resMap;
	}
	
	public static List<Map<String, String>> getSqlFromXml(File file) throws Exception{
		List<Map<String, String>> sqlList =new ArrayList<Map<String, String>>();
		getSqlFromXml(file, sqlList);
		return sqlList;
	}
	
	@SuppressWarnings("unchecked")
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
		if (dir.exists() == false) {
			dir.mkdirs();
		}
	}
	
	public static void deleteDir() throws Exception {
		String dirStr = getProVal("dest.dir");
		File dir = new File(dirStr);
		if (dir.exists() == true) {
			delFile(dir);
		}
	}
	
	public static List<String> getTablePkMap(String tabName, Connection conn)throws SQLException{
		DatabaseMetaData dbm = conn.getMetaData();
		ResultSet rs = dbm.getPrimaryKeys(null, UtilTools.getProVal("DB.schema.name"), tabName);
		List<String> pkList = new ArrayList<String>();
		while (rs.next()) {
			pkList.add(rs.getString("COLUMN_NAME").toLowerCase());   
		}
		rs.close();
		rs = null;
		return pkList;
	}
	
	public static boolean isPkColumn(List<String> pkList, String columnName) {
		if(pkList != null && pkList.size()>0){
			for(String pkColum : pkList){
				if(pkColum.toLowerCase().equals(columnName.toLowerCase())){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 递归删除文件夹下的所有文件及文件夹
	 * @param dir
	 */
	private static void delFile(File dir){
		if(dir.isDirectory()){
			File [] files = dir.listFiles();
			for(File file : files){
				delFile(file);
			}
		}
		dir.delete();
	}
	
	public static String getDateFuncName(){
		if("postgresql".equals(getProVal("DB.type"))){
			return "now()";
		}else if("mssql".equals(getProVal("DB.type"))){
			return "getdate()";
		}
		return "";
	}
	
	/**
	 *将日期类型转换为字符类型
	 * @param ColumName
	 * @param dateType
	 * @return
	 */
	public static String getDateToStringFun(String ColumName,String dateType){
		StringBuilder sql = new StringBuilder();
		if("postgresql".equals(getProVal("DB.type"))){
			if("java.sql.Date".equals(dateType)){
				sql.append("to_char(").append(ColumName).append(", 'YYYY-MM-DD') ");
			}else if("java.sql.Timestamp".equals(dateType)){
				sql.append("to_char(").append(ColumName).append(", 'YYYY-MM-DD HH24:MI:SS') ");
			}
			
			return sql.toString();
		}else if("mssql".equals(getProVal("DB.type"))){
			if("java.sql.Date".equals(dateType)){
				sql.append("CONVERT(varchar,").append(ColumName).append(", 23) ");
			}else if("java.sql.Timestamp".equals(dateType)){
				sql.append("CONVERT(varchar,").append(ColumName).append(", 20) ");
			}
			return sql.toString();
		}else{
			return null;
		}
	}
	
	public static String getOneRecordSql(String sql){
		StringBuilder sb = new StringBuilder();
		if("postgresql".equals(getProVal("DB.type"))){
			sb.append("select * from ( ");
			sb.append(sql);
			sb.append(" ) as _T  limit 1  ");
			return sb.toString();
		}else if("mssql".equals(getProVal("DB.type"))){
			sb.append("select top 1 * from ( ");
			sb.append(sql);
			sb.append(" ) as _T ");
			return sb.toString();
		}else{
			return sql;
		}
	}
	/**
	 * 输出根据模板产生的代码
	 */
	public static void output(Map<String, Object> docMap, String fileName, Template template) throws Exception {
		FileWriter fileWriter = new FileWriter(fileName);
		template.process(docMap, fileWriter);
		fileWriter.flush();
		fileWriter.close();
	}
}
