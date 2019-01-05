package sc.ustc.model;

/**
 * User用户数据封装类
 * @author zhang
 *
 */
public class User {

	private int userId;//用户id 
	private String userName;//用户账号
	private String passWord;//用户密码
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
}
