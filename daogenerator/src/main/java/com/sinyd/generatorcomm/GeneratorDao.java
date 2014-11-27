package com.sinyd.generatorcomm;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import com.sinyd.generatorcomm.UtilTools;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class GeneratorDao {
	private List<String> tableNames = new ArrayList<String>();
	private List<Map<String, String>> sqlList =new ArrayList<Map<String, String>>();
	private List<Map<String, Object>> sqlTemplateList =new ArrayList<Map<String, Object>>();
	private Set<String> tableNameSet = new HashSet<String>();
	
	private String domainPath="";
	private String sqlTemplatePath="";
	private String sqlStringPath="";
	private String domainVoPath = "";
	
	public GeneratorDao(){}

	public GeneratorDao(String srcStr) {
		this(srcStr, true);
	}
	
	public GeneratorDao(String srcStr,boolean isToLower) {
		this(srcStr.split("#"), isToLower);
	}
	
	public GeneratorDao(List<String> srcList) {
		this(srcList.toArray(new String[0]), true);
	}
	
	public GeneratorDao(List<String> srcList, boolean isToLower) {
		this(srcList.toArray(new String[0]), isToLower);
	}
	
	public GeneratorDao(String[] srcArray,boolean isToLower) {
		for (String tabName : srcArray) {
			tabName = tabName.trim();
			if(isToLower){
				tabName = tabName.toLowerCase();
			}
			tableNames.add(tabName);
		}
	}

	private List<String> getTables(Connection conn)throws SQLException{
		List<String> resTabsList = new ArrayList<String>();
		String[] types = { "TABLE" };
		DatabaseMetaData dbMeta = null;
		ResultSet rs=null;
		
		// 得到所有表名
		dbMeta = conn.getMetaData();
		if(StringUtils.isEmpty(UtilTools.getProVal("DB.username"))){
			rs = dbMeta.getTables(null, null, "%", types);
		}else{
			rs = dbMeta.getTables(null, UtilTools.getProVal("DB.schema.name").toLowerCase(), "%", types);
		}

		System.out.println("@@ALL Table Names:");
		while (rs.next()) {
			System.out.println(rs.getString("TABLE_NAME"));
			resTabsList.add(rs.getString("TABLE_NAME"));
		}

		rs.close();
		rs = null;
		return resTabsList;
	}
	
	private void initPath()throws Exception{
		String path = UtilTools.path;
		String packageStr = UtilTools.packageStr;
		String domainPackageStr = UtilTools.domainPackageStr;
		String domainVoPackageStr = UtilTools.domainVoPackageStr;
		String sqlstringPackageStr = UtilTools.sqlPackageStr;
		
		this.sqlTemplatePath = path + "/resources/sqltemplate/";
		
		String packagePath = path + "/java/";
		for (String str : packageStr.split("\\.")) {
			if (StringUtils.isNotBlank(str)) {
				packagePath = packagePath + str + "/";
			}
		}
		
		this.domainPath = path + "/java/";
		for (String str : domainPackageStr.split("\\.")) {
			if (StringUtils.isNotBlank(str)) {
				this.domainPath = this.domainPath + str + "/";
			}
		}
		
		this.sqlStringPath = path + "/java/";
		for (String str : sqlstringPackageStr.split("\\.")) {
			if (StringUtils.isNotBlank(str)) {
				this.sqlStringPath = this.sqlStringPath + str + "/";
			}
		}
		
		this.domainVoPath = path + "/java/";
		for (String str : domainVoPackageStr.split("\\.")) {
			if (StringUtils.isNotBlank(str)) {
				this.domainVoPath = this.domainVoPath + str + "/";
			}
		}
		
		if(UtilTools.deleteDirFlag){
			UtilTools.deleteDir();
			UtilTools.deleteDirFlag = false;
		}
		UtilTools.makeDir(this.sqlTemplatePath);
		UtilTools.makeDir(packagePath);
		UtilTools.makeDir(this.sqlStringPath);
		UtilTools.makeDir(this.domainPath);
		UtilTools.makeDir(this.domainVoPath);
	}

	public void genDoBean() throws Exception {
		initPath();
		getSqlList(UtilTools.getProperty());
		Configuration cfg = UtilTools.getFreeMarkerCfg();
		Connection conn = UtilTools.getConn();
		
		if(tableNames.size()==0){
			tableNames = this.getTables(conn);
		}
		String sqlPackage=UtilTools.sqlPackageStr;
		
		
		// generate all DAO
		Map<String, Object> docMap;
		for (String tabName : tableNames) {
			try{
				docMap = fillTempContentFromDb(tabName, conn, cfg);
				docMap.put("sqlPackage", sqlPackage);
				fillSqlTemplateData(docMap);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		// generate sql template file
		Map<String, Object> docSqlMap = new HashMap<String, Object>();
		docSqlMap.put("sqlTemplateList", this.sqlTemplateList);
		Template templateSql = cfg.getTemplate("templatecomm/db/SqlTemplate.ftl");
		UtilTools.output(docSqlMap, this.sqlTemplatePath + UtilTools.getProVal("sql.filename").trim(), templateSql);
		
		System.out.println(StringUtils.center("ALL Tables are Generated!!!", 40, "#"));
		conn.close();
	}
	
	private void getSqlList(Properties prop) throws Exception {
		String path = this.sqlTemplatePath + UtilTools.getProVal("sql.filename");
		File file = new File(path);
		if (file.exists()) {
			this.sqlList = UtilTools.getSqlFromXml(file);
			for (Map<String, String> sqlMap : this.sqlList) {
				this.tableNameSet.add(sqlMap.get("tableName"));
			}
		}
	}
	
	private void fillSqlTemplateData(Map<String, Object> docMap){
		String tableName = docMap.get("tableNameReal").toString();
		//String columnName="";
		if(!this.tableNameSet.contains(tableName)){
			this.sqlTemplateList.add(docMap);
		}
	}
	
	private Map<String, Object> fillTempContentFromDb(String tabName, Connection conn, Configuration cfg)throws Exception{
		ResultSet rs=null;
		Statement st=null;
		Map<String, Object> docMap = new HashMap<String, Object>();
		List<Map<String, String>> fieldsList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> fieldsWithoutPKList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> fieldsByteList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> fieldsWithoutByteList = new ArrayList<Map<String, String>>();
		
		System.out.println(">>" + tabName);
		
		docMap.put("domainPackage", UtilTools.domainPackageStr);
		docMap.put("domainVoPackage", UtilTools.domainVoPackageStr);
		docMap.put("databasePackageStr", UtilTools.databasePackageStr);
		docMap.put("tableName", UtilTools.format(tabName, true));
		docMap.put("tableNameReal", tabName);
		docMap.put("tableNamelower", tabName.toLowerCase());
		docMap.put("sqlStringName", "AUTO2_"+tabName.toUpperCase());

		st = conn.createStatement();
		StringBuffer sql = new StringBuffer();
		sql.append("select top 1 Upload,* from ").append(tabName);
		rs = st.executeQuery(sql.toString());
//		rs = st.executeQuery(UtilTools.getOneRecordSql(sql.toString()));
		Map<String, String> defaultValMap = UtilTools.getDefaultByTableName(tabName, conn);
		List<String> pkList = UtilTools.getTablePkMap(tabName, conn);
		
		ResultSetMetaData meta = rs.getMetaData();
		List<String> defaultValColArr = new ArrayList<String>();
		List<Map<String, String>> pkColArr = new ArrayList<Map<String, String>>();
		if(tabName.toLowerCase().equals("testspecificinfomanage")){
			System.out.println("2222");
		}
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String columnName = meta.getColumnName(i).toLowerCase();
			if(UtilTools.isAutoincrement(tabName, columnName, conn)){
				continue;
			}
			if(i==1 || columnName.toLowerCase().equals("orgcode")){
				columnName = columnName.toLowerCase();
			}
			if(i != 1 && columnName.toLowerCase().equals("upload")){
				continue;
			}
			Map<String, String> map = new HashMap<String, String>();
			map.put("dateType", "0");
			
			if(meta.getColumnClassName(i).equals("java.sql.Date")){
				map.put("dateType", "1");
			}else if(meta.getColumnClassName(i).equals("java.sql.Timestamp")){
				map.put("dateType", "2");
			}else if(meta.getColumnClassName(i).equals("[B")){
				map.put("dateType", "3");
			}
			
			String type = UtilTools.jdbcType2Java(meta.getColumnClassName(i));
		
			map.put("type", type);
			map.put("method", meta.getColumnName(i).substring(0, 1).toUpperCase() + meta.getColumnName(i).toLowerCase().substring(1));
			map.put("columnName", columnName);
			map.put("columnNameSymbol", "${"+columnName+"Symbol}");
			String defaultVal = defaultValMap.get(columnName);
			map.put("defaultVal", defaultVal);
			if(defaultVal != null && !defaultVal.trim().equals("")){
				defaultValColArr.add(columnName);
			}
			if(UtilTools.isPkColumn(pkList,columnName)){
				Map<String, String> pkColArrMap = new HashMap<String, String>();
				pkColArrMap.put("columnName", columnName);
				pkColArrMap.put("columnNameSymbol", "${"+columnName+"Symbol}");
				pkColArrMap.put("type", type);
				pkColArr.add(pkColArrMap);
			}else{
				if(!map.get("dateType").equals("3")){
					fieldsWithoutPKList.add(map);
				}
			}
			if(map.get("dateType").equals("3")){
				fieldsByteList.add(map);
			}else{
				fieldsWithoutByteList.add(map);
			}
			fieldsList.add(map);
		}
		
		docMap.put("fieldsWithoutPKList", fieldsWithoutPKList);
		docMap.put("fieldsByteList", fieldsByteList);
		docMap.put("fieldsWithoutByteList", fieldsWithoutByteList);
		docMap.put("fields", fieldsList);
		docMap.put("ifprefix", "<#if ");
		docMap.put("ifmiddle", "??>");
		docMap.put("ifmiddleend", ">");
		docMap.put("ifsuffix", "</#if>");
		docMap.put("elseTag", "<#else>");
		docMap.put("slTag", "${");
		docMap.put("slendTag", "}");
		if(defaultValColArr.size()>0){
			docMap.put("defaultValColArr", defaultValColArr);//默认值列表
		}
			
		if(pkColArr.size()>0){
			docMap.put("pkColArr", pkColArr);//主键列表
			if(tabName.toLowerCase().equals("autocolldataburner") ||
					tabName.toLowerCase().equals("autocolldatamarshall") ||
					tabName.toLowerCase().equals("autocolldatarutting") ||
					tabName.toLowerCase().equals("teststatehistory") ||
					tabName.toLowerCase().equals("temphumiwatchdata")){
				docMap.put("pkNotAutoincrement", "3");//特殊类型，主键需要根据
			}else{
				boolean isAutoincrement = UtilTools.isAutoincrement(tabName,pkColArr,conn);
				if(isAutoincrement){
					docMap.put("pkNotAutoincrement", "0");
				}else{
					docMap.put("pkNotAutoincrement", "1");
				}
			}
		}else{
			docMap.remove("pkColArr");
		}
		rs.close();
		st.close();
		return docMap;
	}
	public static void main(String[] args) throws Exception {
		// need can connect to database 
		new GeneratorDao().genDoBean();
	}
}
