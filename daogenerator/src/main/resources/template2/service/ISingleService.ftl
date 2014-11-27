package ${ServicePackage};

import java.util.Map;
import java.util.List;

import ${domainPackage}.${tableName};
import ${domainVoPackage}.${tableName}VO;
import ${searchBeanVOPackage}.${tableName}SearchBeanVO;
import com.sinyd.sframe.util.vo.UserBean;

/*
 * @version 2.0
 */

public interface ${ServiceInterfaceName} {
	/**
	 * Description :get combox list by vo queryInfo
	 * @param queryInfo
	 * @return combox list
	 * @author auto_generator
	 */
	public List<Map<String, Object>> getComboxList(${tableName}SearchBeanVO queryInfo);

	/**
	 * Description :get record list records by vo queryInfo withnot paging
	 * @param queryInfo
	 * @return record list
	 * @author auto_generator
	 */
	public Map<String, Object> getListWithoutPaging(${tableName}SearchBeanVO queryInfo);
	
	/**
	 * Description :get record list records by vo queryInfo with paging
	 * @param queryInfo
	 * @return record list
	 * @author auto_generator
	 */
	public Map<String, Object> getListWithPaging(${tableName}SearchBeanVO queryInfo);
	
	<#if pkColArr??>
	/**
	 * Description :get single-table information by primary key 
	 * @param primary key 
	 * @return ${tableName}VO
	 * @author auto_generator
	 */	
	public ${tableName}VO getInfoByPK(<#list pkColArr as col>${col.type} ${col.columnName}<#if col_has_next>, </#if></#list>);	
	</#if>
	
	/**
	 * Description :add method
	 * @param bean
	 * @return "success" or "error" or user defined
	 * @author auto_generator
	 */
	public String save(${tableName} bean, UserBean user);
	
	/**
	 * Description :update method
	 * @param bean contains pk at least
	 * @return "success" or "error" or user defined
	 * @author auto_generator
	 */
	public String update(${tableName} bean, UserBean user);

	/**
	 * Description :delete method
	 * @param pk
	 * @return "success" or "error" or user defined
	 * @author auto_generator
	 */	
	public String delete(${tableName} bean);
}