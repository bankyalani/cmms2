package com.nibss.cmms.web;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The data tables response.
 * @author sodiq
 */
public class DataTablesResponse <T> implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8034669981931239966L;

	@JsonProperty(value = "iTotalRecords")
    private int totalRecords;

    @JsonProperty(value = "iTotalDisplayRecords")
    private int totalDisplayRecords;

    @JsonProperty(value = "sEcho")
    private String echo;

    @JsonProperty(value = "sColumns")
    private String columns;

    @JsonProperty(value = "aaData")
    private Object[] data;

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public int getTotalDisplayRecords() {
		return totalDisplayRecords;
	}

	public void setTotalDisplayRecords(int totalDisplayRecords) {
		this.totalDisplayRecords = totalDisplayRecords;
	}

	public String getEcho() {
		return echo;
	}

	public void setEcho(String echo) {
		this.echo = echo;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object[] data) {
		this.data = data;
	}

    
}