package com.nibss.cmms.web;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


/**

 * @author sodiq
 */
public class nDataTablesRequest implements Serializable{

	/**
	 * 
	 */
	public static final long serialVersionUID = -6833651930403424787L;

	@JsonProperty(value = "draw")
	private int draw;
	
	@JsonProperty(value = "start")
	private int start;

	@JsonProperty(value = "length")
	private int length;

	@JsonProperty(value = "columns")
	private List<String> columns;


	@JsonProperty(value = "search")
	private List<String> search;


	@JsonProperty(value = "order")
	private List<String> order;

	
	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public List<String> getSearch() {
		return search;
	}

	public void setSearch(List<String> search) {
		this.search = search;
	}

	public List<String> getOrder() {
		return order;
	}

	public void setOrder(List<String> order) {
		this.order = order;
	}




}