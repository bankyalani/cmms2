package com.nibss.cmms.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EmailMessage {
    private List<String> to = new ArrayList<String>();
    private List<String> cc= new ArrayList<String>();
    private List<String> bcc= new ArrayList<String>();
    private String subject;
    private String from;
    private String text;
    private String attachment;
    private String attachmentName;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    public void setCc(String ccStr) {
		if(ccStr!=null && !"".equals(ccStr.trim())){
			cc=Arrays.asList(ccStr.split(","));
		}
		
	}
	
	
	public void setTo(String toStr) {
		
		if(toStr!=null && !"".equals(toStr.trim())){
			to=Arrays.asList(toStr.split(","));
		}
		
	}
	public void setBcc(String bccStr) {
		if(bccStr!=null && !"".equals(bccStr.trim())){
			bcc=Arrays.asList(bccStr.split(","));
		}
		
	}
	
	public List<String> getCc() {
		return cc;
	}
	
	public List<String> getTo() {
		return to;
	}
	
	public List<String> getBcc() {
		return bcc;
	}
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmailMessage that = (EmailMessage) o;

        if (bcc != null ? !bcc.equals(that.bcc) : that.bcc != null) return false;
        if (cc != null ? !cc.equals(that.cc) : that.cc != null) return false;
        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        if (subject != null ? !subject.equals(that.subject) : that.subject != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (to != null ? !to.equals(that.to) : that.to != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (to != null ? to.hashCode() : 0);
        result = 31 * result + (cc != null ? cc.hashCode() : 0);
        result = 31 * result + (bcc != null ? bcc.hashCode() : 0);
        result = 31 * result + (subject != null ? subject.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }


    public String toString() {
        return "EmailMessage{" +
                "to='" + to + '\'' +
                ", cc='" + cc + '\'' +
                ", bcc='" + bcc + '\'' +
                ", subject='" + subject + '\'' +
                ", from='" + from + '\'' +
                ", attachmentName='" + attachmentName + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	/**
	 * @return the attachmentName
	 */
	public String getAttachmentName() {
		return attachmentName;
	}

	/**
	 * @param attachmentName the attachmentName to set
	 */
	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}
}


