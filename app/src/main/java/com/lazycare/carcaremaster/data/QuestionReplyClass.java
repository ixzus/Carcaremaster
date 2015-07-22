package com.lazycare.carcaremaster.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 车主问题回复实体类
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class QuestionReplyClass {
	private String id = "";
	private String artificer_id = "";// 师傅id
	private String belong = "";
	private String type = ""; // 2 我的回复 ； 1 @我 ； 0 抢单
	private String content = "";
	private String audio = "";
	private String audio_time = "";
	private String is_read = "";
	private String video = "";
	private String photos = "";
	private List<String> mphotos = new ArrayList<String>();
	private String add_time = "";
	private String name = "";// name
	private String member_head = "";// 头像
	private String member_mobile = "";
	private String member_nickname = "";
	private String artificer_head = "";
	private String artificer_name = "";
	private String mdate = "";
	private String answers_count = "";
	private String new_answers_count = "";
	private String question_type = "";// 用户有没有点击过抢的

	public List<String> getMphotos() {
		return mphotos;
	}

	public void setMphotos(List<String> mphotos) {
		this.mphotos = mphotos;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getArtificer_id() {
		return artificer_id;
	}

	public void setArtificer_id(String artificer_id) {
		this.artificer_id = artificer_id;
	}

	public String getBelong() {
		return belong;
	}

	public void setBelong(String belong) {
		this.belong = belong;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getAudio_time() {
		return audio_time;
	}

	public void setAudio_time(String audio_time) {
		this.audio_time = audio_time;
	}

	public String getIs_read() {
		return is_read;
	}

	public void setIs_read(String is_read) {
		this.is_read = is_read;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	public String getPhotos() {
		return photos;
	}

	public void setPhotos(String photos) {
		this.photos = photos;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMember_head() {
		return member_head;
	}

	public void setMember_head(String member_head) {
		this.member_head = member_head;
	}

	public String getMember_mobile() {
		return member_mobile;
	}

	public void setMember_mobile(String member_mobile) {
		this.member_mobile = member_mobile;
	}

	public String getMember_nickname() {
		return member_nickname;
	}

	public void setMember_nickname(String member_nickname) {
		this.member_nickname = member_nickname;
	}

	public String getArtificer_head() {
		return artificer_head;
	}

	public void setArtificer_head(String artificer_head) {
		this.artificer_head = artificer_head;
	}

	public String getArtificer_name() {
		return artificer_name;
	}

	public void setArtificer_name(String artificer_name) {
		this.artificer_name = artificer_name;
	}

	public String getMdate() {
		return mdate;
	}

	public void setMdate(String mdate) {
		this.mdate = mdate;
	}

	public String getAnswers_count() {
		return answers_count;
	}

	public void setAnswers_count(String answers_count) {
		this.answers_count = answers_count;
	}

	public String getNew_answers_count() {
		return new_answers_count;
	}

	public void setNew_answers_count(String new_answers_count) {
		this.new_answers_count = new_answers_count;
	}

	public String getQuestion_type() {
		return question_type;
	}

	public void setQuestion_type(String question_type) {
		this.question_type = question_type;
	}

}
