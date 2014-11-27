<?xml version="1.0" encoding="UTF-8"?>
<sqls namespace="auto2">
	<#list sqlTemplateList as sql>
	<sqlElement key="${sql.tableNameReal}#insert" author="auto_generator">  
		<![CDATA[ 
			insert into 
				${sql.tableNameReal}
			(
        		<#list sql.fields as item>
        		${sql.ifprefix}${item.columnName}${sql.ifmiddle}
        			${item.columnNameSymbol}${item.columnName}
        		${sql.ifsuffix}
        		</#list>
			)
			values(
        		<#list sql.fields as item>
        		${sql.ifprefix}${item.columnName}${sql.ifmiddle}
        			${item.columnNameSymbol}:${item.columnName}
        		${sql.ifsuffix}
        		</#list>
			)
	        ]]>
	</sqlElement>
	<sqlElement key="${sql.tableNameReal}#delete" author="auto_generator">  
        <![CDATA[ 
        	delete from 
        		${sql.tableNameReal}
        	where 1=1
        		<#list sql.fields as item>
        		${sql.ifprefix}${item.columnName}${sql.ifmiddle}
        			and ${item.columnName}=:${item.columnName}
        		${sql.ifsuffix}
        		</#list>
        ]]>
	</sqlElement>	
	<#if sql.pkColArr??>
	<sqlElement key="${sql.tableNameReal}#update" author="auto_generator">  
        <![CDATA[ 
        	update 
        		${sql.tableNameReal}
        	set
        		<#list sql.fieldsWithoutPKList as item>
        		${sql.ifprefix}${item.columnName}${sql.ifmiddle}
        			${item.columnNameSymbol} ${item.columnName}=:${item.columnName}
        		${sql.ifsuffix}
        		</#list>
        	where 1=1
			    <#list sql.pkColArr as col>
			  	and ${col.columnName}=:${col.columnName}
			    </#list>
        ]]>
	</sqlElement>
	<sqlElement key="${sql.tableNameReal}#update_all" author="auto_generator">  
        <![CDATA[ 
        	update 
        		${sql.tableNameReal}
        	set
    		<#list sql.fieldsWithoutPKList as item>
    			${item.columnName}=:${item.columnName}<#if item_has_next>,</#if>
    		</#list>
        	where 1=1
			    <#list sql.pkColArr as col>
			  	and ${col.columnName}=:${col.columnName}
			    </#list>
        ]]>
	</sqlElement>
	</#if>
	<sqlElement key="${sql.tableNameReal}#list" author="auto_generator">  
        <![CDATA[ 
        	select
        		<#list sql.fields as item>
        			<#if item.dateType!="3">
        		${item.columnName}<#if item_has_next>,</#if>
        			</#if>
        		</#list>
        	from
        		${sql.tableNameReal}
        	where 1=1
        		<#if sql.pkColArr??>
        		${sql.ifprefix}exclude_pk${sql.ifmiddle}
        			<#list sql.pkColArr as col>
        			${sql.ifprefix}${col.columnName}${sql.ifmiddle}
        				and ${col.columnName}!=:${col.columnName}
        			${sql.ifsuffix}
        			</#list>
        		${sql.elseTag}
        			<#list sql.pkColArr as col>
        			${sql.ifprefix}${col.columnName}${sql.ifmiddle}
        				and ${col.columnName}=:${col.columnName}
        			${sql.ifsuffix}
        			</#list>
        		${sql.ifsuffix}
        		</#if>
        		<#list sql.fieldsWithoutPKList as item>
        		${sql.ifprefix}${item.columnName}${sql.ifmiddle}
        			and ${item.columnName}=:${item.columnName}
        		${sql.ifsuffix}
        		</#list>
        ]]>
	</sqlElement>
	<sqlElement key="${sql.tableNameReal}#list_by_or" author="auto_generator">  
        <![CDATA[ 
	        select
        		<#list sql.fields as item>
        			<#if item.dateType!="3">
        		${item.columnName}<#if item_has_next>,</#if>
        			</#if>
        		</#list>
        	from
        		${sql.tableNameReal}
        	where 1=1
    		${sql.ifprefix}params_exists${sql.ifmiddle}
        		<#if sql.pkColArr??>
        		${sql.ifprefix}exclude_pk${sql.ifmiddle}
        			<#list sql.pkColArr as col>
        			${sql.ifprefix}${col.columnName}${sql.ifmiddle}
        				and ${col.columnName}!=:${col.columnName}
        			${sql.ifsuffix}
        			</#list>
        			and (
        		${sql.elseTag}
        			and (
        			<#list sql.pkColArr as col>
        			${sql.ifprefix}${col.columnName}${sql.ifmiddle}
        				${col.columnNameSymbol} ${col.columnName}=:${col.columnName}
        			${sql.ifsuffix}
        			</#list>
        		${sql.ifsuffix}
        		</#if>
        		<#list sql.fieldsWithoutPKList as item>
        		${sql.ifprefix}${item.columnName}${sql.ifmiddle}
        			${item.columnNameSymbol} ${item.columnName}=:${item.columnName}
        		${sql.ifsuffix}
        		</#list>
    			)
    		${sql.ifsuffix}
        ]]>
	</sqlElement>
	</#list>
</sqls>