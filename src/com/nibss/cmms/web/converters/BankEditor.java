package com.nibss.cmms.web.converters;

import java.beans.PropertyEditorSupport;

import com.nibss.cmms.domain.Bank;
import com.nibss.cmms.service.BankService;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;

public class BankEditor extends PropertyEditorSupport {
	 
    private final BankService bankService;
    private Bank bank;
 
    public BankEditor(BankService bankService) {
        this.bankService = bankService;
    }
    
 
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        
		try {
			bank = bankService.getBankByBankCode(String.valueOf(text));
		} catch (ServerBusinessException e) {
			throw new IllegalArgumentException(e);
		}
        setValue(bank);
    }
 
}