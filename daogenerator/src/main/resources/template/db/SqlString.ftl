package ${sqlPackage};

public class SqlString {
  <#list sqlList as sql>
	public final static String ${sql.valName}="${sql.realName}";
  </#list>
  
}