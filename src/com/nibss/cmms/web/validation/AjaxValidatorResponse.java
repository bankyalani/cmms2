package com.nibss.cmms.web.validation;

import java.util.List;

public class AjaxValidatorResponse {
	private String status;
	private List<String> errorMessageList;
	private List<String> data;

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<String> getErrorMessageList() {
		return this.errorMessageList;
	}
	public void setErrorMessageList(List<String> errorMessageList) {
		this.errorMessageList = errorMessageList;
	}
	public List<String> getData() {
		return data;
	}
	public void setData(List<String> data) {
		this.data = data;
	}
}
