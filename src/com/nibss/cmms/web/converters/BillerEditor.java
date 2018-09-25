package com.nibss.cmms.web.converters;

import java.beans.PropertyEditorSupport;

import com.nibss.cmms.domain.Biller;
import com.nibss.cmms.service.BillerService;

public class BillerEditor extends PropertyEditorSupport {
	 
    private final BillerService billerService;
 
    public BillerEditor(BillerService billerService) {
        this.billerService = billerService;
    }
 
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Biller biller = billerService.getBillerById(Long.parseLong(text));
		setValue(biller);
		
    }
 
}