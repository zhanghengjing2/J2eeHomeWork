package water.ustc.action;

import sc.ustc.model.User;
import water.ustc.bean.UserBean;

public class LoginAction {
	
	private UserBean ub;

	public String handleLogin(User user)
	{
		//UserBean userbean = getUb();
		//UserBean userbean = new UserBean();
//		if(this.ub==null)
//		{
//			System.out.println("传递失败!");
//		}
//		else
//		{
//			this.ub.setUserName(user.getUserName());
//			this.ub.setUserPass(user.getPassWord());
//		}
		this.ub.setUserName(user.getUserName());
		this.ub.setUserPass(user.getPassWord());
		boolean islogined =false;
		System.out.println("username:"+user.getUserName()+",userpass:"+user.getPassWord());
		islogined=this.ub.signIn();
		if(islogined)
			return "success";
		else
			return "failure";
			
		//return "success";
			
		
	}
	
	public void Print()
	{
		if(this.ub!=null)
			System.out.println("有值");
		else
			System.out.println("传递失败!");
	}

	public UserBean getUb() {
		System.out.println("有Userbean");
		return ub;
	}

	public void setUb(UserBean ub) {
		this.ub = ub;
	}

	
}

