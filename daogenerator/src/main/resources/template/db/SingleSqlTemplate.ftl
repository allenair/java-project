<?xml version="1.0" encoding="UTF-8"?>
<sqls namespace="autosingle">
  <#list sqlList as sql>
	<sqlElement key="${sql.tableName}#list"  author="auto_generator">  
        <![CDATA[ 
${sql.content}
        ]]>
	</sqlElement>
	
  </#list>
</sqls>