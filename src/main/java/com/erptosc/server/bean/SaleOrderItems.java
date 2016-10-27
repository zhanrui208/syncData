package com.erptosc.server.bean;

public class SaleOrderItems {
	private long itemID;
	private long noteID;
	private long companyID;
	private int prdID;
	private double price;
	private String memo;
	public long getItemID() {
		return itemID;
	}
	public void setItemID(long itemID) {
		this.itemID = itemID;
	}
	public long getNoteID() {
		return noteID;
	}
	public void setNoteID(long noteID) {
		this.noteID = noteID;
	}
	public long getCompanyID() {
		return companyID;
	}
	public void setCompanyID(long companyID) {
		this.companyID = companyID;
	}
	public int getPrdID() {
		return prdID;
	}
	public void setPrdID(int prdID) {
		this.prdID = prdID;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	
	
}
