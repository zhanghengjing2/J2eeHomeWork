package sc.ustc.controller;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
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

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import sc.ustc.interceptor.InterceptorCglib;
import sc.ustc.model.Action;
import sc.ustc.model.ReqInfo;
import sc.ustc.model.User;
import sc.ustc.utils.DiXmlUtils;
import sc.ustc.utils.XmlUtils;

public class SimpleController extends HttpServlet {
	
	private File controller_xml;//xml文件
	private List<String> actionNames;//记录action
	private String result;//查询xml，反射执行方法后得到的result
	private File bean_xml=new File(this.getClass().getResource("/di.xml").getFile());//获取xml;
	//private List<Action> action_list =new ArrayList<>();
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
        System.out.println("URL:"+url);
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
		boolean isActionMatched = false;//用来标记action是否匹配
		boolean isResultMatched = false;
		boolean isInterceptorMatched = false;//检查是否配置拦截器
		String class_name=null;
		String method_name=null;
		for(String action:actionNames)
		{
			if(action.equals(actionStr))//找到了对应的Action
			{
				isActionMatched=true;//匹配成功，设为true
				//查看di.xml中有无与Action同名的bean
				boolean isBeanMatched= Findbean(action);
				//得到action元素
				Element action_element=null;
				try {
					action_element = XmlUtils.getElement(controller_xml,"action","name",action);//得到action元素	
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				class_name = XmlUtils.getElementValue(action_element,"class");//得到class
		        method_name = XmlUtils.getElementValue(action_element,"method");//得到method
		        //将URL中的其他信息构造成User
		        User user = CreateUser(req);
		        
		        //检测该action下是否配置了拦截器
		        isInterceptorMatched=DetectInterceptor(action_element);
				
		        if(isBeanMatched)//若匹配到同名的bean
				{
		        	//得到该bean元素
		        	Element bean=null;
		        	try {
						bean = DiXmlUtils.getBeanElement(this.bean_xml, action);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        	//得到bean对应的类名
		        	String bean_class = DiXmlUtils.getElementValue(bean, "class");
					//检测是否有依赖
		        	boolean isRef = FindBeanRef(bean);
		        	if(isRef)//如果有依赖
		        	{
		        		//找到依赖的bean
		        		Element bean_ref;
		        		try {
							bean_ref = getBeanRef(bean);//得到依赖的bean
							String bean_ref_class = DiXmlUtils.getElementValue(bean_ref, "class");//得到依赖的bean的类名
							//使用java内省来获取LoginAction对象
							Object ActionInstance =UseIntrospector(bean_class,bean_ref_class);
							//反射执行handleLogin
							Class[] parameterTypes={User.class};//参数类
				            Method method = ActionInstance.getClass().getDeclaredMethod(method_name , parameterTypes);//获取方法
				            Object[] parameters = {user};//要传递的参数
				            result = (String) method.invoke(ActionInstance, parameters);//执行方法
				            handleResult(action_element, result,method_name,req,resp);//根据result，采用forward或者redirect
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        	}
		        	else//无依赖
		        	{
		        		//根据class，执行相应的method,然后返回结果给result
				        result = (String) doMethod(bean_class, method_name,user);
				        handleResult(action_element, result,method_name,req,resp);//根据result，采用forward或者redirect
		        	}
		        	
				}
		        else //没找到的话，直接反射,分发请求
		        {
		        	//根据class，执行相应的method,然后返回结果给result
			        result = (String) doMethod(class_name, method_name,user);
			        handleResult(action_element, result,method_name,req,resp);//根据result，采用forward或者redirect
		        }
				
		        /*
		        //如果配置了拦截器，使用代理来执行method
		        if(isInterceptorMatched)
		        {
		        	//step1.得到拦截器的类名
		        	String interceptor_name = action_element.element("interceptor-ref").attribute("name").getText();
					//step2.启动代理
					Action action1=new Action();
					InterceptorCglib  cglib=new InterceptorCglib();  
					Action actionCglib=(Action)cglib.getInstance(action1);  
					result=actionCglib.handleAction(action,class_name,method_name,interceptor_name,user);//得到action的返回结果
					handleResult(action_element, result,method_name,req,resp);//根据result，采用forward或者redirect
		        }
		        else if(!isInterceptorMatched)//如果没有配置拦截器，直接执行method。
		        {
			        //根据class，执行相应的method,然后返回结果给result
			        result = (String) doMethod(class_name, method_name,user);
			        handleResult(action_element, result,method_name,req,resp);//根据result，采用forward或者redirect
		        }*/
		        
		        
			}
		} 
		String failed=null;
		if(!isActionMatched)//匹配失败时
		{
			failed="不可识 别的 action 请求。";
			//输出流
			PrintWriter out=resp.getWriter();
			out.println("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/><title>SimpleController</title></head>");
			out.println("<body>欢迎使用 SimpleController");
			//out.println("<br>actionStr:"+actionStr+"</br>");
			//out.println("<br>className:"+class_name+"</br>");
			//out.println("<br>methodName:"+method_name+"</br>");
			out.println("<br>callback:"+failed+"</br>");
			out.println("</body></html>");
			out.flush();	
		}
		
	}
	/**
	 * //使用java内省来启动LoginAction
	 * @param beanInstance
	 * @param beanRefInstance
	 * @throws Exception 
	 */
	private Object UseIntrospector(String beanclass, String beanRefclass) throws Exception {
		// TODO Auto-generated method stub
		Class<?> cl = Class.forName(beanclass);
		// 在bean上进行内省
		BeanInfo beaninfo = Introspector.getBeanInfo(cl, Object.class);
		PropertyDescriptor[] pro = beaninfo.getPropertyDescriptors();
		//实例化一个beanclass
		Object beanInstance = getReflectClass(beanclass);
		System.out.print(beanclass+"的属性有:");
		for (PropertyDescriptor pr : pro) {
			System.out.print(pr.getName() + " ");
		}
		System.out.println("");
		for (PropertyDescriptor pr : pro) {
			// 获取beal的set方法
			Method writeme = pr.getWriteMethod();
			if (pr.getName().equals("ub")) {
				// 执行方法
				//实例化被依赖的bean
				Object beanRefInstance = getReflectClass(beanRefclass);
				writeme.invoke(beanInstance, beanRefInstance);
			} 
		}
		Method method = beanInstance.getClass().getDeclaredMethod("Print");//获取方法
        method.invoke(beanInstance);//执行方法
        return beanInstance;
		
		
	}
	//找到依赖的bean
	private Element getBeanRef(Element bean) throws Exception {
		// TODO Auto-generated method stub
		List<Element> beanRef_list = bean.elements("field");
		List<String> beanNames = new ArrayList<>();
		for(Element e : beanRef_list)
		{
			String beanName = DiXmlUtils.getElementValue(e, "bean-ref");
			Element REfbean = DiXmlUtils.getBeanbyName(this.bean_xml, beanName);
			return REfbean;
		}
		return null;
	}
	//判断是否有依赖
	private boolean FindBeanRef(Element bean) {
		// TODO Auto-generated method stub
		boolean flag=false;
		List<Element> beanRef_list = bean.elements("field");
		if(beanRef_list.isEmpty())
			flag=false;
		else
			flag=true;
		return flag;
	}
	/**
	 * 查看di.xml中有无与Action同名的bean
	 * @param action
	 * @return
	 * @throws DocumentException 
	 */
	private boolean Findbean(String action)  {
		// TODO Auto-generated method stub
		List<String> actions = new ArrayList<>();
		boolean flag=false;
		try {
			actions = DiXmlUtils.getBeanName(this.bean_xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(String name:actions)
		{
			if(action.equals(name))
				flag= true;
			else
				flag= false;
		}
		return flag;	
	}
	/**
	 * 解析URL,得到用户信息
	 * @param requset
	 * @return
	 */
	private User CreateUser(HttpServletRequest req) {
		// TODO Auto-generated method stub
		User user = new User();
		String id_str = (req.getParameter("id"));
        if (id_str != null) {
            user.setUserId(Integer.parseInt(id_str));
        }
        String username = req.getParameter("username");
        if (username != null) {
            user.setUserName(username);
        }
        String password = req.getParameter("password");
        if (password != null) {
            user.setPassWord(password);
        }
		return user;
	}
	/**
	 * 检测该action下是否配置了拦截器
	 * @param action_element
	 * @return 配置了拦截器返回true.否则返回false
	 */
	private boolean DetectInterceptor(Element action_element) {
		// TODO Auto-generated method stub
		List<Element> interceptor_ref_elements = action_element.elements("interceptor-ref");
		if (interceptor_ref_elements !=null)
			return true;
		else
			return false;
	}
	
	/*
	 //不使用代理，使用反射执行LogInterceptor
	private void HandleInterceptor(Element action_element, boolean isMethodActioned, String actionStr, String result2, String method_name, HttpServletRequest req, HttpServletResponse resp) throws Exception {
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
                //UseProxy(actionStr,result2,class_name);//使用动态代理
                handleResult(action_element, result,method_name,req,resp);
                
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
                result = (String) doMethod(class_name, method_name);
                
        		//记录结束时间
        		String endTime=df.format(new Date());
				System.out.println("end:"+endTime);
				
				method = instance.getClass().getDeclaredMethod(aft_method_name,parameterTypes);
				//参数
				Object[] parameters2 = {endTime,result2};
				//根据获取的构造函数和参数，创建实例
        		method.invoke(instance,parameters2);
        		
            }
        }
        else
        {
        	System.out.println("未拦截");
        	//根据返回值，与xml进行匹配，根据type来决定跳转还是重定向
            handleResult(action_element, result,method_name,req,resp);
        }
		
	}*/
	
	
	/**
	 * 反射构造
	 * return 对象实例
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public Object getReflectClass(String class_name) throws Exception
	{
		Class clazz = Class.forName(class_name); // 根据类名获得Class
		Object instance =  clazz.newInstance();//获取实例对象
		return instance;
	}
	
	/**
	 * 执行反射方法
	 * @param class_name
	 * @param method_name
	 * @return object
	 */
	private Object doMethod(String class_name, String method_name,User user)  {
        try{
            Class clazz = Class.forName(class_name); // 根据类名获得Class
            Class[] parameterTypes={User.class};//参数类型
            Object instance =  clazz.newInstance();//获取实例对象
            Method method = instance.getClass().getDeclaredMethod(method_name , parameterTypes);//获取方法
            Object[] parameters = {user};//要传递的参数
            return method.invoke(instance, parameters);//执行方法
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("反射出错");
        }
    }
	/**
	 * 根据result来执行forward或者redirect
	 * @param action_element
	 * @param result
	 * @param method_name
	 * @param request
	 * @param response
	 */
	private void handleResult(Element action_element, String result, String method_name,
			HttpServletRequest request,HttpServletResponse response) {
		// TODO Auto-generated method stub
		String sel_str = MessageFormat.format("result[@name=''{0}'']",result);//表达式
		Element result_element = (Element) action_element.selectSingleNode(sel_str);//根据表达式，得到result元素
		String type = result_element.attribute("type").getText();//得到type
        String value = result_element.attribute("value").getText();//得到value
        System.out.println("type:"+type+",value:"+value);
        try {
        	if(value.endsWith(".xml"))
        	{
        		System.out.println("start!!!");
        		String xmlpath = this.getServletContext().getRealPath("/pages/success_view.xml");
        		String xslpath = this.getServletContext().getRealPath("/pages/success_view.xsl");
        		//System.out.println(xmlpath);
        		String content =XmlUtils.XmlToHtml(xslpath, xmlpath).toString();
        		PrintWriter out=response.getWriter();
        		out.println(content);
        		out.flush();
        	}
        	else
        	{
        		request.setAttribute("type",type+":"+ method_name);
                if ("forward".equals(type)) { // 转发到指定页面
                    request.getRequestDispatcher(value).forward(request, response);
                } else if ("redirect".equals(type)) { // 重定向到指定页面
                    response.sendRedirect(value);
                }
        	}
            
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
