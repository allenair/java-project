package com.sinyd.triger;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TableBean {
	
	/**
	 * 表的名字
	 */
	private String tablename;
	
	/**
	 * 表中字段的信息(字段名称,属性)
	 */
	private Map<String,String> fieldMap;

	/**
	 * 字段名称列表
	 */
	private String fieldString;
	
	/**
	 * 状态，记录是否包含Upload字段，如果为fasle，表示不生产触发器
	 */
	private boolean hasUpload;
	
	/**
	 * 字段对应值列表
	 */
	private String valueString;
	
	public String getFieldString() {
		return fieldString;
	}

	public String getValueString() {
		return valueString;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public Map<String, String> getFieldMap() {
		return fieldMap;
	}

	public void setFieldMap(Map<String, String> fieldMap) {
		this.fieldMap = fieldMap;
		setFVString();
	}
	
	public boolean getHasUpload() {
		return this.hasUpload;
	}
	
	/**
	 * 设置字段名称列表
	 */
	private void setFVString(){
		StringBuffer fieldBuffer = new StringBuffer();
		StringBuffer valueBuffer = new StringBuffer();
		
		boolean isFirst = true;
		this.hasUpload = false;
		
        Set<String> key = fieldMap.keySet();
        for (Iterator<String> it = key.iterator(); it.hasNext();) {
        	//	字段名称
        	String sField = it.next();
        	//	字段类型
        	String sType = fieldMap.get(sField);
        	
        	if("UPLOAD".equals(sField.toUpperCase())){
        		this.hasUpload = true; 
        		continue;
        	}        	
        	
        	if(isFirst){
        		isFirst = false;
        	}else{
        		fieldBuffer.append(",");
        		//	数据库字符串连接符
        		valueBuffer.append(" || ','  ");
        	}

        	//	如果字段名称为ID，则转化为data_id，中心数据库中的data_id对应拌和站端的ID
        	if("ID".equals(sField.toUpperCase())){
        		//fieldBuffer.append(sField);
        		fieldBuffer.append("data_id");
        	}else{
        		fieldBuffer.append(sField);
        	}
    		
    		valueBuffer.append(getValueString(sField,sType));
        }
        
        this.fieldString = fieldBuffer.toString();
        this.valueString = valueBuffer.toString();
	}
	
	private String getValueString(String field,String type){
		StringBuffer buffer = new StringBuffer();
		String constStr = " quote_literal(COALESCE(cast(new." + field + " as VARCHAR))) ";
		String constNum = " COALESCE(cast(new." + field + " as VARCHAR)) ";
		
		//retStr = "  COALESCE(cast(new." + field + " as VARCHAR),'null')";
		//retStr = " case when new." + field + " is null then null else ";
		//+ " COALESCE(cast(new." + field + " as VARCHAR))";
		buffer.append(" || case when new.");
		buffer.append(field);
		buffer.append(" is null then 'null' else ");
		
		//	数字类型，不需要增加引号
		if("int8".equals(type) || "int4".equals(type) || "numeric".equals(type)){
			buffer.append(constNum);
			//retStr = " || " + retStr;
		}else{
			//	增加两端的引号，表示是字符类型的数据
			buffer.append(constStr);
		}

		buffer.append(" end ");
		return buffer.toString();
	}
}
