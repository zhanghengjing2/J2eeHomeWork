package sc.ustc.model;

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
	
}
