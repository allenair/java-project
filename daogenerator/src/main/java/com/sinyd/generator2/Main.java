package com.sinyd.generator2;

import com.sinyd.generator2.GeneratorDao;
import com.sinyd.generator2.SingleTableModualGen;
import com.sinyd.generator2.TemplateToSqlString;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// need can connect to database 
		new GeneratorDao().genDoBean();
		
		// neet set single.table.modual.set
		new SingleTableModualGen().gen();
		
		// need change the path setting, in gen.property "sql.other.catalog" parameter, to your sql template path
		new TemplateToSqlString().gen();
	}

}
