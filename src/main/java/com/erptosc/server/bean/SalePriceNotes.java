package com.erptosc.server.bean;

import java.util.Date;

public class SalePriceNotes {
	private long noteID;
	private Date dateNote ;
	private String companyID;
	public long getNoteID() {
		return noteID;
	}
	public void setNoteID(long noteID) {
		this.noteID = noteID;
	}
	public Date getDateNote() {
		return dateNote;
	}
	public void setDateNote(Date dateNote) {
		this.dateNote = dateNote;
	}
	public String getCompanyID() {
		return companyID;
	}
	public void setCompanyID(String companyID) {
		this.companyID = companyID;
	}
	
	
}
