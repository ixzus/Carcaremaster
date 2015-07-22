package com.lazycare.carcaremaster.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 预约的类，请根据实际情况修改
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class AppointmentClass implements Parcelable {
	private String id;
	private String mobile;
	private String add_time;
	private String book_time;
	private String service;
	private String car;
	private String consignee;// 车主姓名
	private String order_state;
	private String service_state;
	private String head;
	private String mmobile;
	private String pay_state="";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public String getBook_time() {
		return book_time;
	}

	public void setBook_time(String book_time) {
		this.book_time = book_time;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getCar() {
		return car;
	}

	public void setCar(String car) {
		this.car = car;
	}

	public String getConsignee() {
		return consignee;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}

	public String getOrder_state() {
		return order_state;
	}

	public void setOrder_state(String order_state) {
		this.order_state = order_state;
	}

	public String getService_state() {
		return service_state;
	}

	public void setService_state(String service_state) {
		this.service_state = service_state;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getMmobile() {
		return mmobile;
	}

	public void setMmobile(String mmobile) {
		this.mmobile = mmobile;
	}

	public String getPay_state() {
		return pay_state;
	}

	public void setPay_state(String pay_state) {
		this.pay_state = pay_state;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.id);
		dest.writeString(this.mobile);
		dest.writeString(this.add_time);
		dest.writeString(this.book_time);
		dest.writeString(this.service);
		dest.writeString(this.car);
		dest.writeString(this.consignee);
		dest.writeString(this.order_state);
		dest.writeString(this.service_state);
		dest.writeString(this.head);
		dest.writeString(this.mmobile);
		dest.writeString(this.pay_state);
	}

	public AppointmentClass() {
	}

	protected AppointmentClass(Parcel in) {
		this.id = in.readString();
		this.mobile = in.readString();
		this.add_time = in.readString();
		this.book_time = in.readString();
		this.service = in.readString();
		this.car = in.readString();
		this.consignee = in.readString();
		this.order_state = in.readString();
		this.service_state = in.readString();
		this.head = in.readString();
		this.mmobile = in.readString();
		this.pay_state = in.readString();
	}

	public static final Parcelable.Creator<AppointmentClass> CREATOR = new Parcelable.Creator<AppointmentClass>() {
		public AppointmentClass createFromParcel(Parcel source) {
			return new AppointmentClass(source);
		}

		public AppointmentClass[] newArray(int size) {
			return new AppointmentClass[size];
		}
	};
}
