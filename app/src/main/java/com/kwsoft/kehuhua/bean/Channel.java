package com.kwsoft.kehuhua.bean;

public class Channel {
	private String id;
	private String name;
	private int order;
	private String tableId;
	private String pageId;
	private String mainId;
	
	public Channel(){
		
	}
	public Channel(String id,String name, int order, String tableId, String pageId, String mainId) {
		super();
		this.id=id;
		this.name = name;
		this.order = order;
		this.tableId = tableId;
		this.pageId = pageId;
		this.mainId = mainId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getTableId() {
		return tableId;
	}
	public void setTableId(String tableId) {
		this.tableId = tableId;
	}
	public String getPageId() {
		return pageId;
	}
	public void setPageId(String pageId) {
		this.pageId = pageId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMainId() {
		return mainId;
	}
	public void setMainId(String mainId) {
		this.mainId = mainId;
	}
	
}
