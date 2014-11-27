<?xml version="1.0" encoding="UTF-8"?>
<sqls namespace="auto2">
	<#list sqlTemplateList as sql>
	<sqlElement key="${sql.tableNamelower}#insert" author="auto_generator">  
		<![CDATA[ 
		<#if sql.pkColArr?? && sql.pkNotAutoincrement?? && sql.pkNotAutoincrement == '1'>
		if not exists(
			select 1 from ${sql.tableNameReal} where 1=1
			    <#list sql.pkColArr as col>
			  	and [${col.columnName}]=:${col.columnName}
			    </#list>
			)
		begin
	 		insert into 
				${sql.tableNameReal}
			(
        		<#list sql.fieldsWithoutByteList as item>
        		${sql.ifprefix}${item.columnName}${sql.ifmiddle}
        			<#if item_index != 0>,</#if>[${item.columnName}]
        		${sql.ifsuffix}
        		</#list>
        		<#list sql.fieldsByteList as item>
        		${sql.ifprefix}${item.columnName}${sql.ifmiddle}
        			,[${item.columnName}]
        		${sql.ifsuffix}
        		</#list>
			)
			values(
        		<#list sql.fieldsWithoutByteList as item>
        		${sql.ifprefix}${item.columnName}${sql.ifmiddle}
        			<#if item_index != 0>,</#if>${sql.ifprefix}${item.columnName} == '$null$'${sql.ifmiddleend}null${sql.elseTag}:${item.columnName}${sql.ifsuffix}
        		${sql.ifsuffix}
        		</#list>
        		<#list sql.fieldsByteList as item>
        		${sql.ifprefix}${item.columnName}${sql.ifmiddle}
        			,${sql.ifprefix}${item.columnName} == '$null$'${sql.ifmiddleend}null${sql.elseTag}${sql.slTag}${item.columnName}${sql.slendTag}${sql.ifsuffix}
        		${sql.ifsuffix}
        		</#list>
			)
 		end
 		else
		begin
	 		update 
        		${sql.tableNameReal}
        	set
        		<#list sql.fieldsWithoutPKList as item>
        		${sql.ifprefix}${item.columnName}${sql.ifmiddle}
        			<#if item_index != 0>,</#if>${sql.ifprefix}${item.columnName} == '$null$'${sql.ifmiddleend}[${item.columnName}]=null${sql.elseTag}[${item.columnName}]=:${item.columnName}${sql.ifsuffix}
        		${sql.ifsuffix}
        		</#list>
        		<#list sql.fieldsByteList as item>
        		${sql.ifprefix}${item.columnName}${sql.ifmiddle}
        			,${sql.ifprefix}${item.columnName} == '$null$'${sql.ifmiddleend}[${item.columnName}]=null${sql.elseTag}[${item.columnName}]=${sql.slTag}${item.columnName}${sql.slendTag}${sql.ifsuffix}
        		${sql.ifsuffix}
        		</#list>
        	where 1=1
			    <#list sql.pkColArr as col>
			  	and [${col.columnName}]=:${col.columnName}
			    </#list>
 		end
		<#elseif sql.pkNotAutoincrement?? && sql.pkNotAutoincrement == '3'>
			if not exists(
			select 1 from ${sql.tableNameReal} where [code] = '${sql.slTag}orgcode${sql.slendTag}${sql.slTag}code${sql.slendTag}'
			)
			begin
	 		insert into 
				${sql.tableNameReal}
			(
				[code]
        		<#list sql.fieldsWithoutPKList as item>
        		${sql.ifprefix}${item.columnName}${sql.ifmiddle}
        			,[${item.columnName}]
        		${sql.ifsuffix}
        		</#list>
			)
			values(
				'${sql.slTag}orgcode${sql.slendTag}${sql.slTag}code${sql.slendTag}'
        		<#list sql.fieldsWithoutPKList as item>
        		${sql.ifprefix}${item.columnName}${sql.ifmiddle}
        			,${sql.ifprefix}${item.columnName} == '$null$'${sql.ifmiddleend}null${sql.elseTag}:${item.columnName}${sql.ifsuffix}
        		${sql.ifsuffix}
        		</#list>
			)
 			end
 		 <#else>
			insert into 
				${sql.tableNameReal}
			(
        		<#list sql.fieldsWithoutPKList as item>
        		${sql.ifprefix}${item.columnName}${sql.ifmiddle}
        			<#if item_index != 0>,</#if>[${item.columnName}]
        		${sql.ifsuffix}
        		</#list>
        		<#list sql.fieldsByteList as item>
        		${sql.ifprefix}${item.columnName}${sql.ifmiddle}
        			,[${item.columnName}]
        		${sql.ifsuffix}
        		</#list>
			)
			values(
        		<#list sql.fieldsWithoutPKList as item>
        		${sql.ifprefix}${item.columnName}${sql.ifmiddle}
        			<#if item_index != 0>,</#if> ${sql.ifprefix}${item.columnName} == '$null$'${sql.ifmiddleend}null${sql.elseTag}:${item.columnName}${sql.ifsuffix}
        		${sql.ifsuffix}
        		</#list>
        		<#list sql.fieldsByteList as item>
        		${sql.ifprefix}${item.columnName}${sql.ifmiddle}
        			,${sql.ifprefix}${item.columnName} == '$null$'${sql.ifmiddleend}null${sql.elseTag}${sql.slTag}${item.columnName}${sql.slendTag}${sql.ifsuffix}
        		${sql.ifsuffix}
        		</#list>
			)
		</#if>
	        ]]>
	</sqlElement>
	<sqlElement key="${sql.tableNamelower}#delete" author="auto_generator">  
        <![CDATA[ 
        	delete from 
        		${sql.tableNameReal}
        	where 1=1
        		<#list sql.fields as item>
        		${sql.ifprefix}${item.columnName}${sql.ifmiddle}
        			and [${item.columnName}]=:${item.columnName}
        		${sql.ifsuffix}
        		</#list>
        ]]>
	</sqlElement>	
	<#if sql.pkColArr??>
	<sqlElement key="${sql.tableNamelower}#update" author="auto_generator">  
        <![CDATA[ 
        	update 
        		${sql.tableNameReal}
        	set
        		<#list sql.fieldsWithoutPKList as item>
        		${sql.ifprefix}${item.columnName}${sql.ifmiddle}
        			<#if item_index != 0>,</#if> ${sql.ifprefix}${item.columnName} == '$null$'${sql.ifmiddleend}[${item.columnName}]=null${sql.elseTag}[${item.columnName}]=:${item.columnName}${sql.ifsuffix}
        		${sql.ifsuffix}
        		</#list>
        		<#list sql.fieldsByteList as item>
        		${sql.ifprefix}${item.columnName}${sql.ifmiddle}
        			,${sql.ifprefix}${item.columnName} == '$null$'${sql.ifmiddleend}[${item.columnName}]=null${sql.elseTag}[${item.columnName}]=${sql.slTag}${item.columnName}${sql.slendTag}${sql.ifsuffix}
        		${sql.ifsuffix}
        		</#list>
        	where 1=1
			    <#list sql.pkColArr as col>
			  	and ${col.columnName}=:${col.columnName}
			    </#list>
        ]]>
	</sqlElement>
	</#if>	
	</#list>
</sqls>