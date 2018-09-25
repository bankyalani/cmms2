package com.nibss.cmms.utils;

public class OptionBean {
	private String value;
	private String id;
	
	public OptionBean(String id, String value){
		this.value=value;
		this.id=id;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

}
