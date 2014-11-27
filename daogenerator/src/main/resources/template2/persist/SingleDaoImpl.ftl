package ${PersistImplPackage};

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import ${domainPackage}.${tableName};
import ${domainVoPackage}.${tableName}VO;
import ${searchBeanVOPackage}.${tableName}SearchBeanVO;
import ${PersistPackage}.${PersistInterfaceName};
import ${sqlPackage}.SqlString;
import com.sinyd.sframe.util.database.AbstractSimpleDao;
import com.sinyd.platform.utiltools.util.StringUtil;

/*
 * @version 2.0
 */

@Repository("${PersistPropertyName}")
public class ${PersistClassName} extends AbstractSimpleDao implements ${PersistInterfaceName} {

	@Override
	public List<Map<String, Object>> getListWithoutPaging(${tableName}SearchBeanVO queryInfo) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		/*
		if(StringUtil.isNotBlank(queryInfo.getSearchName())){
            paramMap.put("searchName", SysTools.getSqlLikeParam(queryInfo.getSearchName()));
        }
        */
        paramMap.put("sortname", queryInfo.getSortname());
		paramMap.put("sortorder", queryInfo.getSortorder());
        return this.queryForListByTemplate(SqlString.${singleListSql}, paramMap);
	}

	@Override	
	public List<Map<String, Object>> getListWithPaging(${tableName}SearchBeanVO queryInfo) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		/*
		if(StringUtil.isNotBlank(queryInfo.getSearchName())){
            paramMap.put("searchName", SysTools.getSqlLikeParam(queryInfo.getSearchName()));
        }
        */
        paramMap.put("sortname", queryInfo.getSortname());
		paramMap.put("sortorder", queryInfo.getSortorder());
		return this.pagingForListByTemplate(SqlString.${singleListSql}, queryInfo.getPagesize(), queryInfo.getPage(),paramMap);		
	}

	@Override	
	public int getListCountNum(${tableName}SearchBeanVO queryInfo) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		/*if(StringUtil.isNotBlank(queryInfo.getSearchName())){
            paramMap.put("searchName", "%"+queryInfo.getSearchName()+"%");
        }*/
        return this.recordNumberForListByTemplate(SqlString.${singleListSql}, paramMap);
	}

	<#if pkColArr??>
	@Override
	public ${tableName}VO getInfoByPK(<#list pkColArr as col>${col.type} ${col.columnName}<#if col_has_next>, </#if></#list>) {
		return ${tableName}.getRecordVOByPK(this, <#list pkColArr as col>${col.columnName}<#if col_has_next>, </#if></#list>);
	}
	
	@Override
	public ${tableName} getDomainByPK(<#list pkColArr as col>${col.type} ${col.columnName}<#if col_has_next>, </#if></#list>) {
		return ${tableName}.getRecordDomainByPK(this, <#list pkColArr as col>${col.columnName}<#if col_has_next>, </#if></#list>);
	}
	</#if>	

	@Override	
	public int addNewRecord(${tableName} bean) {
		return bean.insertRecord(this);
	}
	
	<#if pkColArr??>
	@Override
	public int updateRecordAll(${tableName} bean) {
		return bean.updateRecordAll(this);
	}

	@Override	
	public int updateRecordCols(${tableName} bean) {
		return bean.updateRecordColumn(this);
	}

	@Override
	public int deleteRecordByPK(<#list pkColArr as col>${col.type} ${col.columnName}<#if col_has_next>, </#if></#list>) {
		return ${tableName}.deleteRecordsByPK(this, <#list pkColArr as col>${col.columnName}<#if col_has_next>, </#if></#list>);
	}
	<#else>
	@Override
	public int updateRecord(${tableName} bean) {
		// this table no-pk, compose code yourself
		return 0;
	}
	</#if>

	@Override	
	public int deleteRecordByDomain(${tableName} bean) {
		return bean.deleteRecordsByDomain(this);
	}

	@Override	
	public List<${tableName}> getSingleTableListByAndMethod(${tableName} queryInfo, Boolean isExcludePKFlag) {
        return ${tableName}.getSingleTableListByAndMethod(this, queryInfo, isExcludePKFlag);
	}

	@Override	
	public List<${tableName}> getSingleTableListByOrMethod(${tableName} queryInfo, Boolean isExcludePKFlag) {
        return ${tableName}.getSingleTableListByOrMethod(this, queryInfo, isExcludePKFlag);
	}
}