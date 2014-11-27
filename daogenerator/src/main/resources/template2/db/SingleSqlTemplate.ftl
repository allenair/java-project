<?xml version="1.0" encoding="UTF-8"?>
<sqls namespace="autosingle2">
    <#list sqlList as sql>
	<sqlElement key="${sql.tableName}#list"  author="auto_generator">  
        <![CDATA[ 
${sql.content}
        ]]>
	</sqlElement>
	
	<sqlElement key="${sql.tableName}#update"  author="auto_generator">  
        <![CDATA[ 
${sql.content_u}
        ]]>
	</sqlElement>
	
    </#list>
</sqls>