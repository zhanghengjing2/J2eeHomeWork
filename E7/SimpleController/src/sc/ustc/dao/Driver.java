package sc.ustc.dao;

/**
 * 数据库驱动类
 * @author zhang
 *
 */
public class Driver {

	private String databaseName;//数据库名
	private String url;//数据库url
	private String data_name;//数据库用户名
	private String data_pass;//数据库密码
	private int database_select;//1连接mysql,2连接sqlite
	
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getData_name() {
		return data_name;
	}
	public void setData_name(String data_name) {
		this.data_name = data_name;
	}
	public String getData_pass() {
		return data_pass;
	}
	public void setData_pass(String data_pass) {
		this.data_pass = data_pass;
	}
	public int getDatabase_select() {
		return database_select;
	}
	public void setDatabase_select(int database_select) {
		this.database_select = database_select;
	}
}
