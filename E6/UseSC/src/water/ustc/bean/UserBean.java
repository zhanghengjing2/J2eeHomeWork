package water.ustc.bean;

import sc.ustc.dao.BaseBean;
import sc.ustc.model.User;
import water.ustc.dao.UserDAO;



public class UserBean extends BaseBean{
	private Integer userId;
    private String userName;
    private String userPass;

    public UserBean(){}

    public UserBean(String value) {
        super(value);
    }

    public UserBean(String value, String column) {
        super(value, column);
    }

    public boolean signIn() {
    	
        UserDAO userDao = new UserDAO();
        userDao.openDB();
        UserBean user = (UserBean) userDao.query(new UserBean("1"));
        userDao.closeDBConnection();
        return this.userPass.equals(user.getUserPass());
        //return true;
        
    	
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }
	
}




/*
 * 
 * // userId,userName,userPass
	private int userId;
	private String userName;
	private String userPass;
	private String dataTabel = "UserInfo";
	
	public UserBean(){}

    public UserBean(String value) {
    	super(value);
    }
	
	
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
		
		if(this.userName!=null && this.userPass!=null)
			return true;
		else
			return false;
			
	}
 */
