package com.sinyd.generator;

import java.io.File;
import java.io.FileWriter;
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
 * 根据指定的sql模板的目录，遍历其下所有的sql模板文件，自动生成SqlString常量文件，
 * 其中常量的命名方式为：sql模板中的命名空间+模板中key名字，最终结果将其中的@和#都以_替代，
 * 生成中最终会检查SqlString文件中是否有重名的常量，并把所有重名的常量列出，以供修改
 * */
public class TemplateToSqlString {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new TemplateToSqlString().gen();
	}

	public void gen() throws Exception {
		Properties prop = UtilTools.getProperty();
		
		String path = prop.getProperty("dest.dir").trim();
		String packageStr = "";
		String sqlStringPath = path + "/java/";
		boolean isSpecial = false;
		if(StringUtils.isNotBlank(prop.getProperty("sqlstring.package.special"))){
			packageStr = prop.getProperty("sqlstring.package.special").trim();
			isSpecial = true;
		}else{
			packageStr = prop.getProperty("dest.package").trim();
		}
		for (String str : packageStr.split("\\.")) {
			if (StringUtils.isNotBlank(str)) {
				sqlStringPath = sqlStringPath + str + "/";
			}
		}
		if(!isSpecial){
			sqlStringPath = sqlStringPath + "util/";
		}
		
		UtilTools.makeDir(sqlStringPath);
		
		Configuration cfg = UtilTools.getFreeMarkerCfg(prop);
		
		// generate SqlString.java 
		Map<String, Object> docMap = new HashMap<String, Object>();
		Template template = cfg.getTemplate("template/db/SqlString.ftl");

		path = prop.getProperty("sql.other.catalog");
		List<Map<String, String>> sqlList = new ArrayList<Map<String,String>>();
		iteratorCatalog(new File(path), sqlList);
		String repeatStr = checkRepeat(sqlList);
		if(StringUtils.isNotBlank(repeatStr)){
			System.out.println("=======ERROR!!=====There are some keys repeated!!");
			System.out.println(repeatStr);
			return;
		}
		
		if(StringUtils.isNotBlank(prop.getProperty("sqlstring.package.special"))){
			docMap.put("sqlPackage", prop.getProperty("sqlstring.package.special").trim());
		}else{
			docMap.put("sqlPackage", prop.getProperty("dest.package")+".util");
		}
		
		docMap.put("sqlList", sqlList);
		output(docMap, sqlStringPath + "SqlString.java", template);
		
		System.out.println(StringUtils.center("ALL Task are Finished!!!", 40, "#"));
	}
	
	private void iteratorCatalog(File file, List<Map<String, String>> sqlList) throws Exception {
		if (file.isDirectory()) {
			for(File childFile: file.listFiles()){
				iteratorCatalog(childFile, sqlList);
			}
		}else{
			UtilTools.getSqlFromXml(file, sqlList);
		}
	}
	
	private void output(Map<String, Object> docMap, String fileName, Template template) throws Exception {
		FileWriter fileWriter = new FileWriter(fileName);
		template.process(docMap, fileWriter);
		fileWriter.flush();
		fileWriter.close();
	}
	
	private String checkRepeat(List<Map<String, String>> sqlList){
		Set<String> repSet = new HashSet<String>();
		String repeatStr="",valName="";
		for (Map<String, String> sqlMap : sqlList) {
			valName = sqlMap.get("valName");
			if(repSet.contains(valName)){
				repeatStr=repeatStr+"["+valName+"] ";
			}else{
				repSet.add(valName);
			}
		}
		return repeatStr;
	}
}
