package ${PersistPackage};

import java.util.List;
import java.util.Map;

import ${domainPackage}.${tableName};
import ${domainVoPackage}.${tableName}VO;
import ${searchBeanVOPackage}.${tableName}SearchBeanVO;

/*
 * @version 2.0
 */

public interface ${PersistInterfaceName} {

	/**
	 * Description :get record list records by vo queryInfo withnot paging
	 * @param queryInfo
	 * @return record list
	 * @author auto_generator
	 */
	public List<Map<String, Object>> getListWithoutPaging(${tableName}SearchBeanVO queryInfo);
	
	/**
	 * Description :get record list records by vo queryInfo with paging
	 * @param queryInfo
	 * @return record list
	 * @author auto_generator
	 */
	public List<Map<String, Object>> getListWithPaging(${tableName}SearchBeanVO queryInfo);
	
	/**
	 * Description :get record list count num by vo queryInfo
	 * @param queryInfo
	 * @return records count num
	 * @author auto_generator
	 */
	public int getListCountNum(${tableName}SearchBeanVO queryInfo);
	
	<#if pkColArr??>
	/**
	 * Description :get single-table information vo by primary key 
	 * @param primary key 
	 * @return ${tableName}VO
	 * @author auto_generator
	 */	
	public ${tableName}VO getInfoByPK(<#list pkColArr as col>${col.type} ${col.columnName}<#if col_has_next>, </#if></#list>);	
	
	/**
	 * Description :get single-table information domain by primary key 
	 * @param primary key 
	 * @return ${tableName}
	 * @author auto_generator
	 */	
	public ${tableName} getDomainByPK(<#list pkColArr as col>${col.type} ${col.columnName}<#if col_has_next>, </#if></#list>);	
	</#if>

	/**
	 * Description :insert a record, nonsupport null val
	 * @param bean
	 * @return success num or 0
	 * @author auto_generator
	 */
	public int addNewRecord(${tableName} bean);

	<#if pkColArr??>
	/**
	 * Description :update a record replace all, need primary key, support null val
	 * @param bean
	 * @return success num or 0
	 * @author auto_generator
	 */
	public int updateRecordAll(${tableName} bean);

	/**
	 * Description :update a record replace columns, need primary key, nonsupport null val
	 * @param bean
	 * @return success num or 0
	 * @author auto_generator
	 */
	public int updateRecordCols(${tableName} bean);

	/**
	 * Description :delete record by primary key
	 * @param pk
	 * @return success num or 0
	 * @author auto_generator
	 */	
	public int deleteRecordByPK(<#list pkColArr as col>${col.type} ${col.columnName}<#if col_has_next>, </#if></#list>);
	<#else>
	/**
	 * Description :
	 * @param 
	 * @return 
	 * @author 
	 */
	public int updateRecord(${tableName} bean);
	</#if>

	/**
	 * Description :delete record by domain bean, nonsupport null val
	 * @param bean
	 * @return success num or 0
	 * @author auto_generator
	 */	
	public int deleteRecordByDomain(${tableName} bean);
	
	/**
	 * Description :get list by "and" method, need new ${tableName}() include query-params
	 * @param queryInfo
	 * @param isExludePKFlag, true is exclude primary key, false is include primary key
	 * @return ${tableName} list
	 * @author auto_generator
	 */
	public List<${tableName}> getSingleTableListByAndMethod(${tableName} queryInfo, Boolean isExcludePKFlag);
	
	/**
	 * Description :get list by "or" method, need new ${tableName}() include query-params
	 * @param queryInfo
	 * @param isExludePKFlag, true is exclude primary key, false is include primary key
	 * @return ${tableName} list
	 * @author auto_generator
	 */
	public List<${tableName}> getSingleTableListByOrMethod(${tableName} queryInfo, Boolean isExcludePKFlag);
}