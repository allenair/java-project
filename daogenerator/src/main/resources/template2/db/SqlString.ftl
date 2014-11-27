package ${sqlPackage};

/*
 * @version 2.0
 */

public class SqlString {
    <#list sqlTemplateList as sql>
	public final static String ${sql.valName}="${sql.realName}";
    </#list>
}