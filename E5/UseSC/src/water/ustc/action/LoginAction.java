package water.ustc.action;

import sc.ustc.model.User;
import water.ustc.bean.UserBean;

public class LoginAction {

	public String handleLogin(User user)
	{
		UserBean userbean = new UserBean(user);
		boolean islogined =false;
		System.out.println("username:"+user.getUserName()+",userpass:"+user.getPassWord());
		islogined=userbean.signIn();
		if(islogined)
			return "success";
		else
			return "failure";
	}
}

