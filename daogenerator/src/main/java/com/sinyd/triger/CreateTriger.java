package com.sinyd.triger;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sinyd.generator2.UtilTools;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class CreateTriger {

	/**
	 * 生产TrigerFuntion文件和Triger文件
	 * 
	 * @param outFunctionFile 	函数的输出文件名
	 * @param outTrigerFile		触发器的输出文件名
	 * 
	 * @throws Exception
	 */
    public void getTrager(String outFunctionFile,String outTrigerFile) throws Exception{
    	/*
    	 * 1. 根据配置文件获取需要生成触发器的表
    	 * 2. 查询出每张表的字段信息（名称和属性，字段属性如下）
    	 * 		"int8"
					"date"
					"bpchar"
					"timestamp"
					"varchar"
					"numeric"
					"int4"
					"text"
    	 * 3. 表的字段与属性作为输入信息，调取模板生产输出SQL脚本
    	 */
    	List<TableBean> list = this.printTableList();

    	Configuration cfg = UtilTools.getFreeMarkerCfg();
    	
		Template templateFunction = cfg.getTemplate("triger/trigerfunction.ftl");
		Template templateTriger = cfg.getTemplate("triger/triger.ftl");
    	
		FileWriter fileFuntionWriter = new FileWriter(outFunctionFile);
		FileWriter fileTrigerWriter = new FileWriter(outTrigerFile);
		
    	for(TableBean bean : list){
    		Map<String,Object> root = new HashMap<String,Object>();
    		
    		if(bean.getHasUpload()){
                root.put("tablename",bean.getTablename());
                root.put("FieldString", bean.getFieldString());
                root.put("ValueString", bean.getValueString());
                
                System.out.println("select " + bean.getFieldString() + " from " + bean.getTablename() + ";");
                
                templateFunction.process(root, fileFuntionWriter);
                templateTriger.process(root, fileTrigerWriter);    		
    		}
    	}
    	
    	fileFuntionWriter.flush();    	
    	fileFuntionWriter.close();
    	
    	fileTrigerWriter.flush();    	
    	fileTrigerWriter.close();
    	
    }
    
    /**
     * 开发过程中用来测试的
     * @return
     */
    private TableBean makeTestInfo(){
    	TableBean bean = new TableBean();
    	
    	bean.setTablename("triger_test");
    	
    	Map<String,String> fieldMap = new HashMap<String,String>();
    	
    	fieldMap.put("int8_test", "int8");
    	fieldMap.put("int4_test", "int4");
    	fieldMap.put("date_test", "date");
    	fieldMap.put("timestamp_test", "timestamp");
    	fieldMap.put("numeric_test", "numeric");
    	fieldMap.put("char_test", "bpchar");
    	fieldMap.put("varchar_test", "varchar");
    	fieldMap.put("text_test", "text");
    	
    	bean.setFieldMap(fieldMap);
    	
    	//bean.setFVString();
    	
    	return bean;
    }
    
    /**
     * 构造表对象，以及表结构	
     * @return
     * @throws Exception
     */
	private List<TableBean> printTableList() throws Exception {
		ResultSet rs = null;
		Statement st = null;

		Connection conn = UtilTools.getConn();

		st = conn.createStatement();
		StringBuffer sql = new StringBuffer();

		sql.append(" SELECT ");
		sql.append(" 	att.attname,c.relname,att.attnum,att.atttypid,t.typname ");
		sql.append(" FROM ");
		sql.append(" 	pg_attribute att, ");
		sql.append(" 	pg_class c, ");
		sql.append(" 	pg_type t  ");
		sql.append(" WHERE ");
		sql.append(" 	1=1 ");
		sql.append(" and	att.attnum > 0		 ");
		sql.append(" and	att.atttypid > 0	 ");
		sql.append(" and	att.atttypid = t.oid ");
		sql.append(" and	c.relname in (select relname from pg_stat_user_tables) ");
		sql.append(" and	att.attrelid = c.oid	 ");
//		sql.append(" and	c.relname = 'pqs_mm_measure_monit'	 ");		
		sql.append(" order by c.relname	 ");

		rs = st.executeQuery(sql.toString());

		List<TableBean> list = new ArrayList<TableBean>();

		String currentTableName = "";
		TableBean tBean = null;

		Map<String, String> fieldMap = null;
		
		while (rs.next()) {
			String tName = rs.getString("relname"); // 获取数据表名

			if (!currentTableName.equals(tName)) {

				if (tBean != null) { // 上一个TableBean已经构造完毕，需要加入队列中
					tBean.setFieldMap(fieldMap);
					list.add(tBean);
				}

				tBean = new TableBean();
				tBean.setTablename(tName);
				currentTableName = tName;

				fieldMap = new HashMap<String, String>();
			}

			fieldMap.put(rs.getString("attname"), rs.getString("typname"));
		}

		if (tBean != null) {
			tBean.setFieldMap(fieldMap);
			list.add(tBean);
		}

		rs.close();
		st.close();

		conn.close();
		
		return list;
	}
 
}
