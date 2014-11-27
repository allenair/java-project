-- Function: ${tablename}_upload_triger()
-- DROP FUNCTION ${tablename}_upload_triger();

CREATE OR REPLACE FUNCTION ${tablename}_upload_triger()
  	RETURNS trigger AS
$BODY$
declare
  	lv_value text;
  	lv_field text;
  	lv_delete text;
  	lv_insert text;
  	lv_sql text;			--最终SQL脚本
  	lv_source text;		--拌合站代码
  	lv_isRun boolean;
begin
	--平台系统表(platform_sys_config)
	select sysconfig_value into lv_source from platform_sys_config where sysconfig_key='source_code' ;

	lv_isRun := false;
	
	IF (TG_OP = 'INSERT') THEN
		lv_isRun := true;
	END IF;
	
	IF (TG_OP = 'UPDATE') THEN
		--由已经上传修改为未上传，表示再次上传
		if (old.upload = '1' and new.upload = '0') then
			lv_isRun := true;
		end if;
	END IF;	
	
	IF (lv_isRun) THEN
		lv_field := '${FieldString}';
		lv_value := ''  ${ValueString} ;
		
		lv_field := lv_field || ',source_code' ;
		lv_value := lv_value || ',''' || lv_source || ''''; 
		
	  	lv_insert := 'insert into ${tablename} (' || lv_field || ') values (' || lv_value || ')';
		
		--增加引号
		--lv_sql = quote_literal(lv_sql);
		
		INSERT INTO sys_upload_task (TABLE_NAME,DATA_ID,SOURCE_CODE,SEND_CONTENT) VALUES('${tablename}',NEW.ID,lv_source,lv_insert);

		--RAISE NOTICE 'This is a sql = %', lv_sql;
		
		UPDATE 	${tablename} SET UPLOAD='1' WHERE ID=NEW.ID; 
	END IF;
	
	IF (TG_OP = 'DELETE') THEN
		lv_delete := 'DELETE FROM ${tablename} WHERE DATA_ID =' || OLD.ID || ' and source_code = ''' || lv_source || '''' ;
		INSERT INTO sys_upload_task (TABLE_NAME,DATA_ID,SOURCE_CODE,SEND_CONTENT) VALUES('${tablename}',OLD.ID,lv_source,lv_delete); 
		--RAISE NOTICE 'This is a sql UPDATE = %', lv_sql;
	END IF;

  	return null;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION ${tablename}_upload_triger()
  OWNER TO qaspmixdb;
  
