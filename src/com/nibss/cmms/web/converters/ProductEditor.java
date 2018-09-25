package com.nibss.cmms.web.converters;

import java.beans.PropertyEditorSupport;

import com.nibss.cmms.domain.Product;
import com.nibss.cmms.service.ProductService;
import com.nibss.cmms.utils.exception.domain.ServerBusinessException;

public class ProductEditor extends PropertyEditorSupport {
	 
    private final ProductService productService;
 
    public ProductEditor(ProductService productService) {
        this.productService = productService;
    }
 
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Product product;
		try {
			product = productService.getProductById(Long.parseLong(text));
		} catch (ServerBusinessException e) {
			throw new IllegalArgumentException(e);
		}
        setValue(product);
    }
 
}