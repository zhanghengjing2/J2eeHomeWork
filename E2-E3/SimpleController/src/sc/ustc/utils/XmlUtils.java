package sc.ustc.utils;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XmlUtils {
	
	//解析xml文件的ActionName
	public static List<String> GetActionName(File file, String attribute) throws Exception
	{
		List<String> actionNames = new ArrayList<>();
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		Element root = document.getRootElement();
		Element controller = root.element("controller");
		List<Element> action_list = controller.elements("action");
		for(Element child:action_list)
		{
			actionNames.add(child.attribute(attribute).getText());
		}
		return actionNames;
		
	}
	//解析xml文件的InterceptorName
	public static String GetInterceptorName(File file, String attribute) throws Exception
	{
		List<String> actionNames = new ArrayList<>();
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		Element root = document.getRootElement();
		Element interceptor = root.element("interceptor");
		return interceptor.attribute(attribute).getText();
		
	}	
	//解析xml特定节点的属性，得到该element
	public static Element getElement(File file, String element_name,String attr_name, String attr_value) throws Exception
	{
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		Element root = document.getRootElement();
        String sel_str = MessageFormat.format("//{0}[@{1}=''{2}'']", element_name, attr_name, attr_value);
        return (Element) root.selectSingleNode(sel_str);
	}
	
	//获取该element的值
	public static String getElementValue(Element element, String attrName)
	{
		if (element!=null){

            return element.attribute(attrName).getText();
        }else {
            throw new RuntimeException("传入的元素节点为null!");
        }
		
	}

}
