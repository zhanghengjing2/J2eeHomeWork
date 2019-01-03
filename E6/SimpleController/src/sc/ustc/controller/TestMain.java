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

import sc.ustc.dao.BaseBean;
import sc.ustc.dao.Configuration;
import sc.ustc.dao.Conversation;
import sc.ustc.dao.Driver;
import sc.ustc.interceptor.InterceptorCglib;
import sc.ustc.model.Action;
import sc.ustc.model.Property;
import sc.ustc.utils.XmlUtils;

public class TestMain {

	private static File controller_xml;//xml文件
	private static List<String> actionNames,className,methodNames;//记录action
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		TestMain t= new TestMain();
		
		
		Conversation conver = new Conversation();
		conver.OpenDB();
		conver.getObject(new BaseBean("1"));
		conver.CloseDB();
		
		
		//t.handle();
		//t.proxytest();//测试代理方法
		
		
//		Configuration c = new Configuration();
//		c.setPath("/or_mapping.xml");
//		System.out.println(c.getTableColumn("userName"));
//		List<Property> demo = c.getProperty();
//		for(Property d : demo)
//		{
//			System.out.println(d.getName()+" "+d.getColumn()+" "+d.getType()+" "+d.isLazy());
//		}
//		List<Property> lazyload = new ArrayList<>();
//		List<Property> notlazyload = new ArrayList<>();
//		for(Property p : demo)
//		{
//			if(p.isLazy())
//				lazyload.add(p);
//			else
//				notlazyload.add(p);
//		}
//		String info = null ;
//		for(Property p:notlazyload)
//		{
//			info = info+","+p.getName();
//		}
//		info =info.substring(5, info.length());
//		System.out.println(info);
		
	}
	
	

}
