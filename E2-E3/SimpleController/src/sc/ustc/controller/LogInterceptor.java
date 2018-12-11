package sc.ustc.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


public class LogInterceptor implements Interceptor{
	
	private String ActionNames;
	private String StartTimes;
	private String EndsTimes;
	private String ActionResult;
	
	
	//记录请求的action,访问开始时间
	public void preAction(String actionNames,String startTimes)
	{
		this.ActionNames=actionNames;
		this.StartTimes=startTimes;
		System.out.println("actionNames:"+actionNames+"\t StartTimes:"+startTimes);
	}
	
	//记录访问结束时间，请求返回结果，将信息追加至日志文件log.xml
	public void afterAction(String endsTimes,String actionResult)
	{
		this.ActionResult=actionResult;
		this.EndsTimes=endsTimes;
		System.out.println("log in");
			
				try {
					AppendToXML("/log.xml");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	

	private void AppendToXML(String Filename) throws FileNotFoundException, UnsupportedEncodingException ,IOException {
		// TODO Auto-generated method stub
		//1.判断文件是否存在
		File logxml = new File(this.getClass().getResource(Filename).getFile());
		Document doc=null;
		if(!logxml.exists())
		{
			//创建一个xml文档
			doc = DocumentHelper.createDocument();
			System.out.println("xml创建");
		}
		else
		{
			SAXReader sax = new SAXReader();
			try {
				doc = sax.read(logxml);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("xml读取成功");
			
		}
		//2.创建根对象
		Element root = doc.getRootElement();
		//添加属性
		Element action=root.addElement("action");
		Element name = action.addElement("name");
		Element stime = action.addElement("s-time");
		Element etime = action.addElement("e-time");
		Element result = action.addElement("result");
		name.setText(this.ActionNames);
		stime.setText(this.StartTimes);
		etime.setText(this.EndsTimes);
		result.setText(this.ActionResult);
		//指定文件输出的位置
        FileOutputStream out =new FileOutputStream("src/log.xml");
        // 指定文本的写出的格式：
        OutputFormat format=OutputFormat.createPrettyPrint();   //漂亮格式：有空格换行
        format.setEncoding("UTF-8");
        //1.创建写出对象
        XMLWriter writer=new XMLWriter(out,format);
        //2.写出Document对象
        writer.write(doc);
        //3.关闭流
        writer.close();
		
	}
	private void printname() {
		System.out.println("hello loginterceptor!");
	}

	@Override
	public void Test() {
		// TODO Auto-generated method stub
		try {
	          //假设数钱花了一秒时间
	            Thread.sleep(1000);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
		System.out.println("Hello Interceptor!");
	}
}
