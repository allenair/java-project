package ${domainPackage};

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import ${sqlPackage}.SqlString;
import ${domainVoPackage}.${tableName}VO;
import com.sinyd.sframe.util.database.AbstractSimpleDao;
import com.sinyd.platform.utiltools.util.DateUtil;

/*
 * @version 2.0
 */

public class ${tableName} implements Serializable {
	private static final long serialVersionUID = 1L;
	
	<#list fields as item>
	private ${item.type} ${item.columnName};
	
	</#list>
	
	/**
	* default val cols name array
	*/	
	private static String[] defaultValColArr = {
		<#if defaultValColArr??>
	    	<#list defaultValColArr as col>
	  	"${col}"<#if col_has_next>,</#if>
	    	</#list>
	    </#if>
	};
	
	/**
	* pk cols name array
	*/	
	private static String[] pkColArr = {
		<#if pkColArr??>
	    	<#list pkColArr as col>
	  	"${col.columnName}"<#if col_has_next>,</#if>
	    	</#list>
	    </#if>
	};
	
	private static String[] columnNameArr = {
		<#if fields??>
			<#list fields as item>
		"${item.columnName}"<#if item_has_next>,</#if>
			</#list>
		</#if>
	};
  
	<#list fields as item>
	public ${item.type} get${item.method} () {
		return ${item.columnName};
	}
	
	public void set${item.method} (${item.type} obj) {
		${item.columnName} = obj;
	}
	
	</#list>
	
	/**
	* put all columns into a map
	*/
	public void putInMap(Map<String, Object> paramMap) {
		<#list fields as item>
		paramMap.put("${item.columnName}", this.${item.columnName});
		</#list>
	}
	
