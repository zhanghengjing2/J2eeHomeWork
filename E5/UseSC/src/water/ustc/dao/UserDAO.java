package water.ustc.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.*;


import sc.ustc.dao.BaseDAO;
import sc.ustc.dao.Driver;
import sc.ustc.model.User;
import water.ustc.bean.UserBean;

public class UserDAO extends BaseDAO {

	private Driver driver;
	private int database_select=2;//1连接mysql,2连接sqlite
	
	public UserDAO() {
		// TODO Auto-generated constructor stub
		this.driver= new Driver();
		if(database_select==1)
		{
			driver.setData_name("root");
			driver.setData_pass("zhj1996520");
			driver.setUrl("jdbc:mysql://localhost:3306/j2ee");
			driver.setDatabaseName("com.mysql.jdbc.Driver");
			driver.setDatabase_select(this.database_select);
			System.out.println("use Mysql");
		}
		if(database_select==2)
		{
			driver.setData_name("null");
			driver.setData_pass("null");
			driver.setUrl("jdbc:sqlite:/Users/zhang/Documents/J2EE/UseSC/src/J2ee.db");
			driver.setDatabaseName("org.sqlite.JDBC");
			driver.setDatabase_select(this.database_select);
			System.out.println("use SQLite");
		}
		this.setDriver(this.driver);//初始化driver
		this.setUrl(this.driver.getUrl());//初始化父类url
		this.setUserName(this.driver.getData_name());//初始化数据库登录name
		this.setUserPassword(this.driver.getData_pass());//初始化数据库登录password
	}
	
	/**
	 * 删除元素
	 */
	@Override
	public boolean delete(String sql) {
		// TODO Auto-generated method stub
		Connection conn = this.openDBConnection();
		Statement stat= null;
		try {
			stat = (Statement) conn.createStatement();
			stat.executeUpdate(sql); 
			conn.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	
	/**
	 * 插入数据库
	 */
	@Override
	public boolean insert(String sql) {
		// TODO Auto-generated method stub
		Connection conn = this.openDBConnection();
		Statement stat= null;
		try {
			stat = (Statement) conn.createStatement();
			stat.executeUpdate(sql); 
			conn.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}

	
	/**
	 * 查询数据库
	 * @param sql
	 * @return
	 */
	@Override
	public Object query(String sql) {
		// TODO Auto-generated method stub
		//建立连接
		Connection conn = this.openDBConnection();//建立连接
		User u = new User();//新建一个对象
		try {
			Statement stat = (Statement) conn.createStatement();
			ResultSet rs = stat.executeQuery(sql);
			// 输出查询结果
		    while(rs.next()) 
		    {
		    	String passWord=rs.getString("userPassword");//得到密码
		        System.out.println("passWord from database--UserDAO:"+passWord);
		        
		        u.setPassWord(passWord);
		    }
		    //关闭连接
		    conn.close();
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("查询失败");
			return null;
		}
		return u;
	}

	
	/**
	 * 修改元素
	 */
	@Override
	public boolean update(String sql) {
		// TODO Auto-generated method stub
		Connection conn = this.openDBConnection();
		Statement stat= null;
		try {
			stat = (Statement) conn.createStatement();
			stat.executeUpdate(sql); 
			conn.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	
}
