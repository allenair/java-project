package ${PersistImplPackage};

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import ${DomainPackage}.${TableClassName};
import ${VoPackage}.SearchBeanVO;
import ${PersistPackage}.${PersistInterfaceName};
import ${sqlPackage}.SqlString;
import com.sinyd.platform.database.AbstractDao;
import com.sinyd.platform.utiltools.util.StringUtil;

@Repository("${PersistPropertyName}")
public class ${PersistClassName} extends AbstractDao implements ${PersistInterfaceName} {
	private static Logger log = LoggerFactory.getLogger(${PersistClassName}.class);
	
	public List<Map<String, Object>> getList(SearchBeanVO queryInfo) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		if(StringUtil.isNotBlank(queryInfo.getSearchName())){
            paramMap.put("searchName", "%"+queryInfo.getSearchName()+"%");
        }
		paramMap.put("sortname", queryInfo.getSortname());
		paramMap.put("sortorder", queryInfo.getSortorder());
		return this.pagingForListByTemplate(SqlString.${singleListSql}, queryInfo.getPagesize(), queryInfo.getPage(),paramMap);
	}
	
	public int getListCountNum(SearchBeanVO queryInfo) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if(StringUtil.isNotBlank(queryInfo.getSearchName())){
            paramMap.put("searchName", "%"+queryInfo.getSearchName()+"%");
        }
		return this.recordNumberForListByTemplate(SqlString.${singleListSql}, paramMap);
	}

	public int deleteRecord(String[] idArr) {
		int sum=0;
		for (String idStr : idArr) {
			sum+=${TableClassName}.deleteRecord(this, new ${idType}(idStr));
		}
		return sum;
	}

	public int addNewRecord(${TableClassName} bean) {
		return bean.insertRecord(this);
	}

	public int updateRecord(${TableClassName} bean) {
		return bean.updateRecord(this);
	}

	public ${TableClassName} getInfo(${idType} id) {
		return ${TableClassName}.getRecordByPK(this, id);
	}
}