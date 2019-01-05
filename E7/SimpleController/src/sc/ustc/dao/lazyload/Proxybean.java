package sc.ustc.dao.lazyload;

import java.util.List;

import sc.ustc.model.Property;

public class Proxybean {

	private List<String> lazyinfo;//存放要查询的信息
	public Proxybean(List<String> info) {
		// TODO Auto-generated constructor stub
		this.lazyinfo=info;
	}
	public List<String> getLazyinfo() {
		return lazyinfo;
	}
	public Proxybean() {
		// TODO Auto-generated constructor stub
	}
}