	/**
	* return the columns map
	*/
	public Map<String, Object> getInfoMap() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		this.putInMap(paramMap);
		return paramMap;
	}
	
	/**
	* remove default value and pk if it is null
	*/
	private Map<String, Object> dealWithMap(Map<String, Object> paramMap) {
		Set<String> set = new HashSet<String>();
		for (String colName : defaultValColArr) {
			set.add(colName);
		}
		for (String colName : pkColArr) {
			set.add(colName);
		}
		Iterator<String> iterator = set.iterator();
		while (iterator.hasNext()) {
			String colName = iterator.next();
			if(paramMap.get(colName) == null) {
				paramMap.remove(colName);
			}
		}
		return paramMap;
	}
	
	public Map<String, Object> setSymbolInsert(Map<String, Object> paramMap) {
		paramMap = dealWithMap(paramMap);
		Boolean flag = true;
		for (String ss : columnNameArr) {
			if (flag) {
				if (paramMap.containsKey(ss) && paramMap.get(ss) != null) {
					paramMap.put(ss+"Symbol", " ");
					flag = false;
				}
			} else {
				paramMap.put(ss+"Symbol", ", ");
			}
		}	
		return paramMap;
	}
	
	private Map<String, Object> setSymbolUpdateWithNullValue(Map<String, Object> paramMap) {
		return setSymbolUpdate(dealWithMap(paramMap));
	}
	
	private Map<String, Object> setSymbolUpdateWithoutNullValue(Map<String, Object> paramMap) {
		return setSymbolUpdate(dealWithMapNullVal(paramMap));
	}
	
	public Map<String, Object> setSymbolUpdate(Map<String, Object> paramMap) {
		Boolean flag = true;
		for (String ss : columnNameArr) {
			if (flag) {
				if (paramMap.containsKey(ss) && paramMap.get(ss) != null && !Arrays.asList(pkColArr).contains(ss)) {
					paramMap.put(ss+"Symbol", " ");
					flag = false;
				}
			} else {
				paramMap.put(ss+"Symbol", ", ");
			}
		}	
		return paramMap;
	}
	
	/**
	* remove null
	*/
	private Map<String, Object> dealWithMapNullVal(Map<String, Object> paramMap) {
		<#list fields as item>	
		if(paramMap.get("${item.columnName}") == null) {
			paramMap.remove("${item.columnName}");
		}
		</#list>
		return paramMap;
	}	
	
	/**
	* this table insert function, nonsupport null val 
	*/
	public int insertRecord(AbstractSimpleDao dao) {
		return dao.updateByTemplate(SqlString.${sqlStringName}_INSERT, setSymbolInsert(this.getInfoMap()));
	}
	
	<#if pkColArr??>
	/**
	* delete records by primary key
	*/
	public static int deleteRecordsByPK(AbstractSimpleDao dao, <#list pkColArr as col>${col.type} ${col.columnName}<#if col_has_next>, </#if></#list>) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		<#list pkColArr as col>
		paramMap.put("${col.columnName}", ${col.columnName});
		</#list>
		return dao.updateByTemplate(SqlString.${sqlStringName}_DELETE, paramMap);
	}		
	
	/**
	* this table update row function, need primary key, support null val
	*/
	public int updateRecordAll(AbstractSimpleDao dao) {
		return dao.updateByTemplate(SqlString.${sqlStringName}_UPDATE_ALL, setSymbolUpdateWithNullValue(this.getInfoMap()));
	}
	
	/**
	* this table update column function, need primary key, nonsupport null val
	*/
	public int updateRecordColumn(AbstractSimpleDao dao) {
		return dao.updateByTemplate(SqlString.${sqlStringName}_UPDATE, setSymbolUpdateWithoutNullValue(this.getInfoMap()));
	}
	
	/**
	* return single record domain by primary key 
	*/
	public static ${tableName} getRecordDomainByPK(AbstractSimpleDao dao, <#list pkColArr as col>${col.type} ${col.columnName}<#if col_has_next>, </#if></#list>) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		<#list pkColArr as col>
		paramMap.put("${col.columnName}", ${col.columnName});
		</#list>
		${tableName} bean = dao.qryObj(SqlString.${sqlStringName}_LIST, paramMap, ${tableName}.class);
		return bean;
	}
	
	/**
	* return single record vo by primary key 
	*/
	public static ${tableName}VO getRecordVOByPK(AbstractSimpleDao dao, <#list pkColArr as col>${col.type} ${col.columnName}<#if col_has_next>, </#if></#list>) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		<#list pkColArr as col>
		paramMap.put("${col.columnName}", ${col.columnName});
		</#list>
		List<Map<String, Object>> resList = dao.queryForListByTemplate(SqlString.${sqlStringName}_LIST, paramMap);
		${tableName}VO bean = new ${tableName}VO();
		if(resList.size()>0){
			Map<String, Object> row = resList.get(0);
			<#list fields as item>
			if(row.get("${item.columnName}")!=null) {
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
	</#if>
	
	/**
	* delete records by domain, nonsupport null val
	*/
	public int deleteRecordsByDomain(AbstractSimpleDao dao) {
		return dao.updateByTemplate(SqlString.${sqlStringName}_DELETE, dealWithMapNullVal(this.getInfoMap()));
	}	
	
	/**
	* get list by "and" method, need new ${tableName}() include query-params
	*/
	public static List<${tableName}> getSingleTableListByAndMethod(AbstractSimpleDao dao, ${tableName} queryInfo, Boolean isExcludePKFlag) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		<#list fields as item>
		if(queryInfo.get${item.method}() != null) {
			paramMap.put("${item.columnName}", queryInfo.get${item.method}());
		}
		</#list>
        return dao.qryObjList(SqlString.${sqlStringName}_LIST, setSymbolPKPrior(paramMap, isExcludePKFlag, false), ${tableName}.class);
	}
	
	/**
	* get list by "or" method, need new ${tableName}() include query-params
	*/
	public static List<${tableName}> getSingleTableListByOrMethod(AbstractSimpleDao dao, ${tableName} queryInfo, Boolean isExcludePKFlag) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		<#list fields as item>
		if(queryInfo.get${item.method}() != null) {
			paramMap.put("${item.columnName}", queryInfo.get${item.method}());
		}
		</#list>
        return dao.qryObjList(SqlString.${sqlStringName}_LIST_BY_OR, setSymbolPKPrior(paramMap, isExcludePKFlag, true), ${tableName}.class);
	}
	
	private static Map<String, Object> setSymbolPKPrior(Map<String, Object> paramMap, Boolean exclude_pk, Boolean isOr) {
		if (paramMap == null || paramMap.size() == 0) {
			return paramMap;
		} 
		if (exclude_pk) {
			for (String ss : pkColArr) {
				paramMap.put(ss+"Symbol", "exists");
			}
			paramMap.put("exclude_pk", true);
		}	
		Boolean flag = true;
		if (isOr) {
			paramMap.put("params_exists", true);
			for (String ss : columnNameArr) {
				if (flag) {
					if (paramMap.containsKey(ss) && !paramMap.containsKey(ss+"Symbol")) {
						paramMap.put(ss+"Symbol", " ");
						flag = false;
					}
				} else {
					paramMap.put(ss+"Symbol", " or ");
				}
			}
		}
		return paramMap;
	}
}