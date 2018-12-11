package sc.ustc.controller;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Element;

import sc.ustc.model.ReqInfo;
import sc.ustc.test.InterceptorInvocationHandler;
import sc.ustc.utils.XmlUtils;

public class SimpleController extends HttpServlet {
	
	private File controller_xml;//xml文件
	private List<String> actionNames;//记录action
	private String result;//查询xml，反射执行方法后得到的result
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//设置响应类型
		resp.setContentType("text/html;charset=utf-8");
		/*step1. 读取action*/
        String url = req.getRequestURL().toString();// 解析请求的URL，并拆分出action的名称
		String actionStr = url.substring(url.lastIndexOf('/') + 1, url.length() - 3);//action.sc取action
		/*step2. 读取controller.xml并解析*/
		controller_xml = new File(this.getClass().getResource("/controller.xml").getFile());//获取xml
		try {
			actionNames=XmlUtils.GetActionName(controller_xml,"name");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*step3.进行匹配*/
		boolean isActionMatched = false;
		boolean isResultMatched = false;
		boolean isInterceptorMatched = false;//检查是否配置拦截器
		String class_name=null;
		String method_name=null;
		for(String action:actionNames)
		{
			if(action.equals(actionStr))
			{
				isActionMatched=true;//匹配成功，设为true
				//反射执行相应操作
				Element action_element=null;
				try {
					action_element = XmlUtils.getElement(controller_xml,"action","name",action);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//得到Element
				class_name = XmlUtils.getElementValue(action_element,"class");//得到class
		        method_name = XmlUtils.getElementValue(action_element,"method");//得到method
		      
		        //调用响应的方法，使用Java反射机制
		        result = (String) doMethod(class_name, method_name);
		        
		        //检测是否配置了拦截器
		        //isInterceptorMatched=Match();
				try {
					NewHandleInterceptor(action_element,isInterceptorMatched,actionStr,result,method_name,req,resp);
					isInterceptorMatched=true;
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
		        
			}
		} 
		String failed=null;
		if(!isActionMatched)
		{
			failed="不可识 别的 action 请求。";
		}
		//输出流
		PrintWriter out=resp.getWriter();
		out.println("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/><title>SimpleController</title></head>");
		out.println("<body>欢迎使用 SimpleController");
		out.println("<br>actionStr:"+actionStr+"</br>");
		out.println("<br>className:"+class_name+"</br>");
		out.println("<br>methodName:"+method_name+"</br>");
		out.println("<br>callback:"+failed+"</br>");
		out.println("</body></html>");
		out.flush();	
	}
	
	private void NewHandleInterceptor(Element action_element, boolean isMethodActioned, String actionStr, String result2, String method_name, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		// TODO Auto-generated method stub
		
		//解析xml
		List<Element> interceptor_ref_elements = action_element.elements("interceptor-ref");
        if (interceptor_ref_elements !=null){
        	System.out.println("产生拦截");
            for(Element interceptor_ref_element : interceptor_ref_elements){
            	
        		//得到需要的信息
                String interceptor_name = interceptor_ref_element.attribute("name").getText();
                Element interceptor = XmlUtils.getElement(controller_xml,"interceptor","name",interceptor_name);
                String class_name = interceptor.attribute("class").getText();
                String pre_method_name = interceptor.attribute("predo").getText();
                String aft_method_name = interceptor.attribute("afterdo").getText();
                System.out.println("interceptor name is :"+interceptor_name+"\t class name is:"+class_name+"\t pre method name is:"+pre_method_name+"\t after method name is:"+aft_method_name);
                UseProxy(actionStr,result2);//使用动态代理
                /*
                 //反射机制
                //记录开始时间
        		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        		String startTime=df.format(new Date());
        		
                Class c = Class.forName(class_name);
                Class[] parameterTypes={String.class,String.class};
				Object instance = c.newInstance();
				Method method=null;
				method = instance.getClass().getDeclaredMethod(pre_method_name,parameterTypes);
				//参数
				Object[] parameters = {actionStr,startTime};
				//根据获取的构造函数和参数，创建实例
        		method.invoke(instance,parameters);
        		
        		//根据返回值，与xml进行匹配，根据type来决定跳转还是重定向
                handleResult(action_element, result,method_name,req,resp);
                
        		//记录结束时间
        		String endTime=df.format(new Date());
				System.out.println("end:"+endTime);
				
				method = instance.getClass().getDeclaredMethod(aft_method_name,parameterTypes);
				//参数
				Object[] parameters2 = {endTime,result2};
				//根据获取的构造函数和参数，创建实例
        		method.invoke(instance,parameters2);
        		*/
            }
        }
        else
        {
        	System.out.println("未拦截");
        	//根据返回值，与xml进行匹配，根据type来决定跳转还是重定向
            handleResult(action_element, result,method_name,req,resp);
        }
		
	}
	//使用动态代理
	private void UseProxy(String actionStr, String result2) {
		// TODO Auto-generated method stub
		//创建一个实例对象，这个对象是被代理的对象
		Interceptor log = new LogInterceptor();
		//创建一个与代理对象相关联的InvocationHandler
		InterceptorInvocationHandler interceptorhandler = new InterceptorInvocationHandler<Interceptor>(log);
		//创建一个代理对象stuProxy来代理log，代理对象的每个执行方法都会替换执行Invocation中的invoke方法
		Interceptor interceptorProxy = (Interceptor) Proxy.newProxyInstance(Interceptor.class.getClassLoader(), new Class<?>[]{Interceptor.class}, interceptorhandler);
		//记录开始时间
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String startTime=df.format(new Date());
		//代理执行方法
		interceptorProxy.preAction(actionStr, startTime);
		//记录结束时间
		String endTime=df.format(new Date());
		//代理执行方法
		interceptorProxy.afterAction(endTime, result2);
	}
	private void Handleinterceptor(Element action_element, String actionStr, String time, String result ,int type) throws Exception {
		// TODO Auto-generated method stub
		List<Element> interceptor_ref_elements = action_element.elements("interceptor-ref");
        if (interceptor_ref_elements !=null){
            for(Element interceptor_ref_element : interceptor_ref_elements){
                String interceptor_name = interceptor_ref_element.attribute("name").getText();
                Element interceptor = XmlUtils.getElement(controller_xml,"interceptor","name",interceptor_name);
                String class_name = interceptor.attribute("class").getText();
                String pre_method_name = interceptor.attribute("predo").getText();
                String aft_method_name = interceptor.attribute("afterdo").getText();
                System.out.println("interceptor name is :"+interceptor_name+"\t class name is:"+class_name+"\t pre method name is:"+pre_method_name+"\t after method name is:"+aft_method_name);
                //反射机制
                Class c = Class.forName(class_name);
                Class[] parameterTypes={String.class,String.class};
				Object instance = c.newInstance();
				Method method=null;
				
				if(type==0)//0执行preAction
				{
					method = instance.getClass().getDeclaredMethod(pre_method_name,parameterTypes);
					//参数
					Object[] parameters = {actionStr,time};
					//根据获取的构造函数和参数，创建实例
	        		method.invoke(instance,parameters);
				}
				if(type==1)//1执行afeAction
				{
					method = instance.getClass().getDeclaredMethod(aft_method_name,parameterTypes);
					//参数
					Object[] parameters = {time,result};
					//根据获取的构造函数和参数，创建实例
	        		method.invoke(instance,parameters);
				}
                
        		
                
            }
        }
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
	
	private void handleResult(Element action_element, String result, String method_name,HttpServletRequest request,HttpServletResponse response) {
		// TODO Auto-generated method stub
		String sel_str = MessageFormat.format("result[@name=''{0}'']",result);
		Element result_element = (Element) action_element.selectSingleNode(sel_str);
		String type = result_element.attribute("type").getText();
        String value = result_element.attribute("value").getText();
        System.out.println("type:"+type+",value:"+value);
        try {
            request.setAttribute("type",type+":"+ method_name);
            if ("forward".equals(type)) { // 转发到指定页面
                request.getRequestDispatcher(value).forward(request, response);
            } else if ("redirect".equals(type)) { // 重定向到指定页面
                response.sendRedirect(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
