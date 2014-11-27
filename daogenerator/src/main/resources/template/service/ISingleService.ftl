package ${ServicePackage};

import java.util.Map;

import ${DomainPackage}.${TableClassName};
import ${VoPackage}.SearchBeanVO;

public interface ${ServiceInterfaceName} {
	public Map<String, Object> doList(SearchBeanVO queryInfo);

	public String doSave(${TableClassName} bean);

	public String doDelete(String idArray);

	public ${TableClassName} getInfo(${idType} id);
}