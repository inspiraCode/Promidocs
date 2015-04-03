package com.inspiracode.promidocs.dto;

import java.sql.Date;

public class Tracker {
	private long id = -1;
	private String fileName = "";
	private String status = "";
	private Date uploadedOn;
	private String sourceLocation = "";
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the uploadedOn
	 */
	public Date getUploadedOn() {
		return uploadedOn;
	}
	/**
	 * @param uploadedOn the uploadedOn to set
	 */
	public void setUploadedOn(Date uploadedOn) {
		this.uploadedOn = uploadedOn;
	}
	/**
	 * @return the sourceLocation
	 */
	public String getSourceLocation() {
		return sourceLocation;
	}
	/**
	 * @param sourceLocation the sourceLocation to set
	 */
	public void setSourceLocation(String sourceLocation) {
		this.sourceLocation = sourceLocation;
	}
	
	
}
