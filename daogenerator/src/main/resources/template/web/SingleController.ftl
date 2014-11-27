package ${ControllerPackage};

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ${DomainPackage}.${TableClassName};
import ${ServicePackage}.${ServiceInterfaceName};
import ${VoPackage}.SearchBeanVO;

@Controller
public class ${ControllerClassName} {
	@Autowired
	private ${ServiceInterfaceName} ${ServicePropertyName};

	@RequestMapping(value = "/${modualPath}/${tableLittleName}list.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> doList(SearchBeanVO queryInfo) {
		return ${ServicePropertyName}.doList(queryInfo);
	}
	
	@RequestMapping(value = "/${modualPath}/${tableLittleName}save.do", method = RequestMethod.POST)
	@ResponseBody
	public String doSave(${TableClassName} bean) {
		return ${ServicePropertyName}.doSave(bean);
	}
	
	@RequestMapping(value = "/${modualPath}/${tableLittleName}delete.do", method = RequestMethod.POST)
	@ResponseBody
	public String doDelete(String idArray) {
		return ${ServicePropertyName}.doDelete(idArray);
	}

	@RequestMapping(value = "/${modualPath}/${tableLittleName}info.do", method = RequestMethod.GET)
	@ResponseBody
	public ${TableClassName} getInfo(${idType} id) {
		return ${ServicePropertyName}.getInfo(id);
	}
}