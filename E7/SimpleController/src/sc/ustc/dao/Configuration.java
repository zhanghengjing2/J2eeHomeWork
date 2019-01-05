package sc.ustc.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.*;
import org.dom4j.io.SAXReader;

import sc.ustc.model.Property;
import sc.ustc.model.User;

public class Configuration {

	private String path;
	private Map<String,Map<String,String>> table = null;

	public void setPath(String path) {
		this.path = path;
		System.out.println("or_mapping:"+this.path);
	}
	
	//从xml中解析出数据库
	public Driver getDBMS() throws DocumentException
	{
		File XMLfile = new File(this.getClass().getResource(this.path).getFile());//获取xml
		Map dbmsInfo = new HashMap<>();//存放数据库驱动相关的键值对
		SAXReader reader = new SAXReader();
		Document document = reader.read(XMLfile);
		Element root = document.getRootElement();
		Element jdbc = root.element("jdbc");//得到jdbc元素
		List<Element> property_list = jdbc.elements("property");
		for(Element property : property_list)
		{
			//Element name_element = property.element("name").getText();
			//String name = name_element.attributeValue("name");
			String name =property.element("name").getText();
			String value =property.element("value").getText();
			dbmsInfo.put(name,value);		
		}
		System.out.println(dbmsInfo);
		
		Driver driver = new Driver();
		driver.setDatabaseName((String) dbmsInfo.get("driver_class"));
		driver.setUrl((String)dbmsInfo.get("url_path"));
		driver.setData_name((String)dbmsInfo.get("db_username"));
		driver.setData_pass((String)dbmsInfo.get("db_userpassword"));
		driver.setDatabase_select(Integer.parseInt((String) dbmsInfo.get("db_select")));
					
		return driver;
	}
	
	public Map<String,Map<String,String>> getTable(){
        if (this.table == null){
        	// 初始化table 读取or_mapping
            table = new HashMap<>();
            SAXReader reader = new SAXReader();
            try {
                Document document = reader.read(new File(this.getClass().getResource(this.path).getFile()));
                Element root = document.getRootElement();// OR-Mappering
                Element class_element = (Element) root.selectSingleNode("class");
                List<Element> child_ele_list =class_element.elements();
                for(Element child_ele : child_ele_list   ){ // name table id property
                    String child_eleText = child_ele.getName();
                    Map<String,String> column_info = new HashMap<>();
                    switch (child_eleText){
                        case "property": // 读取name属性作为table的key
                            String property_key="";
                            for(Element property_element : (List<Element>)child_ele.elements()){ //name column type lazy
                                // 读取property节点下所有属性值,并将name节点值作为table的key
                                String property_name = property_element.getName();
                                if ("name".equals(property_name)){
                                    property_key = property_element.getText();
                                }
                                System.out.println(property_name+" , "+property_element.getText());
                                column_info.put(property_name,property_element.getText());
                            }
                            table.put(property_key,column_info);
                            break;
                        case "id":// 使用tb_primarykey作为table的key
                            for(Element property_id : (List<Element>)child_ele.elements()){
                                column_info.put(property_id.getName(),property_id.getText());
                            }
                            table.put("id",column_info);
                            break;
                        default: // 使用节点名作为table的key
                            column_info.put(child_eleText,child_ele.getText());
                            table.put(child_ele.getName(),column_info);
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return table;
    }
	
    /**
     * 获取表的主键名
     * @return
     */
    public  String getTablePK(){
        return getTable().get("id").get("column");
    }

    /**
     * 根据实体类属性名获取对应的表字段名
     * @param entity_attr
     * @return
     */
    public  String getTableColumn(String entity_attr){
        return getTable().get(entity_attr).get("column");
    }

    /**
     * 获取数据表的名称
     * @return
     */
    public String getTableName() {
        return getTable().get("table").get("table");
    }

    /**
     * 获取对应实体类的类名
     * @return
     */
    public String getEntityName(){
        return getTable().get("name").get("name");
    }

    /**
     * 判断属性是否懒加载
     * @param entity_attr
     * @return
     */
    public Boolean isLazyLoad(String entity_attr){

        Map<String, String> map = getTable().get(entity_attr);
        if (map != null ) {
            switch (map.get("lazy")){
                case "true":
                    return true;
                case "false":
                    return false;
                    default:
                        return false;
            }
        }
        return false;
    }
    
    public List<Property> getProperty() throws DocumentException
    {
    	File XMLfile = new File(this.getClass().getResource(this.path).getFile());//获取xml
		List<Property> propertys = new ArrayList<>();
		SAXReader reader = new SAXReader();
		Document document = reader.read(XMLfile);
		Element root = document.getRootElement();
		Element jdbc = root.element("class");//得到jdbc元素
		List<Element> property_list = jdbc.elements("property");
		for(Element property : property_list)
		{
			String name =property.element("name").getText();
			String column =property.element("column").getText();
			String type =property.element("type").getText();
			String lazy =property.element("lazy").getText();
			Property p = new Property();
			p.setColumn(column);
			p.setLazy(Boolean.parseBoolean(lazy));
			p.setName(name);
			p.setType(type);
			propertys.add(p);
					
		}
		return propertys;
    }
}

