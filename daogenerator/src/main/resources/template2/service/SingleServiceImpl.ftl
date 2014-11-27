package ${ServiceImplPackage};

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ${domainPackage}.${tableName};
import ${PersistPackage}.${PersistInterfaceName};
import ${ServicePackage}.${ServiceInterfaceName};
import ${domainVoPackage}.${tableName}VO;
import ${searchBeanVOPackage}.${tableName}SearchBeanVO;
import com.sinyd.platform.uivo.vo.GridDataBean;
import com.sinyd.sframe.util.vo.UserBean;

/*
 * @version 2.0
 */

@Service("${ServicePropertyName}")
public class ${ServiceClassName} implements ${ServiceInterfaceName} {
	private static Logger log = LoggerFactory.getLogger(${ServiceClassName}.class);

	@Autowired
	private ${PersistInterfaceName} ${PersistPropertyName};
	
	@Override
	public List<Map<String, Object>> getComboxList(${tableName}SearchBeanVO queryInfo) {
		return ${PersistPropertyName}.getListWithoutPaging(queryInfo);		
	}

	@Override
	public Map<String, Object> getListWithoutPaging(${tableName}SearchBeanVO queryInfo) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put("Rows", ${PersistPropertyName}.getListWithoutPaging(queryInfo));
		return resMap;		
	}

	@Override
	public Map<String, Object> getListWithPaging(${tableName}SearchBeanVO queryInfo) {
		GridDataBean bean = new GridDataBean(queryInfo.getPage(),
				${PersistPropertyName}.getListCountNum(queryInfo),
				${PersistPropertyName}.getListWithPaging(queryInfo));
		return bean.getGridData();			
	}

	<#if pkColArr??>
	@Override
	public ${tableName}VO getInfoByPK(<#list pkColArr as col>${col.type} ${col.columnName}<#if col_has_next>, </#if></#list>) {
		return ${PersistPropertyName}.getInfoByPK(<#list pkColArr as col>${col.columnName}<#if col_has_next>, </#if></#list>);
	}
	</#if>

	@Override	
	@Transactional
	public String save(${tableName} bean, UserBean user) {
		String resStr = "success";
		int ret = 0;
		ret = ${PersistPropertyName}.addNewRecord(bean);
		if (ret == 0) {
			resStr = "error";
		}
		return resStr;
	}

	@Override
	@Transactional
	public String update(${tableName} bean, UserBean user) {
		String resStr = "success";
		int ret = 0;
		<#if pkColArr??>
		ret = ${PersistPropertyName}.updateRecordAll(bean); // update a record replace all, support null val
		// ret = ${PersistPropertyName}.updateRecordCols(bean); // update a record replace columns, nonsupport null val
		<#else>
		ret = ${PersistPropertyName}.updateRecord(bean); // this table no-pk, compose code yourself in dao
		</#if>
		if (ret == 0) {
			resStr = "error";
		}
		return resStr;
	}

	@Override
	@Transactional	
	public String delete(${tableName} bean) {
		String resStr = "success";
		int ret = 0;
		try {
			ret = ${PersistPropertyName}.deleteRecordByDomain(bean); // nonsupport null val
		} catch (Exception e) {
			log.error(e.getMessage());
			resStr = "fkerror"; // default foreign key error
		}
		if (ret == 0) {
			resStr = "error";
		}
		return resStr;
	}
	
	/**
	 * Description :check repeat by "and" method, union checking, need new ${tableName}() include check-params
	 * @param queryInfo
	 * @param isExludePKFlag, true is exclude primary key, false is include primary key
	 * @return "success" or "repeat"
	 * @author auto_generator
	 */
	private String checkParamsRepeatByAndMethod(${tableName} queryInfo, Boolean isExcludePKFlag) {
		<#if pkColArr??>
		if (isExcludePKFlag) {
			Boolean setAllPk = true;
			<#list pkColArr as col>
			if (queryInfo.get${col.method}() == null) {
				setAllPk = false;
			}
			</#list>
			if (!setAllPk) {
				log.info("**************please set pk into the queryInfo.**************");
				return "error";
			}
		}
		</#if>
		String resStr = "success";
		List<${tableName}> beanList = ${PersistPropertyName}.getSingleTableListByAndMethod(queryInfo, isExcludePKFlag);
		if(beanList != null && beanList.size() > 0) {
			resStr = "repeat";
		}
		return resStr;
	}
	
	/**
	 * Description :check repeat by "or" method, each checking, need new ${tableName}() include check-params
	 * @param queryInfo
	 * @param isExludePKFlag, true is exclude primary key, false is include primary key
	 * @return "success" or "repeat"
	 * @author auto_generator
	 */	
	private String checkParamsRepeatByOrMethod(${tableName} queryInfo, Boolean isExcludePKFlag) {
		<#if pkColArr??>
		if (isExcludePKFlag) {
			Boolean setAllPk = true;
			<#list pkColArr as col>
			if (queryInfo.get${col.method}() == null) {
				setAllPk = false;
			}
			</#list>
			if (!setAllPk) {
				log.info("**************please set pk into the queryInfo.**************");
				return "error";
			}
		}
		</#if>
		String resStr = "success";
		List<${tableName}> beanList = ${PersistPropertyName}.getSingleTableListByOrMethod(queryInfo, isExcludePKFlag);
		if(beanList != null && beanList.size() > 0) {
			resStr = "repeat";
		}
		return resStr;
	}
}
