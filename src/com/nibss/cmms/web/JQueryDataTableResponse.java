/*
 * Copyright 2014 Nuno Jacinto
 * Released under the MIT license
 *
 * Date: 2014-06-22
 */

package com.nibss.cmms.web;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nuno
 * @param <E>
 */
@XmlRootElement
public class JQueryDataTableResponse<E extends Serializable> implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3034664684679649053L;
	private int draw; 
    // total number of records
    private int recordsTotal; 
    // total number of records after applying the filtering conditions
    private int recordsFiltered;  
    // configuration, the default is 'data'
    private E []data;

    /**
     * @return the draw
     */
    public int getDraw() {
        return draw;
    }

    /**
     * @param draw the draw to set
     */
    public void setDraw(int draw) {
        this.draw = draw;
    }

    /**
     * @return the recordsTotal
     */
    public int getRecordsTotal() {
        return recordsTotal;
    }

    /**
     * @param recordsTotal the recordsTotal to set
     */
    public void setRecordsTotal(int recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    /**
     * @return the recordsFiltered
     */
    public int getRecordsFiltered() {
        return recordsFiltered;
    }

    /**
     * @param recordsFiltered the recordsFiltered to set
     */
    public void setRecordsFiltered(int recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    /**
     * @return the data
     */
    public E[] getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(E[] data) {
        this.data = data;
    }
    
    /*  */
    protected static final int MIN_BUFFER_SIZE = 65+9+9+9;
    //
    /**
     * 
     * @param draw
     * @param recordsTotal
     * @param recordsFiltered
     * @param jsonData
     * @return 
     */
    public static String createJson(int draw, int recordsTotal, 
            int recordsFiltered, String jsonData){
        StringBuilder buff = new StringBuilder(MIN_BUFFER_SIZE + jsonData.length());
        return buff.append("{ \"draw\": ").append(draw).append(", \"recordsTotal\": ")
                .append(draw).append(", \"recordsFiltered\": ").append(draw)
                .append(", \"elements\": ").append(jsonData).append(" }").toString();
    }
}
