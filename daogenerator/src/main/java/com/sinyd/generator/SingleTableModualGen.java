package com.sinyd.generator;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class SingleTableModualGen {
	private final String LINE_SEP = System.getProperty("line.separator");
	private String controllerPath="";
	private String servicePath="";
	private String serviceImplPath="";
	private String persistPath="";
	private String persistImplPath="";
	private String htmlPath="";
	private String sqlPath="";
	private String sqlStringStr="";
	
	private List<String> colList = new ArrayList<String>();
	private Map<String,String> typeMap = new HashMap<String, String>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		new SingleTableModualGen().gen();
	}

	public void gen() throws Exception{
		Properties prop = UtilTools.getProperty();
		initPath(prop);
		Connection conn = UtilTools.getConn(prop);
		
		Configuration cfg = UtilTools.getFreeMarkerCfg(prop);
		Template controllerTemplate = cfg.getTemplate("template/web/SingleController.ftl");
		Template serviceTemplate = cfg.getTemplate("template/service/ISingleService.ftl");
		Template serviceImplTemplate = cfg.getTemplate("template/service/SingleServiceImpl.ftl");
		Template persistTemplate = cfg.getTemplate("template/persist/ISingleDao.ftl");
		Template persistImplTemplate = cfg.getTemplate("template/persist/SingleDaoImpl.ftl");
		Template htmlTemplate = cfg.getTemplate("template/html/SingleHtml.ftl");
		Template sqlTemplate = cfg.getTemplate("template/db/SingleSqlTemplate.ftl");
		
		Map<String, Object> docMap;
		List<Map<String, String>> sqlList = new ArrayList<Map<String,String>>();
		String packageStr = prop.getProperty("dest.package").trim();
		
		if(StringUtils.isNotBlank(prop.getProperty("single.table.modual.set"))){
			String[] modualArr = prop.getProperty("single.table.modual.set").trim().split("#");
			for (String modualSet : modualArr) {
				if(StringUtils.isNotBlank(modualSet)){
					String[] setArr = modualSet.split("@");
					String tableName = setArr[0];
					String modualPath = setArr[1];
					if(StringUtils.isNotBlank(tableName) && StringUtils.isNotBlank(modualPath)){
						tableName = tableName.toLowerCase();
						modualPath = modualPath.toLowerCase();
						String singleModualName = UtilTools.format(tableName, true);
						fillTableColumnInfo(conn, tableName);
						
						Map<String, String> sqlMap = new HashMap<String, String>();
						sqlMap.put("tableName", tableName);
						sqlMap.put("content", getSqlListTemplate(tableName));
						sqlList.add(sqlMap);
						
						docMap = new HashMap<String, Object>();
						docMap.put("package", packageStr);
						docMap.put("ControllerPackage", packageStr+".web");
						docMap.put("ServicePackage", packageStr+".service");
						docMap.put("ServiceImplPackage", packageStr+".service.impl");
						docMap.put("PersistPackage", packageStr+".persist");
						docMap.put("PersistImplPackage", packageStr+".persist.impl");
						docMap.put("DomainPackage", packageStr+".domain");
						
						if(StringUtils.isNotBlank(prop.getProperty("searchbeanvo.package.special"))){
							docMap.put("VoPackage", prop.getProperty("searchbeanvo.package.special").trim());
						}else{
							docMap.put("VoPackage", packageStr+".vo");
						}
						
						if(StringUtils.isNotBlank(prop.getProperty("sqlstring.package.special"))){
							docMap.put("sqlPackage", prop.getProperty("sqlstring.package.special").trim());
						}else{
							docMap.put("sqlPackage", packageStr+".util");
						}
						
						docMap.put("ControllerClassName", singleModualName+"Controller");
						docMap.put("ServiceInterfaceName", "I"+singleModualName+"Service");
						docMap.put("ServiceClassName", singleModualName+"ServiceImpl");
						docMap.put("PersistInterfaceName", "I"+singleModualName+"Dao");
						docMap.put("PersistClassName", singleModualName+"DaoImpl");
						docMap.put("TableClassName", singleModualName);
						
						docMap.put("ServicePropertyName", UtilTools.format(singleModualName,false)+"Service");
						docMap.put("PersistPropertyName", UtilTools.format(singleModualName,false)+"Dao");
						
						docMap.put("modualPath", modualPath);
						docMap.put("tableLittleName", singleModualName.toLowerCase());
						docMap.put("singleListSql", "AUTOSINGLE_"+tableName.toUpperCase()+"_LIST");
						
						fillMapFromDb(docMap, conn, tableName);
						
						
						output(docMap, this.controllerPath + docMap.get("ControllerClassName") + ".java", controllerTemplate);
						output(docMap, this.servicePath + docMap.get("ServiceInterfaceName") + ".java", serviceTemplate);
						output(docMap, this.serviceImplPath + docMap.get("ServiceClassName") + ".java", serviceImplTemplate);
						output(docMap, this.persistPath + docMap.get("PersistInterfaceName") + ".java", persistTemplate);
						output(docMap, this.persistImplPath + docMap.get("PersistClassName") + ".java", persistImplTemplate);
						UtilTools.makeDir(this.htmlPath + modualPath + "/");
						output(docMap, this.htmlPath + modualPath + "/" + tableName + ".html", htmlTemplate);
					}
				}
			}
		}
		
		if(sqlList.size()>0){
			docMap = new HashMap<String, Object>();
			docMap.put("sqlList", sqlList);
			output(docMap, this.sqlPath + "auto_single_list.xml", sqlTemplate);
		}
		
		System.out.println(StringUtils.center("ALL FrameWork are Finished!!!", 40, "#"));
		conn.close();
	}
	
	private void fillTableColumnInfo(Connection conn, String tableName) throws Exception{
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery("select * from "+tableName+"  limit 0 ");
		
		this.colList = new ArrayList<String>();
		this.typeMap = new HashMap<String, String>();
		ResultSetMetaData meta = rs.getMetaData();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			colList.add(meta.getColumnName(i).toLowerCase());
			typeMap.put(meta.getColumnName(i).toLowerCase(), meta.getColumnClassName(i));
		}
		
		rs.close();
		st.close();
	}
	
	private void fillMapFromDb(Map<String, Object> docMap, Connection conn, String tableName) throws Exception{
		List<String> columnNameList = new ArrayList<String>();
		for (String colName : this.colList) {
			if("id".equals(colName)){
				docMap.put("idType", UtilTools.jdbcType2Java(this.typeMap.get(colName)));
			}else{
				columnNameList.add(colName);
			}
		}
		docMap.put("columnNameList", columnNameList);
	}
	
	private String getSqlListTemplate(String tableName){
		StringBuilder colStr = new StringBuilder();
		for (int i = 0; i < this.colList.size(); i++) {
			String colName = colList.get(i);
			String type = this.typeMap.get(colName);
			
			colStr.append("\t\t\t  ");
			if("java.sql.Date".equals(type)){
				colStr.append("to_char(").append(colName).append(", 'YYYY-MM-DD') as ").append(colName);
			}else if("java.sql.Timestamp".equals(this.typeMap.get(colName))){
				colStr.append("to_char(").append(colName).append(", 'YYYY-MM-DD HH24:MI:SS') as ").append(colName);
			}else{
				colStr.append(colName);
			}
			
			if(i<this.colList.size()-1){
				colStr.append(",");
			}
			colStr.append(this.LINE_SEP);
		}
		
		
		StringBuilder sql = new StringBuilder();
		sql.append("\t\t  select  ").append(this.LINE_SEP);
		sql.append(colStr.toString()).append(this.LINE_SEP);
		sql.append("\t\t   from  ").append(tableName).append(this.LINE_SEP);
		sql.append("\t\t   where 1=1").append(this.LINE_SEP);
		sql.append("\t\t   <#if searchName??> ").append(this.LINE_SEP);
		sql.append("\t\t       /* column_name like :searchName */ ").append(this.LINE_SEP);
		sql.append("\t\t   </#if> ").append(this.LINE_SEP);
		sql.append("\t\t   <#if sortname??> ").append(this.LINE_SEP);
		sql.append("\t\t      order by ${sortname} ${sortorder} ").append(this.LINE_SEP);
		sql.append("\t\t   </#if> ").append(this.LINE_SEP);
		return sql.toString();
	}
	
	private void output(Map<String, Object> docMap, String fileName, Template template) throws Exception {
		FileWriter fileWriter = new FileWriter(fileName);
		template.process(docMap, fileWriter);
		fileWriter.flush();
		fileWriter.close();
	}
	
	private void initPath(Properties prop)throws Exception{
		String basePath = prop.getProperty("dest.dir").trim();
		String packageStr = prop.getProperty("dest.package").trim();

		String path = basePath + "/java/";
		for (String str : packageStr.split("\\.")) {
			if (StringUtils.isNotBlank(str)) {
				path = path + str + "/";
			}
		}
		
		this.controllerPath = path + "web/";
		this.servicePath = path + "service/";
		this.serviceImplPath = path + "service/impl/";
		this.persistPath = path + "persist/";
		this.persistImplPath = path + "persist/impl/";
		
		this.sqlPath = basePath + "/resources/sqltemplate/";
		this.htmlPath = basePath + "/webapp/resources/html/";
		
		UtilTools.makeDir(this.controllerPath);
		UtilTools.makeDir(this.servicePath);
		UtilTools.makeDir(this.serviceImplPath);
		UtilTools.makeDir(this.persistPath);
		UtilTools.makeDir(this.persistImplPath);
		UtilTools.makeDir(this.sqlPath);
		UtilTools.makeDir(this.htmlPath);
		
		if(StringUtils.isNotBlank(prop.getProperty("sqlstring.package.special"))){
			this.sqlStringStr = prop.getProperty("sqlstring.package.special").trim();
			for (String str : this.sqlStringStr.split("\\.")) {
				if (StringUtils.isNotBlank(str)) {
					path = path + str + "/";
				}
			}
			UtilTools.makeDir(this.sqlStringStr);
		}
	}
	
}
