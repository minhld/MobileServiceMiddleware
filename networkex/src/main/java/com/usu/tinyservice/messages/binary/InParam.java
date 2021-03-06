package com.usu.tinyservice.messages.binary;

import java.io.Serializable;

/**
 * define the input parameter class to hold a pair of key-value
 * @author minhld
 *
 */
public class InParam implements Serializable {
	public static final long serialVersionUID = 1l;
	
	public String param;
	public String type;
	public Object[] values;
	
	public InParam() { } 
	
	public InParam(String param, String type, Object value) {
		this.param = param;
		this.type = type;
		try {
			this.values = new Object[1];
			this.values[0] = value;
		} catch (Exception e) { } 
	}
	
	public InParam(String param, String type, Object[] values) {
		this.param = param;
		this.type = type;
		this.values = values;
	}
}
