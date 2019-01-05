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

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import sc.ustc.dao.BaseBean;
import sc.ustc.dao.Configuration;
import sc.ustc.dao.Conversation;
import sc.ustc.dao.Driver;
import sc.ustc.interceptor.InterceptorCglib;
import sc.ustc.model.Action;
import sc.ustc.model.Property;
import sc.ustc.utils.DiXmlUtils;
import sc.ustc.utils.XmlUtils;

public class TestMain {

	private static File controller_xml;//xml文件
	private static List<String> actionNames,className,methodNames;//记录action
	private File bean_xml = new File(this.getClass().getResource("/di.xml").getFile());//获取xml
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		TestMain t= new TestMain();
		//得到该bean元素
    	
    	Element bean=null;
    	try {
			bean = DiXmlUtils.getBeanElement(t.bean_xml, "login");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//找到依赖的bean
		Element bean_ref;
		try {
			bean_ref =t.getBeanRef(bean);//得到依赖的bean
			String bean_ref_class = DiXmlUtils.getElementValue(bean_ref, "class");//得到依赖的bean的类名
			System.out.println(bean_ref_class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	
	
	

}
