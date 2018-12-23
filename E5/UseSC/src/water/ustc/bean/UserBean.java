package water.ustc.bean;

import sc.ustc.model.User;
import water.ustc.dao.UserDAO;

public class UserBean {

	// userId,userName,userPass
	private int userId;
	private String userName;
	private String userPass;
	private String dataTabel = "UserInfo";
	
	public UserBean(User user) {
		// TODO Auto-generated constructor stub
		this.userName = user.getUserName();
		this.userPass = user.getPassWord();
		System.out.println("userbean--name:"+this.userName+",password="+this.userPass);
	}
	//boolean 方法负责处理登录业务
	public boolean signIn()
	{
		String sql = "select * from "+this.dataTabel+" where userName='"+this.userName+"';";
		UserDAO ud = new UserDAO();
		User userdb =(User) ud.query(sql);//执行查询,返回结果
		if(userdb!=null)
			if(userdb.getPassWord().equals(this.userPass))
				return true;
			else
				return false;
		else
			return false;
		/*
		if(this.userName!=null && this.userPass!=null)
			return true;
		else
			return false;
			*/
	}
}
