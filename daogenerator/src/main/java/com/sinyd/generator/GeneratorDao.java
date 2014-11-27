package com.sinyd.generator;

import java.io.File;
import java.io.FileWriter;
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

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * @author allen
 * 根据数据库中的表自动生成domain对象、相应的sql模板的xml文件、SqlString常量文件
 */
public class GeneratorDao {
	private List<String> tableNames = new ArrayList<String>();
	private List<Map<String, String>> sqlList =new ArrayList<Map<String,String>>();
	private Set<String> tableNameSet = new HashSet<String>();

	private String domainPath="";
	private String sqlTemplatePath="";
	private String sqlStringPath="";
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
//		if(args.length<1){
//			new GeneratorDao().genDaoBean();
//		}else if(args.length==1){
//			new GeneratorDao(args[0]).genDaoBean();
//		}
		
		new GeneratorDao("mc_temp_map_data#mc_alert_send#sys_back_task#platform_sys_config#mc_test_alert_ti#mc_spec_test_alert_ti#mc_temp_lab_sum_ti#mc_warn_deal#mc_temp_alert_detail").genDaoBean();
		
	}
	
	/**
	 * 获取相应的参数，并进行初始化
	 */
	public GeneratorDao() {
	}

	public GeneratorDao(String srcStr) {
		this(srcStr, false);
	}
	public GeneratorDao(String srcStr,boolean isToLower) {
		if(srcStr == null || srcStr.trim().equals("")){
			return;
		}
		String[] tabArr=null;
		if (srcStr != null){
			tabArr = srcStr.split("#");
		}
		for (String tabName : tabArr) {
			tabName = tabName.trim();
			if(isToLower){
				tabName = tabName.toLowerCase();
			}
			tableNames.add(tabName);
		}
	}

	/**
	 * 生成DAO的pojo代码
	 */
	public void genDaoBean() throws Exception {
		Properties prop = UtilTools.getProperty();
		init(prop);
		Connection conn = UtilTools.getConn(prop);
		Configuration cfg = UtilTools.getFreeMarkerCfg(prop);
		
		if(tableNames.size()==0){
			tableNames = this.getTables(conn, prop);
		}
		
		String sqlPackage="";
		if(StringUtils.isNotBlank(prop.getProperty("sqlstring.package.special"))){
			sqlPackage = prop.getProperty("sqlstring.package.special").trim();
		}else{
			sqlPackage = prop.getProperty("dest.package")+".util";
		}
		
		// generate all DAO
		Map<String, Object> docMap;
		Template template = cfg.getTemplate("template/db/DaoTemplate.ftl");
		for (String tabName : tableNames) {
			try{
				docMap = fillTempContentFromDb(tabName, conn, prop, cfg);
				docMap.put("sqlPackage", sqlPackage);
				fillSqlTemplateData(docMap);
				output(docMap, this.domainPath + UtilTools.format(tabName, true) + ".java", template);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		// generate sql template file
		docMap = new HashMap<String, Object>();
		docMap.put("sqlList", this.sqlList);
		template = cfg.getTemplate("template/db/SqlTemplate.ftl");
		output(docMap, this.sqlTemplatePath + prop.getProperty("sql.filename").trim(), template);
		
		// generate SqlString.java 
		docMap = new HashMap<String, Object>();
		docMap.put("sqlPackage", sqlPackage);
		docMap.put("sqlList", UtilTools.getSqlFromXml(new File(this.sqlTemplatePath + prop.getProperty("sql.filename").trim())));
		
		template = cfg.getTemplate("template/db/SqlString.ftl");
		output(docMap, this.sqlStringPath + "SqlString.java", template);
		
		
		System.out.println(StringUtils.center("ALL Tables are Generated!!!", 40, "#"));
		
		conn.close();
	}

	/**
	 * init properties file
	 * */
	private void init(Properties prop)throws Exception {
		initPath(prop);
		getSqlList(prop);
	}
	
	/**
	 * get all tables name from database
	 * */
	private List<String> getTables(Connection conn, Properties prop)throws SQLException{
		List<String> resTabsList = new ArrayList<String>();
		String[] types = { "TABLE" };
		DatabaseMetaData dbMeta = null;
		ResultSet rs=null;
		
		// 得到所有表名
		dbMeta = conn.getMetaData();
		if(prop.getProperty("DB.username")==null || prop.getProperty("DB.username").trim().equals("")){
			rs = dbMeta.getTables(null, null, "%", types);
		}else{
			rs = dbMeta.getTables(null, prop.getProperty("DB.schema.name").toLowerCase(), "%", types);
		}

		System.out.println("@@ALL Table Names:");
		while (rs.next()) {
			resTabsList.add(rs.getString("TABLE_NAME"));
		}

		rs.close();
		rs = null;
		return resTabsList;
	}
	
	private Map<String, Object> fillTempContentFromDb(String tabName, Connection conn, Properties prop, Configuration cfg)throws Exception{
		ResultSet rs=null;
		Statement st=null;
		Map<String, Object> docMap = new HashMap<String, Object>();
		List<Map<String, String>> fieldsList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> fieldsListWithoutId = new ArrayList<Map<String, String>>();
		List<String> dateColArr = new ArrayList<String>();
		
		System.out.println(">>" + tabName);
		
		docMap.put("package", prop.getProperty("dest.package"));
		docMap.put("tableName", UtilTools.format(tabName, true));
		docMap.put("tableNameReal", tabName);
		docMap.put("sqlStringName", "AUTO_"+tabName.toUpperCase());

		st = conn.createStatement();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ").append(tabName).append("  limit 0 ");
		rs = st.executeQuery(sql.toString());
		Map<String, String> defaultValMap = UtilTools.getDefaultByName(tabName, conn);
		
		ResultSetMetaData meta = rs.getMetaData();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("dateType", "0");
			if(meta.getColumnClassName(i).equals("java.sql.Date")){
				dateColArr.add(meta.getColumnName(i).toLowerCase());
				map.put("dateType", "1");
			}
			if(meta.getColumnClassName(i).equals("java.sql.Timestamp")){
				dateColArr.add(meta.getColumnName(i).toLowerCase());
				map.put("dateType", "2");
			}
			
			map.put("type", UtilTools.jdbcType2Java(meta.getColumnClassName(i)));
			map.put("method", meta.getColumnName(i).substring(0, 1).toUpperCase() + meta.getColumnName(i).substring(1));
			map.put("columnName", meta.getColumnName(i).toLowerCase());
			map.put("defaultVal", defaultValMap.get(meta.getColumnName(i).toLowerCase()));
			
			if("id".equals(meta.getColumnName(i).toLowerCase())){
				docMap.put("idType", UtilTools.jdbcType2Java(meta.getColumnClassName(i)));
			}else{
				fieldsListWithoutId.add(map);
			}
			fieldsList.add(map);
			
			//System.out.println(meta.getColumnName(i)+"##"+meta.isAutoIncrement(i));
		}
		
		docMap.put("dateColArr", dateColArr);
		docMap.put("fields", fieldsList);
		docMap.put("fieldsWithoutId", fieldsListWithoutId);
		
		rs.close();
		st.close();
		return docMap;
	}
	
	private void fillSqlTemplateData(Map<String, Object> docMap){
		String tableName = docMap.get("tableNameReal").toString();
		String columnName="";
		
		if(!this.tableNameSet.contains(tableName)){
			StringBuilder sb;
			Map<String, String> singleTableMap = new HashMap<String, String>();
			singleTableMap.put("tableName", tableName);
			
			// create delete=========================================
			sb = new StringBuilder();
			sb.append(" delete from ").append(tableName).append(" where  id=:id ");
			singleTableMap.put("delete", sb.toString());
			
			// create list all=========================================
			sb = new StringBuilder();
			sb.append(" select * from ").append(tableName).append("  order by id");
			singleTableMap.put("list", sb.toString());
			
			// create list by pk=========================================
			sb = new StringBuilder();
			sb.append(" select * from ").append(tableName).append(" where  id=:id ");
			singleTableMap.put("listbypk", sb.toString());
			
			List<Map<String, String>> fieldsListWithoutId = (List<Map<String, String>>)docMap.get("fieldsWithoutId");
			String tmp="",sql="";
			
			// create update=========================================
			sb = new StringBuilder();
			sb.append(" update ").append(tableName).append(" set ");
			for (Map<String, String> column : fieldsListWithoutId) {
				columnName = column.get("columnName");
				if("create_timestamp".equalsIgnoreCase(columnName) || "create_user".equalsIgnoreCase(columnName)){
					// 跳过，不更新此字段
				}else if("last_update_timestamp".equalsIgnoreCase(columnName)){
					sb.append("last_update_timestamp=now()").append(",");
				}else{
					sb.append(columnName).append("=:").append(columnName).append(",");
				}
			}
			tmp = sb.toString();
			tmp = tmp.substring(0, tmp.length()-1);
			singleTableMap.put("update", tmp+"   where id=:id ");
			
			// create insert=========================================
			sb = new StringBuilder();
			StringBuilder sbVal = new StringBuilder();
			sb.append(" insert into ").append(tableName).append("  (");
			sbVal.append(" values (");
			for (Map<String, String> column : fieldsListWithoutId) {
				columnName = column.get("columnName");
				sb.append(columnName).append(",");
				if("create_timestamp".equalsIgnoreCase(columnName) || "last_update_timestamp".equalsIgnoreCase(columnName)){
					sbVal.append("now()").append(",");
				}else{
					sbVal.append(":").append(columnName).append(",");
				}
			}
			tmp = sb.toString();
			tmp = tmp.substring(0, tmp.length()-1);
			sql = tmp+") ";
			
			tmp = sbVal.toString();
			tmp = tmp.substring(0, tmp.length()-1);
			sql = sql + tmp + ") ";
			singleTableMap.put("insert", sql);
			
			
			this.sqlList.add(singleTableMap);
		}
	}
	
	/**
	 * 输出根据模板产生的代码
	 */
	private void output(Map<String, Object> docMap, String fileName, Template template) throws Exception {
		FileWriter fileWriter = new FileWriter(fileName);
		template.process(docMap, fileWriter);
		fileWriter.flush();
		fileWriter.close();
	}

	private void getSqlList(Properties prop)throws Exception{
		String path = this.sqlTemplatePath+prop.getProperty("sql.filename");
		File file = new File(path);
		if (file.exists()){
			this.sqlList = UtilTools.getSqlFromXml(file);
			// record all keys, make sure the new generated key is not repeated
			for (Map<String, String> sqlMap : this.sqlList) {
				this.tableNameSet.add(sqlMap.get("tableName"));
			}
		}
	}
	
	private void initPath(Properties prop)throws Exception{
		String path = prop.getProperty("dest.dir").trim();
		String packageStr = prop.getProperty("dest.package").trim();
		this.sqlTemplatePath = path + "/resources/sqltemplate/";

		
		this.domainPath = path + "/java/";
		for (String str : packageStr.split("\\.")) {
			if (StringUtils.isNotBlank(str)) {
				this.domainPath = this.domainPath + str + "/";
			}
		}
		this.domainPath = this.domainPath + "domain/";
		
		
		this.sqlStringPath = path + "/java/";
		boolean isSpecial = false;
		if(StringUtils.isNotBlank(prop.getProperty("sqlstring.package.special"))){
			packageStr = prop.getProperty("sqlstring.package.special").trim();
			isSpecial = true;
		}else{
			packageStr = prop.getProperty("dest.package").trim();
		}
		for (String str : packageStr.split("\\.")) {
			if (StringUtils.isNotBlank(str)) {
				this.sqlStringPath = this.sqlStringPath + str + "/";
			}
		}
		if(!isSpecial){
			this.sqlStringPath = this.sqlStringPath + "util/";
		}
		
		UtilTools.makeDir(this.sqlTemplatePath);
		UtilTools.makeDir(this.sqlStringPath);
		UtilTools.makeDir(this.domainPath);
	}

}
