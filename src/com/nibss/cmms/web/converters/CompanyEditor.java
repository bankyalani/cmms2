package com.nibss.cmms.web.converters;

import java.beans.PropertyEditorSupport;

import com.nibss.cmms.domain.Company;
import com.nibss.cmms.service.CompanyService;

public class CompanyEditor extends PropertyEditorSupport {
	 
    private final CompanyService companyService;
 
    public CompanyEditor(CompanyService companyService) {
        this.companyService = companyService;
    }
 
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Company company = companyService.getCompanyById(Long.parseLong(text));
		setValue(company);
		
    }
 
}