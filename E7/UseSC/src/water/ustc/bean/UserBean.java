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




