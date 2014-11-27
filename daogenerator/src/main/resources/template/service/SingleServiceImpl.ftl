package ${ServiceImplPackage};

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ${DomainPackage}.${TableClassName};
import ${PersistPackage}.${PersistInterfaceName};
import ${ServicePackage}.${ServiceInterfaceName};
import ${VoPackage}.SearchBeanVO;
import com.sinyd.platform.uivo.vo.GridDataBean;

@Service("${ServicePropertyName}")
public class ${ServiceClassName} implements ${ServiceInterfaceName} {
	private static Logger log = LoggerFactory.getLogger(${ServiceClassName}.class);

	@Autowired
	private ${PersistInterfaceName} ${PersistPropertyName};

	public Map<String, Object> doList(SearchBeanVO queryInfo) {
		GridDataBean bean = new GridDataBean(queryInfo.getPage(),
				${PersistPropertyName}.getListCountNum(queryInfo),
				${PersistPropertyName}.getList(queryInfo));
		return bean.getGridData();
	}
	
	public String doSave(${TableClassName} bean) {
		String resStr = "success";
		int ret = 0;
		// add new record
		if (bean.getId() == 0) {
			ret = ${PersistPropertyName}.addNewRecord(bean);
		} else { // update
			ret = ${PersistPropertyName}.updateRecord(bean);
		}
		if (ret == 0) {
			resStr = "error";
		}
		return resStr;
	}

	public String doDelete(String idArray) {
		String resStr = "success";
		int ret = 0;

		String[] idArr = idArray.split("#");
		ret = ${PersistPropertyName}.deleteRecord(idArr);

		if (ret == 0) {
			resStr = "error";
		}
		return resStr;
	}
		
	public ${TableClassName} getInfo(${idType} id) {
		return ${PersistPropertyName}.getInfo(id);
	}
}
