package com.lazycare.carcaremaster.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 车主问题实体类
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class QuestionClass {
	private String id = "";// id
	private String car = "";// 车的款系
	private String type = "";// 类型 服务中 未服务
	private String title = "";// 标题
	private String content = "";// 内容
	private String audio = "";
	private String photos = "";
	private List<String> mphotos=new ArrayList<>();
	private String add_time = "";
	private String nickname = "";
	private String mobile = "";
	private String head = "";
	private String new_answers_count = "";
	private String state = "";
	private String answers_count = "";
	private String mdate = "";
	private String audio_time = "";
	private int unread = 0;

	public int getUnread() {
		return unread;
	}

	public void setUnread(int unread) {
		this.unread = unread;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCar() {
		return car;
	}

	public void setCar(String car) {
		this.car = car;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAudio() {
		return audio;
	}

	public void setAudio(String audio) {
		this.audio = audio;
	}

	public String getPhotos() {
		return photos;
	}

	public void setPhotos(String photos) {
		this.photos = photos;
	}

	public List<String> getMphotos() {
		return mphotos;
	}

	public void setMphotos(List<String> mphotos) {
		this.mphotos = mphotos;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getNew_answers_count() {
		return new_answers_count;
	}

	public void setNew_answers_count(String new_answers_count) {
		this.new_answers_count = new_answers_count;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getAnswers_count() {
		return answers_count;
	}

	public void setAnswers_count(String answers_count) {
		this.answers_count = answers_count;
	}

	public String getMdate() {
		return mdate;
	}

	public void setMdate(String mdate) {
		this.mdate = mdate;
	}

	public String getAudio_time() {
		return audio_time;
	}

	public void setAudio_time(String audio_time) {
		this.audio_time = audio_time;
	}

}
