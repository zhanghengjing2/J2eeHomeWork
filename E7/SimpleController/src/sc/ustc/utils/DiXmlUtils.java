package sc.ustc.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class DiXmlUtils {

	
	public static List<String> getBeanName(File file) throws Exception
	{
		List<String> beanNames = new ArrayList<>();
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		
		Element root = document.getRootElement();
		List<Element> bean_list = root.elements("bean");
		for(Element child:bean_list)
		{
			beanNames.add(child.attribute("id").getText());
		}
		return beanNames;
	}
	
	//返回匹配到的bean元素
	public static Element getBeanElement(File file,String action_name) throws Exception
	{
		Element bean = null;
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		
		Element root = document.getRootElement();
		List<Element> bean_list = root.elements("bean");
		for(Element child:bean_list)
		{
			if(child.attribute("id").getText().equals(action_name))
			{
				bean = child;
			}
		}
		return bean;
	}
	
	//得到元素类的属性的值
	public static String getElementValue(Element element, String attrName)
	{
		if (element!=null)
		{
			return element.attribute(attrName).getText();
	    }
		else
		{
			throw new RuntimeException("传入的元素节点为null!");
	    }	
	}
	
	public static Element getBeanbyName(File file,String beanName) throws Exception
	{
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		
		Element root = document.getRootElement();
		List<Element> bean_list = root.elements("bean");
		for(Element child:bean_list)
		{
			if(child.attribute("id").getText().equals(beanName))
			{
				return child;
			}
		}
		return null;
		
	}
}
