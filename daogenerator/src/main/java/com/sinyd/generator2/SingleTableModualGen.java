package com.sinyd.generator2;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.sinyd.generator2.UtilTools;

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
	private String searchBeanVoPath;
	
	private List<String> colList = new ArrayList<String>();
	private List<String> pkList = new ArrayList<String>();
	private Map<String,String> typeMap = new HashMap<String, String>();
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		new SingleTableModualGen().gen();
		new TemplateToSqlString().gen();
	}

	public void gen() throws Exception{
		initPath();
		Connection conn = UtilTools.getConn();
		Configuration cfg = UtilTools.getFreeMarkerCfg();
		Template controllerTemplate = cfg.getTemplate("template2/web/SingleController.ftl");
		Template serviceTemplate = cfg.getTemplate("template2/service/ISingleService.ftl");
		Template serviceImplTemplate = cfg.getTemplate("template2/service/SingleServiceImpl.ftl");
		Template persistTemplate = cfg.getTemplate("template2/persist/ISingleDao.ftl");
		Template persistImplTemplate = cfg.getTemplate("template2/persist/SingleDaoImpl.ftl");
		Template searchBeanVoTemplate = cfg.getTemplate("template2/vo/SingleSearchBeanVO.ftl");
		Template sqlTemplate = cfg.getTemplate("template2/db/SingleSqlTemplate.ftl");
		
		Map<String, Object> docMap;
		List<Map<String, String>> sqlList = new ArrayList<Map<String,String>>();
		String modualPackageStr = UtilTools.modualPackageStr;
		String domainPackage = UtilTools.domainPackageStr;
		String domainVoPath  = UtilTools.domainVoPackageStr;
		if(StringUtils.isNotBlank(UtilTools.getProVal("single.table.modual.set"))){
			String[] modualArr = UtilTools.getProVal("single.table.modual.set").trim().split("#");
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
						sqlMap.put("content_u", getSqlListTemplateForUpdate(tableName));
						sqlList.add(sqlMap);
						
						docMap = new HashMap<String, Object>();
						docMap.put("ControllerPackage", modualPackageStr+".web");
						docMap.put("ServicePackage", modualPackageStr+".service");
						docMap.put("ServiceImplPackage", modualPackageStr+".service.impl");
						docMap.put("PersistPackage", modualPackageStr+".persist");
						docMap.put("PersistImplPackage", modualPackageStr+".persist.impl");
						docMap.put("searchBeanVOPackage", modualPackageStr+".vo");
						docMap.put("domainPackage", domainPackage);
						docMap.put("domainVoPackage", domainVoPath);
						docMap.put("databasePackageStr", UtilTools.databasePackageStr);
						docMap.put("sqlPackage", UtilTools.sqlPackageStr);
						
						docMap.put("ControllerClassName", singleModualName+"Controller");
						docMap.put("ServiceInterfaceName", "I"+singleModualName+"Service");
						docMap.put("ServiceClassName", singleModualName+"ServiceImpl");
						docMap.put("PersistInterfaceName", "I"+singleModualName+"Dao");
						docMap.put("PersistClassName", singleModualName+"DaoImpl");
						docMap.put("searchBeanVoPath", singleModualName+"SearchBeanVO");
						docMap.put("tableName", singleModualName);
						
						docMap.put("ServicePropertyName", UtilTools.format(singleModualName,false)+"Service");
						docMap.put("PersistPropertyName", UtilTools.format(singleModualName,false)+"Dao");
						
						docMap.put("modualPath", modualPath);
						docMap.put("tableLittleName", singleModualName.toLowerCase());
						docMap.put("singleListSql", "AUTOSINGLE2_"+tableName.toUpperCase()+"_LIST");
						
						fillMapFromDb(docMap, conn, tableName);
						
						
						output(docMap, this.controllerPath + docMap.get("ControllerClassName") + ".java", controllerTemplate);
						output(docMap, this.servicePath + docMap.get("ServiceInterfaceName") + ".java", serviceTemplate);
						output(docMap, this.serviceImplPath + docMap.get("ServiceClassName") + ".java", serviceImplTemplate);
						output(docMap, this.persistPath + docMap.get("PersistInterfaceName") + ".java", persistTemplate);
						output(docMap, this.persistImplPath + docMap.get("PersistClassName") + ".java", persistImplTemplate);
						output(docMap, this.searchBeanVoPath + docMap.get("searchBeanVoPath") + ".java", searchBeanVoTemplate);
						//UtilTools.makeDir(this.htmlPath + modualPath + "/");
						//output(docMap, this.htmlPath + modualPath + "/" + tableName + ".html", htmlTemplate);
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
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ").append(tableName);
		ResultSet rs = st.executeQuery(UtilTools.getOneRecordSql(sql.toString()));
		pkList = UtilTools.getTablePkMap(tableName, conn);
		
		this.colList = new ArrayList<String>();
		this.typeMap = new HashMap<String, String>();
		ResultSetMetaData meta = rs.getMetaData();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			if(meta.getColumnClassName(i).equals("[B")){
				continue;
			}
			colList.add(meta.getColumnName(i));
			typeMap.put(meta.getColumnName(i), meta.getColumnClassName(i));
		}
		
		rs.close();
		st.close();
	}
	
	private void fillMapFromDb(Map<String, Object> docMap, Connection conn, String tableName) throws Exception{
		List<String> columnNameList = new ArrayList<String>();
		List<Map<String, String>> pkColArr = new ArrayList<Map<String, String>>();
		for (String colName : this.colList) {
			if(UtilTools.isPkColumn(pkList,colName)){
				String type = UtilTools.jdbcType2Java(typeMap.get(colName));
				Map<String, String> pkColArrMap = new HashMap<String, String>();
				pkColArrMap.put("columnName", colName.toLowerCase());
				pkColArrMap.put("method", colName.substring(0, 1).toUpperCase() + colName.toLowerCase().substring(1));
				pkColArrMap.put("type", type);
				pkColArr.add(pkColArrMap);
			}
			columnNameList.add(colName);
		}
		docMap.put("columnNameList", columnNameList);
		if(pkColArr.size()>0){
			docMap.put("pkColArr", pkColArr);//主键列表
		}
	}
	
	private String getSqlListTemplate(String tableName){
		StringBuilder colStr = new StringBuilder();
		for (int i = 0; i < this.colList.size(); i++) {
			String colName = colList.get(i);
			String type = this.typeMap.get(colName);
			
			colStr.append("\t\t\t  ");
			
			if("java.sql.Date".equals(type)){
				colStr.append(UtilTools.getDateToStringFun(colName, type)).append(" as ").append(colName);
			}else if("java.sql.Timestamp".equals(this.typeMap.get(colName))){
				colStr.append(UtilTools.getDateToStringFun(colName, type)).append(" as ").append(colName);
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
		sql.append(colStr.toString().toLowerCase()).append(this.LINE_SEP);
		sql.append("\t\t  from  ").append(tableName).append(this.LINE_SEP);
		sql.append("\t\t  where 1=1").append(this.LINE_SEP);
		sql.append("\t\t  <#if searchName??> ").append(this.LINE_SEP);
		sql.append("\t\t      /* and column_name = :searchName */ ").append(this.LINE_SEP);
		sql.append("\t\t  </#if> ").append(this.LINE_SEP);
		sql.append("\t\t  <#if sortname??> ").append(this.LINE_SEP);
		sql.append("\t\t  order by ${sortname} ${sortorder} ").append(this.LINE_SEP);
		sql.append("\t\t  </#if> ").append(this.LINE_SEP);
		return sql.toString();
	}
	
	private String getSqlListTemplateForUpdate(String tableName){
		StringBuilder colStr = new StringBuilder();
		for (int i = 0; i < this.colList.size(); i++) {
			String colName = colList.get(i);
			
			colStr.append("\t\t  <#if ").append(colName).append("??>").append(this.LINE_SEP);
			colStr.append("\t\t\t  ").append(colName).append(" = :").append(colName);
			if(i<this.colList.size()-1){
				colStr.append(",");
			}
			colStr.append(this.LINE_SEP);
			colStr.append("\t\t  <#else>").append(this.LINE_SEP);
			colStr.append("\t\t\t  ").append(colName).append(" = null");
			if(i<this.colList.size()-1){
				colStr.append(",");
			}
			colStr.append(this.LINE_SEP);
			colStr.append("\t\t  </#if>").append(this.LINE_SEP);
		}
		
		
		StringBuilder sql = new StringBuilder();
		sql.append("\t\t  update").append(this.LINE_SEP);
		sql.append("\t\t\t  ").append(tableName).append(this.LINE_SEP);
		sql.append("\t\t  set").append(this.LINE_SEP);
		sql.append(colStr.toString().toLowerCase()).append(this.LINE_SEP);
		sql.append("\t\t  where 1=1").append(this.LINE_SEP);
		sql.append("\t\t  <#if pkName??>").append(this.LINE_SEP);
		sql.append("\t\t\t  and pkName = :pkName").append(this.LINE_SEP);
		sql.append("\t\t  </#if>").append(this.LINE_SEP);
		return sql.toString();
	}
	
	private void output(Map<String, Object> docMap, String fileName, Template template) throws Exception {
		FileWriter fileWriter = new FileWriter(fileName);
		template.process(docMap, fileWriter);
		fileWriter.flush();
		fileWriter.close();
	}
	
	private void initPath()throws Exception{
		String basePath = UtilTools.path;
		String packageStr = UtilTools.modualPackageStr;

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
		
		this.sqlPath = basePath + "/resources/sqltemplate/"+UtilTools.getProVal("modual.package")+"/";
		this.htmlPath = basePath + "/webapp/resources/html/";
		this.searchBeanVoPath = path + "vo/";
		
		if(UtilTools.deleteDirFlag){
			UtilTools.deleteDir();
			UtilTools.deleteDirFlag = false;
		}
		
		UtilTools.makeDir(this.controllerPath);
		UtilTools.makeDir(searchBeanVoPath);
		UtilTools.makeDir(this.servicePath);
		UtilTools.makeDir(this.serviceImplPath);
		UtilTools.makeDir(this.persistPath);
		UtilTools.makeDir(this.persistImplPath);
		UtilTools.makeDir(this.sqlPath);
		UtilTools.makeDir(this.htmlPath);
	}
	
}
