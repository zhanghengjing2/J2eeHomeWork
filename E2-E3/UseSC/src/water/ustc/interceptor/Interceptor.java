package water.ustc.interceptor;

public interface Interceptor {
	void preAction(String actionNames,String startTimes);
	void afterAction(String endsTimes,String actionResult);
}
