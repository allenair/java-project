package ${domainVoPackage};

import java.io.Serializable;

/*
 * @version 2.0
 */

public class ${tableName}VO implements Serializable {

	private static final long serialVersionUID = 1L;
	
    <#list fields as item>
  		<#if item.type == "java.sql.Timestamp">
  	private String ${item.columnName};
  		<#elseif item.type == "java.sql.Date">
  	private String ${item.columnName};
  		<#else>
  	private ${item.type} ${item.columnName};
  		</#if>
  		
    </#list>
    
    <#list fields as item>
      	<#if item.type == "java.sql.Timestamp">
    public String get${item.method} () {
      	<#elseif item.type == "java.sql.Date">
    public String get${item.method} () {      	
      	<#else>
	public ${item.type} get${item.method} () {
		</#if>
		return ${item.columnName};
	}
	    <#if item.type == "java.sql.Timestamp">
	public void set${item.method} ( String obj ) {
      	<#elseif item.type == "java.sql.Date">
    public void set${item.method} ( String obj ) {
      	<#else>
    public void set${item.method} ( ${item.type} obj ) {
      	</#if>
		${item.columnName} = obj;
	}
    </#list>
}