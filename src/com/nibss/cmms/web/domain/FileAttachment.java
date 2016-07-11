package com.nibss.cmms.web.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

public class FileAttachment implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2884311683438025004L;

	@NotNull(message="File extension is required")
	private String fileExtension;
	
	@NotBlank(message="Base 64 encoded string of file is required")
	private String fileBase64EncodedString;

	
	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public String getFileBase64EncodedString() {
		return fileBase64EncodedString;
	}

	public void setFileBase64EncodedString(String fileEncodedString) {
		this.fileBase64EncodedString = fileEncodedString;
	}
	
}
