package com.lazycare.carcaremaster.data;

/**
 * 系统消息实体类
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class SysMessage {
	private int id;
	private String content;
	private String title;
	private String description;
	private String add_time;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public SysMessage() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

}
