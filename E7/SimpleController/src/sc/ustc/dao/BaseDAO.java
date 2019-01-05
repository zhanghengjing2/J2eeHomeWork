package sc.ustc.dao;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;

import com.mysql.jdbc.Statement;

/**
 * 抽象DAO基
 * @author zhang
 *
 */
public abstract class BaseDAO {
	
	protected Driver driver;//数据库驱 动类
	protected String url;//数据库访问路径
	protected String userName;//数据库用户名
	protected String userPassword;//数据库用户 密码
	
	public BaseDAO() {
		// TODO Auto-generated constructor stub
	}
	/*setter方法*/
	public Driver getDriver() {
		return driver;
	}
	public void setDriver(Driver driver) {
		this.driver = driver;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	
	
	
	//Connection, 负责打开数据库连接
	public Connection openDBConnection() 
	{
		Connection conn = null;//连接
		Statement stat = null;//状态
		// 注册驱动
	    try {
			Class.forName(this.driver.getDatabaseName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("加载驱动失败！");
		}
	    // 创建链接
	    try {
	    	if(this.driver.getDatabase_select()==1)
	    		conn = (Connection) DriverManager.getConnection(this.url,this.userName,this.userPassword);
	    	if(this.driver.getDatabase_select()==2)
	    		conn = (Connection) DriverManager.getConnection(this.url);
	    	System.out.println("连接成功！");
	    	return conn;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("获取数据库连接失败！");
			return null;
		}
		
	}
	
	//boolean, 负责关闭数据库连接
	public boolean closeDBConnection(Connection conn)
	{
		try {
			conn.close();//关闭连接
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("数据库连接断开失败！");
			return false;
		}
	}
	
	/*抽象方法*/
	//Object, 负责执行 sql 语句，并返回结果对象
	public abstract Object query(BaseBean T);
	//boolean, 负责执行 sql 语句，并返回执行结果
	public abstract boolean insert(BaseBean T);
	//boolean, 负责执行 sql 语句，并返回执行结果 
	public abstract boolean update(BaseBean T);
	//boolean, 负责执行 sql 语句，并返回执行结果
	public abstract boolean delete(BaseBean T);
	public String getUserName() {
		return userName;
	}
	
	
	
	
}
