package com.sinyd.triger;

/**
 * 
 * @author Administrator
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//	请注意，使用的数据库连接是 gen2.properties 中的设置，对应的是Postgresql
		
		CreateTriger triger = new CreateTriger();
		try {
			triger.getTrager("c:\\function.txt","c:\\triger.txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
