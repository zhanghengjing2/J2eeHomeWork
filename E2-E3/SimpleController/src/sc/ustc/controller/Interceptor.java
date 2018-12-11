package sc.ustc.controller;

public interface Interceptor {

	void Test();
	void preAction(String actionNames,String startTimes);
	void afterAction(String endsTimes,String actionResult);
	
}
