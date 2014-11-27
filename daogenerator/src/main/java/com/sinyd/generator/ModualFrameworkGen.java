package com.sinyd.generator;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 根据framework.modual.name中设定的模块名字生成各层模板，模块之间使用#分隔
 * */
public class ModualFrameworkGen {
	private String controllerPath="";
	private String servicePath="";
	private String serviceImplPath="";
	private String persistPath="";
	private String persistImplPath="";
	private String pomPath="";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new ModualFrameworkGen().gen();
	}

	public void gen() throws Exception{
		Properties prop = UtilTools.getProperty();
		initPath(prop);
		
		Configuration cfg = UtilTools.getFreeMarkerCfg(prop);
		Template controllerTemplate = cfg.getTemplate("template/web/ModualController.ftl");
		Template serviceTemplate = cfg.getTemplate("template/service/IModualService.ftl");
		Template serviceImplTemplate = cfg.getTemplate("template/service/ModualServiceImpl.ftl");
		Template persistTemplate = cfg.getTemplate("template/persist/IModualDao.ftl");
		Template persistImplTemplate = cfg.getTemplate("template/persist/ModualDaoImpl.ftl");
		Template pomTemplate = cfg.getTemplate("template/pom.ftl");
		
		Map<String, String> docMap;
		String packageStr = prop.getProperty("dest.package").trim();
		
		if(StringUtils.isNotBlank(prop.getProperty("framework.modual.name"))){
			String[] modualArr = prop.getProperty("framework.modual.name").trim().split("#");
			for (String modualName : modualArr) {
				if(StringUtils.isNotBlank(modualName)){
					modualName = modualName.trim();
					docMap = new HashMap<String, String>();
					
					docMap.put("ControllerPackage", packageStr+".web");
					docMap.put("ServicePackage", packageStr+".service");
					docMap.put("ServiceImplPackage", packageStr+".service.impl");
					docMap.put("PersistPackage", packageStr+".persist");
					docMap.put("PersistImplPackage", packageStr+".persist.impl");
					
					docMap.put("ControllerClassName", modualName+"Controller");
					docMap.put("ServiceInterfaceName", "I"+modualName+"Service");
					docMap.put("ServiceClassName", modualName+"ServiceImpl");
					docMap.put("PersistInterfaceName", "I"+modualName+"Dao");
					docMap.put("PersistClassName", modualName+"DaoImpl");
					
					docMap.put("ServicePropertyName", UtilTools.format(modualName,false)+"Service");
					docMap.put("PersistPropertyName", UtilTools.format(modualName,false)+"Dao");
					
					output(docMap, this.controllerPath + docMap.get("ControllerClassName") + ".java", controllerTemplate);
					output(docMap, this.servicePath + docMap.get("ServiceInterfaceName") + ".java", serviceTemplate);
					output(docMap, this.serviceImplPath + docMap.get("ServiceClassName") + ".java", serviceImplTemplate);
					output(docMap, this.persistPath + docMap.get("PersistInterfaceName") + ".java", persistTemplate);
					output(docMap, this.persistImplPath + docMap.get("PersistClassName") + ".java", persistImplTemplate);
				}
			}
		}
		
		docMap = new HashMap<String, String>();
		String[] pkgArr = packageStr.split("\\.");
		String appName = pkgArr[pkgArr.length-1];
		docMap.put("appName", appName);
		output(docMap, this.pomPath + "pom.xml", pomTemplate);
		
		System.out.println(StringUtils.center("ALL FrameWork are Finished!!!", 40, "#"));
	}
	
	private void output(Map<String, String> docMap, String fileName, Template template) throws Exception {
		FileWriter fileWriter = new FileWriter(fileName);
		template.process(docMap, fileWriter);
		fileWriter.flush();
		fileWriter.close();
	}
	
	private void initPath(Properties prop)throws Exception{
		String path = prop.getProperty("dest.dir").trim();
		String packageStr = prop.getProperty("dest.package").trim();

		this.pomPath = path+"/";
		path = path + "/java/";
		
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
		
		UtilTools.makeDir(this.pomPath);
		UtilTools.makeDir(this.controllerPath);
		UtilTools.makeDir(this.servicePath);
		UtilTools.makeDir(this.serviceImplPath);
		UtilTools.makeDir(this.persistPath);
		UtilTools.makeDir(this.persistImplPath);
	}
}
