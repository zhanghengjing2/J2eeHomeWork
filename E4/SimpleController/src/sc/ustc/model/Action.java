package sc.ustc.model;

import java.lang.reflect.Method;

/**
 * Action类
 * @author zhang
 *
 */
public class Action {
	private String name;//Action的名字
	private String classpath;//该Action指向的类的路径
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getClasspath() {
		return classpath;
	}
	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}
	
	public String handleAction(String action,String class_name,String method_name,String interceptor_name)
	{
		System.out.println("执行Action");
		try{
            Class clazz = Class.forName(class_name); // 根据类名获得Class
            Object instance =  clazz.newInstance();
            Method method = instance.getClass().getDeclaredMethod(method_name);
            return (String) method.invoke(instance);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("反射出错");
        }
	}
	
}
