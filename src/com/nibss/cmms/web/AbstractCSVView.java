package com.nibss.cmms.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;
 
/**
 * This class is an abstract CSV view which concrete views must implement.
 * 
 *
 */
public abstract class AbstractCSVView extends AbstractView {
    private String fileName;
 
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
 
    protected void prepareResponse(HttpServletRequest request,
            HttpServletResponse response) {
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                fileName);
        response.setContentType("text/csv");
        response.setHeader(headerKey, headerValue);
    }
 
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
 
     /*   ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);*/
        ICsvListWriter csvWriter = new CsvListWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);
 
        buildCsvDocument(csvWriter, model);
        csvWriter.close();
    }
 
    /**
     * The concrete view must implement this method.
     */
    protected abstract void buildCsvDocument(ICsvListWriter csvWriter,
            Map<String, Object> model) throws Exception;
}