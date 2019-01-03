package sc.ustc.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

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
	public static Element getElement(File file, String element_name,String attr_name, String attr_value) 
			throws Exception
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
	
	public static ByteArrayOutputStream XmlToHtml(String xsl_path,String xml_path){
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = null;
        FileInputStream fis=null;
        FileInputStream fis1=null;
        try {
			fis=new FileInputStream(xsl_path);
			fis1=new FileInputStream(xml_path);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        StreamSource source_xsl = new StreamSource(fis);
        StreamSource source_xml = new StreamSource(fis1);
        try {
            transformer = factory.newTransformer(source_xsl);
            StreamResult output = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            output = new StreamResult(baos);

            transformer.transform(source_xml,output);
            String str = baos.toString();
            System.out.println(str);
            return baos;
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }

}
