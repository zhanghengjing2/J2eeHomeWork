package sc.ustc.interceptor;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.Element;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import sc.ustc.utils.XmlUtils;


/**
 * Interceptor拦截器的代理类
 * @author zhang
 */
public class InterceptorCglib implements MethodInterceptor {

	private Object target;//业务类对象，供代理方法中进行真正的业务方法调用
	
	//相当于JDK动态代理中的绑定
    public Object getInstance(Object target) {  
        this.target = target;  //给业务对象赋值
        Enhancer enhancer = new Enhancer(); //创建加强器，用来创建动态代理类
        enhancer.setSuperclass(this.target.getClass());  //为加强器指定要代理的业务类（即：为下面生成的代理类指定父类）
        //设置回调：对于代理类上所有方法的调用，都会调用CallBack，而Callback则需要实现intercept()方法进行拦
        enhancer.setCallback(this); 
       // 创建动态代理类对象并返回  
       return enhancer.create(); 
    }
    // 实现回调方法
	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		// TODO Auto-generated method stub
		//获得preAction和afterAction方法
		File controller_xml = new File(this.getClass().getResource("/controller.xml").getFile());//获取xml
		Element interceptor = XmlUtils.getElement(controller_xml,"interceptor","name",(String)args[3]);//获取标签元素
        String class_name = interceptor.attribute("class").getText();//获得类名
        String pre_method_name = interceptor.attribute("predo").getText();//获得preAction
        String aft_method_name = interceptor.attribute("afterdo").getText();//获得afterAction
        
        //反射得到LogInterceptor
        Class clazz = Class.forName(class_name); // 根据类名获得Class
        Object instance =  clazz.newInstance();
        Method Interceptormethod;//方法
        Class[] parameterTypes={String.class,String.class};//方法参数类型
        
		System.out.println("参数为"+args[0]+","+args[1]+",执行方法:"+method.getName()+",代理:"+proxy.toString());
		
		//记录开始时间
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String startTime=df.format(new Date());
		Interceptormethod = instance.getClass().getDeclaredMethod(pre_method_name,parameterTypes);
		//构造参数
		Object[] parameters = {(String)args[0],startTime};
		Interceptormethod.invoke(instance,parameters);
		
		
        String result =(String) proxy.invokeSuper(obj, args); //调用业务类（父类中）的方法
        
        //记录结束时间
      	String endTime=df.format(new Date());
      	//构造参数
      	Object[] parameters2 = {endTime,result};
      	Interceptormethod = instance.getClass().getDeclaredMethod(aft_method_name,parameterTypes);
      	Interceptormethod.invoke(instance,parameters2);
        return result; 
	}
}
