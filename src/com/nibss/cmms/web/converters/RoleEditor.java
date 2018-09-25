package com.nibss.cmms.web.converters;

import java.beans.PropertyEditorSupport;

import com.nibss.cmms.domain.Role;
import com.nibss.cmms.service.RoleService;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;

public class RoleEditor extends PropertyEditorSupport {
	 
    private final RoleService roleService;
 
    public RoleEditor(RoleService roleService) {
        this.roleService = roleService;
    }
 
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Role role;
		try {
			role = roleService.getRoleById(Long.parseLong(text));
		} catch (ServerBusinessException e) {
			throw new IllegalArgumentException(e);
		}
        setValue(role);
    }
 
}