package com.nibss.cmms.web.converters;

import java.beans.PropertyEditorSupport;

import com.nibss.cmms.domain.Industry;
import com.nibss.cmms.service.CompanyService;


public class IndustryEditor extends PropertyEditorSupport {
	 
    private final CompanyService companyService;
 
    public IndustryEditor(CompanyService companyService) {
        this.companyService = companyService;
    }
 
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Industry industry = companyService.getIndustryById(Long.parseLong(text));
		setValue(industry);
		
    }
 
}