package water.ustc.dao;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.dom4j.DocumentException;

import sc.ustc.dao.BaseBean;
import sc.ustc.dao.BaseDAO;
import sc.ustc.dao.Conversation;
import water.ustc.bean.UserBean;


public class UserDAO extends BaseDAO {

	private Conversation conversation;
	
	
	public UserDAO() {
		// TODO Auto-generated constructor stub
		this.conversation = new Conversation();
	}
	
	//打开数据库
	public void openDB()
	{
		conversation.OpenDB();
	}

	//关闭数据库
    public void closeDBConnection() {
        conversation.CloseDB();
    }
	
	//删
	@Override
	public boolean delete(BaseBean user) {
		// TODO Auto-generated method stub
		try {
			return conversation.deleteObject(user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	//插
	@Override
	public boolean insert(BaseBean user) {
		// TODO Auto-generated method stub
		try {
			return conversation.insertObject(user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	//查
	@Override
	public Object query(BaseBean user) {
		// TODO Auto-generated method stub
		try {
			List<String> result=conversation.getObject(user);
			UserBean u = new UserBean();
			u.setUserName(result.get(0));
			u.setUserPass(result.get(1));
			System.out.println("UserDAO:"+u.getUserName()+","+u.getUserPass());
			return u;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	//改
	@Override
	public boolean update(BaseBean user) {
		// TODO Auto-generated method stub
		try {
			return conversation.updateObject(user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	

	
	
}
