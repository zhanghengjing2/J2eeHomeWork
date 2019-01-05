package sc.ustc.model;

public class ReqInfo {
	private String Action;
	private String s_time;
	private String e_time;
	private String result;
	
	public ReqInfo(String arg1,String arg2,String arg3,String arg4) {
		// TODO Auto-generated constructor stub
		this.Action=arg1;
		this.s_time=arg2;
		this.e_time=arg3;
		this.result=arg4;
	}
	
	public String getAction() {
		return Action;
	}
	public void setAction(String action) {
		Action = action;
	}
	public String getS_time() {
		return s_time;
	}
	public void setS_time(String s_time) {
		this.s_time = s_time;
	}
	public String getE_time() {
		return e_time;
	}
	public void setE_time(String e_time) {
		this.e_time = e_time;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}

}
