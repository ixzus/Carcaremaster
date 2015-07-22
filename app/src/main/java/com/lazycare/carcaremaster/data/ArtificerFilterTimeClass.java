package com.lazycare.carcaremaster.data;

import java.util.List;

/**
 * 技师繁忙时间
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class ArtificerFilterTimeClass {

	private String showDay;
	private String realDay;
	private List<Temp> time;

	public String getShowDay() {
		return showDay;
	}

	public void setShowDay(String showDay) {
		this.showDay = showDay;
	}

	public List<Temp> getTime() {
		return time;
	}

	public void setTime(List<Temp> time) {
		this.time = time;
	}

	public String getRealDay() {
		return realDay;
	}

	public void setRealDay(String realDay) {
		this.realDay = realDay;
	}

}
