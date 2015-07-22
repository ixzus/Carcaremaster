package com.lazycare.carcaremaster.data;

/**
 * 首页菜单类
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class MenuClass {
	private String name;
	private String icon;
	private String model;
	private String action;
	private String url;
//	private int unread = 0;

	// public int getUnread() {
	// return unread;
	// }
	//
	// public void setUnread(int unread) {
	// this.unread = unread;
	// }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
