package sc.ustc.controller;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Element;

import sc.ustc.interceptor.InterceptorCglib;
import sc.ustc.model.Action;
import sc.ustc.utils.XmlUtils;

public class TestMain {

	private static File controller_xml;//xml文件
	private static List<String> actionNames,className,methodNames;//记录action
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		TestMain t= new TestMain();
		//t.handle();
		//t.proxytest();//测试代理方法
		
	}
	
	private void proxytest() throws ReflectiveOperationException {
		// TODO Auto-generated method stub
		/*
		//创建一个实例对象，这个对象是被代理的对象
		//Interceptor log = new LogInterceptor();
		Class c = Class.forName("sc.ustc.controller.LogInterceptor");
		Interceptor log = (Interceptor) c.newInstance();
		//创建一个与代理对象相关联的InvocationHandler
		InterceptorCglib interceptorhandler = new InterceptorCglib<Interceptor>(log);
		//创建一个代理对象stuProxy来代理log，代理对象的每个执行方法都会替换执行Invocation中的invoke方法
		Interceptor interceptorProxy = (Interceptor) Proxy.newProxyInstance(Interceptor.class.getClassLoader(), new Class<?>[]{Interceptor.class}, interceptorhandler);
		//参数数组
		Object[] parameters1={"hello","2018-12-09"};
		Object[] parameters2= {"2018-12-10","success"};
		//记录开始时间
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String startTime=df.format(new Date());
		//代理执行方法
		interceptorProxy.preAction("hello", startTime);
		//记录结束时间
		String endTime=df.format(new Date());
		System.out.println("end:"+endTime);
		//代理执行方法
		interceptorProxy.afterAction(endTime, "success");
		*/
		Action bookFacade=new Action();
		InterceptorCglib  cglib=new InterceptorCglib();  
		Action bookCglib=(Action)cglib.getInstance(bookFacade);  
		//bookCglib.handleAction("hello","success","1231","123");
	}

	public void handle() throws Exception
	{
		controller_xml = new File(this.getClass().getResource("/controller.xml").getFile());//获取xml
		actionNames=XmlUtils.GetActionName(controller_xml,"name");
		for(String action:actionNames)
		{
			System.out.println("name:"+action);
			Element action_element = XmlUtils.getElement(controller_xml,"action","name",action);
			// 判断是否存在interceptor-ref节点
            List<Element> interceptor_ref_elements = action_element.elements("interceptor-ref");
            if (interceptor_ref_elements !=null){
                for(Element interceptor_ref_element : interceptor_ref_elements){

                    String interceptor_name = interceptor_ref_element.attribute("name").getText();
                    Element interceptor = XmlUtils.getElement(controller_xml,"interceptor","name",interceptor_name);
                    String class_name = interceptor.attribute("class").getText();
                    String pre_method_name = interceptor.attribute("predo").getText();
                    String aft_method_name = interceptor.attribute("afterdo").getText();
                    System.out.println("interceptor name is :"+interceptor_name+"\t class name is:"+class_name+"\t pre method name is:"+pre_method_name+"\t after method name is:"+aft_method_name);
                    //doInterceptor(interceptor_name,PRE_EXECUTION);
                    List<String> actionNames=new ArrayList<>();
                    List<String> startTimes=new ArrayList<>();
                    actionNames.add(interceptor_name);
                    startTimes.add("2018-12-8");
                    //反射机制
                    try{

                        
                        Class[] parameterTypes={String.class,String.class};
                        Class clazz = Class.forName(class_name); // 根据类名获得Class
                        Object instance =  clazz.newInstance();
                        Method method = instance.getClass().getDeclaredMethod(pre_method_name,parameterTypes);
            			//参数数组
            			Object[] parameters={"hello","2018-12-09"};
            			//根据获取的构造函数和参数，创建实例
            			method.invoke(instance,parameters);
            
            			Method method2 = instance.getClass().getDeclaredMethod(aft_method_name,parameterTypes);
            			//参数数组
            			Object[] parameters2={"2018-12-10","success"};
            			//根据获取的构造函数和参数，创建实例
            			method2.invoke(instance,parameters2);
            			
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("反射出错");
                    }
                }
            }
			
		}
	}
	private void handleResult(Element action_element, String result, String method_name) {
		// TODO Auto-generated method stub
		String sel_str = MessageFormat.format("result[@name=''{0}'']",result);
		Element result_element = (Element) action_element.selectSingleNode(sel_str);
		String type = result_element.attribute("type").getText();
        String value = result_element.attribute("value").getText();
        System.out.println("type:"+type+",value:"+value);
	}
	private Object doMethod(String class_name, String method_name)  {
        try{

            Class clazz = Class.forName(class_name); // 根据类名获得Class
            Object instance =  clazz.newInstance();
            Method method = instance.getClass().getDeclaredMethod(method_name);
            return method.invoke(instance);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("反射出错");
        }
    }

}
