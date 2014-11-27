package com.sinyd.generator;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// need can connect to database 
		new GeneratorDao().genDaoBean();
		// neet set single.table.modual.set
		new SingleTableModualGen().gen();
		// need change the path setting, in gen.property "sql.other.catalog" parameter, to your sql template path
		new TemplateToSqlString().gen();
		new ModualFrameworkGen().gen();
	}

}
