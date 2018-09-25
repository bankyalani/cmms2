package com.nibss.cmms.web;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * {
 *  "sEcho":1,
 *  "iColumns":7,
 *  "sColumns":"",
 *  "iDisplayStart":0,
 *  "iDisplayLength":10,
 *  "amDataProp":[0,1,2,3,4,5,6],
 *  "sSearch":"",
 *  "bRegex":false,
 *  "asSearch":["","","","","","",""],
 *  "abRegex":[false,false,false,false,false,false,false],
 *  "abSearchable":[true,true,true,true,true,true,true],
 *  "iSortingCols":1,
 *  "aiSortCol":[0],
 *  "asSortDir":["asc"],
 *  "abSortable":[true,true,true,true,true,true,true]
 * }
 *
 * @author sodiq
 */
public class DataTablesRequest implements Serializable{

	/**
	 * 
	 */
	public static final long serialVersionUID = -6833651930403424787L;

	@JsonProperty(value = "sEcho")
	private String echo;

	@JsonProperty(value = "iColumns")
	private int numColumns;

	@JsonProperty(value = "sColumns")
	private String columns;

	@JsonProperty(value = "iDisplayStart")
	private int displayStart;

	@JsonProperty(value = "iDisplayLength")
	private int displayLength;

	//has to be revisited for Object type dataProps.
	@JsonProperty(value = "amDataProp")
	private List<Integer> dataProp;

	@JsonProperty(value = "sSearch")
	private String searchQuery;

	@JsonProperty(value = "asSearch")
	private List<String> columnSearches;

	@JsonProperty(value = "bRegex")
	private boolean hasRegex;

	@JsonProperty (value = "abRegex")
	private List<Boolean> regexColumns;

	@JsonProperty (value = "abSearchable")
	private List<Boolean> searchColumns;

	@JsonProperty (value = "iSortingCols")
	private int sortingCols;

	@JsonProperty(value = "aiSortCol")
	private List<Integer> sortedColumns;

	@JsonProperty(value = "asSortDir")
	private List<String> sortDirections;

	@JsonProperty(value = "abSortable")
	private List<Boolean> sortableColumns;

	public String getEcho() {
		return echo;
	}

	public void setEcho(String echo) {
		this.echo = echo;
	}

	public int getNumColumns() {
		return numColumns;
	}

	public void setNumColumns(int numColumns) {
		this.numColumns = numColumns;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public int getDisplayStart() {
		return displayStart;
	}

	public void setDisplayStart(int displayStart) {
		this.displayStart = displayStart;
	}

	public int getDisplayLength() {
		return displayLength;
	}

	public void setDisplayLength(int displayLength) {
		this.displayLength = displayLength;
	}

	public List<Integer> getDataProp() {
		return dataProp;
	}

	public void setDataProp(List<Integer> dataProp) {
		this.dataProp = dataProp;
	}

	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}

	public List<String> getColumnSearches() {
		return columnSearches;
	}

	public void setColumnSearches(List<String> columnSearches) {
		this.columnSearches = columnSearches;
	}

	public boolean isHasRegex() {
		return hasRegex;
	}

	public void setHasRegex(boolean hasRegex) {
		this.hasRegex = hasRegex;
	}

	public List<Boolean> getRegexColumns() {
		return regexColumns;
	}

	public void setRegexColumns(List<Boolean> regexColumns) {
		this.regexColumns = regexColumns;
	}

	public List<Boolean> getSearchColumns() {
		return searchColumns;
	}

	public void setSearchColumns(List<Boolean> searchColumns) {
		this.searchColumns = searchColumns;
	}

	public int getSortingCols() {
		return sortingCols;
	}

	public void setSortingCols(int sortingCols) {
		this.sortingCols = sortingCols;
	}

	public List<Integer> getSortedColumns() {
		return sortedColumns;
	}

	public void setSortedColumns(List<Integer> sortedColumns) {
		this.sortedColumns = sortedColumns;
	}

	public List<String> getSortDirections() {
		return sortDirections;
	}

	public void setSortDirections(List<String> sortDirections) {
		this.sortDirections = sortDirections;
	}

	public List<Boolean> getSortableColumns() {
		return sortableColumns;
	}

	public void setSortableColumns(List<Boolean> sortableColumns) {
		this.sortableColumns = sortableColumns;
	}

}