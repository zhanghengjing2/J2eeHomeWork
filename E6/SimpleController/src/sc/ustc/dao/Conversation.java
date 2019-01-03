package sc.ustc.dao;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.dom4j.DocumentException;

import net.sf.cglib.proxy.Enhancer;
import sc.ustc.dao.lazyload.Proxybean;
import sc.ustc.dao.lazyload.SqlQueryLazyLoader;
import sc.ustc.model.Property;
import sc.ustc.model.User;

public class Conversation  {

	private String xml_path="/or_mapping.xml";//or_mapping.xml的路径
	private Configuration configuration;
	private Connection conn= null;
	private Driver driver;
	private String table_name = null;//表名
	private String table_pk = null;//键值
	private String table_username = null;//用户名
	private String table_password = null;//密码
	private String objectname = null;//类名
	private List<Property> propertys;//属性对象
	
	//构造函数
	public Conversation() {
		// TODO Auto-generated constructor stub
		Configuration configuration = new Configuration();
		configuration.setPath(this.xml_path);
		setConfiguration(configuration);
	}
	
	//获取Configuration
	public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        try {
        	driver= this.configuration.getDBMS();
			initTable();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	//得到表格数据
	private void initTable(){
		
        if (this.table_name == null){
            this.table_name = configuration.getTableName();
        }
        if (this.table_pk == null){
            this.table_pk = configuration.getTablePK();
        }
        if (this.table_username == null){
            this.table_username = configuration.getTableColumn("UserName");
        }
        if (this.table_password == null){
            this.table_password = configuration.getTableColumn("UserPass");
        }
        if(this.objectname==null) {
        	this.objectname=configuration.getEntityName();
        }
        try {
			this.propertys=configuration.getProperty();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

	/**
	 * 打开数据库
	 * @return connn
	 */
	public void OpenDB()
	{
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
	    		this.conn = (Connection) DriverManager.getConnection(this.driver.getUrl(),this.driver.getData_name(),this.driver.getData_pass());
	    	if(this.driver.getDatabase_select()==2)
	    		this.conn = (Connection) DriverManager.getConnection(this.driver.getUrl());
	    	System.out.println("连接成功！");
	    	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("获取数据库连接失败！");
		}
	}
	
	/**
	 * 关闭数据库
	 * @return
	 */
	public boolean CloseDB()
	{
		try {
			this.conn.close();//关闭连接
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("数据库连接断开失败！");
			return false;
		}	
	}
	
	/**
	 * 查询
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public <T extends BaseBean> List<String> getObject(T user) throws SQLException
	{
		List<String> result = new ArrayList<>();
		String userid = null,username = null,passWord=null;
		BaseBean obj = new BaseBean();
		//把属性分成两类，一类不懒加载。一类懒加载
		List<Property> lazyload = new ArrayList<>();
		List<Property> notlazyload = new ArrayList<>();
		for(Property p : this.propertys)
		{
			if(p.isLazy())
				lazyload.add(p);
			else
				notlazyload.add(p);
		}
		//得到id值
		String value = user.getValue();
		//打开连接
		OpenDB();
		
		if(!notlazyload.isEmpty())
		{
			//对于非懒加载部分，直接构造查询语句
			String info=null;
			for(Property p:notlazyload)
			{
				info = info+","+p.getName();
			}
			info =info.substring(5, info.length());
			//构造查询语句
			String sql="select "+info+" from "+this.table_name+" where UserId = '"+value+"';";
			//进行查询
			ResultSet rs =  (ResultSet) query(sql);
			// 输出查询结果
		    while(rs.next()) 
		    {
		    	String str;
		    	for(Property p:notlazyload)
		    	{
		    		str = rs.getString(p.getName());
		    		result.add(str);
		    	}
		    }
		    System.out.println("非懒加载:"+result);
		}
		
	    if(!lazyload.isEmpty())
	    {
	    	//对于懒加载，启动代理
	    	Proxybean p = createLazyloadquery(lazyload);
	    	String info=null;
			for(String s:p.getLazyinfo())
			{
				info = info+","+s;
			}
			info =info.substring(5, info.length());
			//构造查询语句
			String sql="select "+info+" from "+this.table_name+" where UserId = '"+value+"';";
			//进行查询
			ResultSet rs =  (ResultSet) query(sql);
			// 输出查询结果
		    while(rs.next()) 
		    {
		    	String str;
		    	for(String s:p.getLazyinfo())
		    	{
		    		str = rs.getString(s);
		    		result.add(str);
		    	}
		    }
		    System.out.println("懒加载:"+result);
	    }
	    CloseDB();
	    return result;
	}
	

	/**
	 * 删除
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public <T extends BaseBean> boolean deleteObject(T user) throws SQLException
	{
		return false;
		
	}
	
	
	/**
	 * 插入
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public <T extends BaseBean> boolean insertObject(T user) throws SQLException
	{
		return false;
		
	}
	
	/**
	 * 修改
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public <T extends BaseBean> boolean updateObject(T user) throws SQLException
	{
		return false;
		
	}
	
	
	
	

	public Object query(String sql) {
		// TODO Auto-generated method stub
		Statement stat = null;
		try {
			stat = (Statement) this.conn.createStatement();
			return stat.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Proxybean createLazyloadquery(List<Property> lazy_p)
	{
		 Enhancer enhancer=new Enhancer();  
	     enhancer.setSuperclass(Proxybean.class);  
	     SqlQueryLazyLoader sl = new SqlQueryLazyLoader();
	     sl.setLazyinfo(lazy_p);
	     return (Proxybean)enhancer.create(Proxybean.class,sl);  
	}

	
	
	
	
	
}
