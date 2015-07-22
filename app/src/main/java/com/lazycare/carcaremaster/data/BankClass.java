package com.lazycare.carcaremaster.data;

import java.io.Serializable;

/**
 * 银行卡
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class BankClass implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4031142408223122072L;
	private String id;
	private String bank;
	private String tail;
	private String icon;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getTail() {
		return tail;
	}

	public void setTail(String tail) {
		this.tail = tail;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}
