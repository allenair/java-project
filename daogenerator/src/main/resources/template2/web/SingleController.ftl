package ${ControllerPackage};

import java.util.Map;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ${ServicePackage}.${ServiceInterfaceName};
import ${domainPackage}.${tableName};
import ${domainVoPackage}.${tableName}VO;
import ${searchBeanVOPackage}.${tableName}SearchBeanVO;
import com.sinyd.sframe.util.GlobalVal;
import com.sinyd.sframe.util.vo.UserBean;

/*
 * @version 2.0
 * @author
 */

@Controller
public class ${ControllerClassName} {
	private static Logger log = LoggerFactory.getLogger(${ControllerClassName}.class);
	
	@Autowired
	private ${ServiceInterfaceName} ${ServicePropertyName};

	/**
	 * Description :get combox list by vo queryInfo
	 * @param queryInfo
	 * @return combox list
	 * @author auto_generator
	 */
	@RequestMapping(value = "/${modualPath}/${tableLittleName}getcomboxlist.do", method = {RequestMethod.GET})
	@ResponseBody
	public List<Map<String, Object>> getComboxList(${tableName}SearchBeanVO queryInfo) {
		return ${ServicePropertyName}.getComboxList(queryInfo);
	}
		
	/**
	 * Description :get record list records by vo queryInfo withnot paging
	 * @param queryInfo
	 * @return record list
	 * @author auto_generator
	 */
	@RequestMapping(value = "/${modualPath}/${tableLittleName}withoutpaginglist.do", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> getListWithoutPaging(${tableName}SearchBeanVO queryInfo) {
		return ${ServicePropertyName}.getListWithoutPaging(queryInfo);
	}

	/**
	 * Description :get record list records by vo queryInfo with paging
	 * @param queryInfo
	 * @return record list
	 * @author auto_generator
	 */
	@RequestMapping(value = "/${modualPath}/${tableLittleName}withpaginglist.do", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> getListWithPaging(${tableName}SearchBeanVO queryInfo) {
		return ${ServicePropertyName}.getListWithPaging(queryInfo);
	}
	
	<#if pkColArr??>
	/**
	 * Description :get single-table information by primary key 
	 * @param primary key 
	 * @return ${tableName}VO
	 * @author auto_generator
	 */	
	@RequestMapping(value = "/${modualPath}/${tableLittleName}infobypk.do", method = {RequestMethod.GET})
	@ResponseBody
	public ${tableName}VO getInfoByPK(<#list pkColArr as col>${col.type} ${col.columnName}<#if col_has_next>, </#if></#list>) {
		return ${ServicePropertyName}.getInfoByPK(<#list pkColArr as col>${col.columnName}<#if col_has_next>, </#if></#list>);
	}	
	</#if>

	/**
	 * Description :add method
	 * @param bean
	 * @return "success" or "error" or user defined
	 * @author auto_generator
	 */	
	@RequestMapping(value = "/${modualPath}/${tableLittleName}save.do", method = {RequestMethod.POST})
	@ResponseBody
	public String doSave(HttpServletRequest request, ${tableName} bean) {
		String result = "";
		HttpSession session = request.getSession();
		UserBean user = (UserBean)session.getAttribute(GlobalVal.USER_SESSION);
		try {
			result = ${ServicePropertyName}.save(bean, user);
		} catch (Exception e) {
			log.error(e.getMessage());
			result = "error";
		}
		/*
		// record log	
		if("success".equals(result)){
			String msg = "log content";
			SysTools.saveLog(request, msg); // record log method
		}
		*/
		return result;
	}

	/**
	 * Description :update method
	 * @param bean
	 * @return "success" or "error" or user defined
	 * @author auto_generator
	 */	
	@RequestMapping(value = "/${modualPath}/${tableLittleName}update.do", method = {RequestMethod.POST})
	@ResponseBody
	public String doUpdate(HttpServletRequest request, ${tableName} bean) {
		String result = "";
		HttpSession session = request.getSession();
		UserBean user = (UserBean)session.getAttribute(GlobalVal.USER_SESSION);
		try {
			result = ${ServicePropertyName}.update(bean, user);
		} catch (Exception e) {
			log.error(e.getMessage());
			result = "error";
		}
		/*			
		// record log	
		if("success".equals(result)){
			String msg = "log content";
			SysTools.saveLog(request, msg); // record log method
		}
		*/
		return result;
	}

	/**
	 * Description :delete method
	 * @param pk
	 * @return "success" or "error" or user defined
	 * @author auto_generator
	 */		
	@RequestMapping(value = "/${modualPath}/${tableLittleName}delete.do", method = {RequestMethod.POST})
	@ResponseBody
	public String doDelete(HttpServletRequest request, ${tableName} bean) {
		String result = ${ServicePropertyName}.delete(bean);
		/*
		// record log	
		if("success".equals(result)){
			String msg = "log content";
			SysTools.saveLog(request, msg); // record log method
		}
		*/
		return result;
	}
}