package ${PersistPackage};

import java.util.List;
import java.util.Map;

import ${DomainPackage}.${TableClassName};
import ${VoPackage}.SearchBeanVO;

public interface ${PersistInterfaceName} {
	public List<Map<String, Object>> getList(SearchBeanVO queryInfo);

	public int getListCountNum(SearchBeanVO queryInfo);

	public int addNewRecord(${TableClassName} bean);

	public int updateRecord(${TableClassName} bean);

	public int deleteRecord(String[] idArr);
	
	public ${TableClassName} getInfo(${idType} id);
}