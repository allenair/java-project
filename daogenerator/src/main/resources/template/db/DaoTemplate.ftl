package ${package}.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import ${sqlPackage}.SqlString;
import com.sinyd.platform.database.AbstractDao;
import com.sinyd.platform.utiltools.util.DateUtil;

public class ${tableName} implements Serializable{
  <#list fields as item>
  	<#if item.defaultVal??>
  	<#if item.type == "String">
  	private ${item.type} ${item.columnName} = ${item.defaultVal};
  	<#else>
  	private ${item.type} ${item.columnName} = new ${item.type}(${item.defaultVal});
  	</#if>
  	<#else>
  	private ${item.type} ${item.columnName};
  	</#if>
	
  </#list>
  
	private String[] dateColArr = {
	  <#list dateColArr as col>
	  	"${col}"<#if col_has_next>,</#if>
	  </#list>	
	};
	
  <#list fields as item>
	public ${item.type} get${item.method} (){
		return ${item.columnName};
	}
	
	public void set${item.method} ( ${item.type} obj ){
		${item.columnName} = obj;
	}
  </#list>

	
	
	/**
	 * put all columns into a map
	*/
	public void putInMap(Map<String, Object> paramMap){
	  <#list fields as item>
		paramMap.put("${item.columnName}", this.${item.columnName});
	  </#list>
	}
	
	
	/**
	 * return the columns map
	*/
	public Map<String, Object> getInfoMap(){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		this.putInMap(paramMap);
		return paramMap;
	}
	
	/**
	 * return the columns map
	 */
	public Map<String, Object> getInfoDateMap(){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		this.putInMap(paramMap);
		return putDateValue(paramMap);
	}
	
	private Map<String, Object> putDateValue(Map<String, Object> paramMap){
		for (String colName : dateColArr) {
			if(paramMap.get(colName)!=null){
				String val = paramMap.get(colName).toString();
				try{
					if(val.indexOf(":")!=-1){
						paramMap.put(colName, DateUtil.string2Date(val, "yyyy-MM-dd HH:mm:ss"));
					}else{
						paramMap.put(colName, DateUtil.string2Date(val, "yyyy-MM-dd"));
					}
				}catch(Exception e){
					paramMap.put(colName,null);
				}
			}
		}
		return paramMap;
	}
	
	/**
	 * this table insert function 
	*/
	public int insertRecord(AbstractDao dao){
		return dao.updateByTemplate(SqlString.${sqlStringName}_INSERT, putDateValue(this.getInfoMap()));
	}
	
	/**
	 * this table update function 
	*/
	public int updateRecord(AbstractDao dao){
		return dao.updateByTemplate(SqlString.${sqlStringName}_UPDATE, putDateValue(this.getInfoMap()));
	}
	
	/**
	 * return single record by primary key 
	*/
	public static ${tableName} getRecordByPK(AbstractDao dao, ${idType} id){
		${tableName} bean = new ${tableName}();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", id);

		List<Map<String, Object>> resList = dao.queryForListByTemplate(SqlString.${sqlStringName}_LISTBYPK, paramMap);
		if(resList.size()>0){
			Map<String, Object> row = resList.get(0);
		<#list fields as item>
			if(row.get("${item.columnName}")!=null){
			  <#if item.dateType="0">
			  	bean.set${item.method}((${item.type})row.get("${item.columnName}"));
			  </#if>
			  <#if item.dateType="1">
			  	bean.set${item.method}(DateUtil.date2String((java.util.Date)row.get("${item.columnName}"),"yyyy-MM-dd"));
			  </#if>
			  <#if item.dateType="2">
			  	bean.set${item.method}(DateUtil.date2String((java.util.Date)row.get("${item.columnName}"),"yyyy-MM-dd HH:mm:ss"));
			  </#if>
			}
		</#list>
		}
		return bean;
	}
	
	/**
	 * get all records 
	*/
	public static List<${tableName}> getAllRecord(AbstractDao dao){
		List<${tableName}> beanList = new ArrayList<${tableName}>();
		List<Map<String, Object>> resList = dao.queryForListByTemplate(SqlString.${sqlStringName}_LIST, new HashMap<String, Object>());
		for (Map<String, Object> row : resList) {
			${tableName} bean = new ${tableName}();
		  <#list fields as item>
			if(row.get("${item.columnName}")!=null){
			  <#if item.dateType="0">
			  	bean.set${item.method}((${item.type})row.get("${item.columnName}"));
			  </#if>
			  <#if item.dateType="1">
			  	bean.set${item.method}(DateUtil.date2String((java.util.Date)row.get("${item.columnName}"),"yyyy-MM-dd"));
			  </#if>
			  <#if item.dateType="2">
			  	bean.set${item.method}(DateUtil.date2String((java.util.Date)row.get("${item.columnName}"),"yyyy-MM-dd HH:mm:ss"));
			  </#if>
			}
		  </#list>
			beanList.add(bean);
		}
		return beanList;
	}
	
	/**
	 * delete single record by primary key 
	*/
	public static int deleteRecord(AbstractDao dao, ${idType} id){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", id);
		return dao.updateByTemplate(SqlString.${sqlStringName}_DELETE, paramMap);
	}
	
	
}