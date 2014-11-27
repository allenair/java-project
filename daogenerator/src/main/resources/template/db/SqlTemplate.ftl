<?xml version="1.0" encoding="UTF-8"?>
<sqls namespace="auto">
  <#list sqlList as sql>
  
  <#if sql.list??>
	<sqlElement key="${sql.tableName}#list"  author="auto_generator">  
        <![CDATA[ 
        	${sql.list}
        ]]>
	</sqlElement>
	<sqlElement key="${sql.tableName}#listbypk"  author="auto_generator">  
        <![CDATA[ 
        	${sql.listbypk}
        ]]>
	</sqlElement>
	<sqlElement key="${sql.tableName}#insert"  author="auto_generator">  
        <![CDATA[ 
        	${sql.insert}
        ]]>
	</sqlElement>
	<sqlElement key="${sql.tableName}#update"  author="auto_generator">  
        <![CDATA[ 
        	${sql.update}
        ]]>
	</sqlElement>
	<sqlElement key="${sql.tableName}#delete"  author="auto_generator">  
        <![CDATA[ 
        	${sql.delete} 
        ]]>
	</sqlElement>
	
  <#else>
  	<sqlElement key="${sql.key}"  author="auto_generator">  
        <![CDATA[ 
        	${sql.content}
        ]]>
	</sqlElement>
  </#if>
	
  </#list>
</sqls>