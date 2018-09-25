package com.nibss.cmms.web;

public class SearchMapper {

	private int itemIndex;
	
	private String searchItem;
	
	private String type;
	
	public SearchMapper(int index, String item, String type){
		this.itemIndex=index;
		this.searchItem=item;
		this.type=type;
	}

	public int getItemIndex() {
		return itemIndex;
	}

	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}

	public String getSearchItem() {
		return searchItem;
	}

	public void setSearchItem(String searchItem) {
		this.searchItem = searchItem;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
