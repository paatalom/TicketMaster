package com.ticketmaster.portal.webui.client.utils;

import java.util.Map;

import com.smartgwt.client.data.Criteria;

public class ReportParams {
	String ds_name;
	String ds_operation;
	String field_names;
	Map<String, String> headers;
	Map<String, String> captions;
	Criteria criteria;

	public ReportParams(String ds_name, String ds_operation, String field_names, Criteria criteria,
			Map<String, String> captions, Map<String, String> headers) {
		super();
		this.ds_name = ds_name;
		this.ds_operation = ds_operation;
		this.field_names = field_names;
		this.captions = captions;
		this.criteria = criteria;
		this.headers = headers;
	}

	public ReportParams(String ds_name, String ds_operation, String field_names, Criteria criteria,
			Map<String, String> captions) {
		super();
		this.ds_name = ds_name;
		this.ds_operation = ds_operation;
		this.field_names = field_names;
		this.captions = captions;
		this.criteria = criteria;
	}

	public ReportParams() {
	}

	public String getDs_name() {
		return ds_name;
	}

	public void setDs_name(String ds_name) {
		this.ds_name = ds_name;
	}

	public String getDs_operation() {
		return ds_operation;
	}

	public void setDs_operation(String ds_operation) {
		this.ds_operation = ds_operation;
	}

	public String getField_names() {
		return field_names;
	}

	public void setField_names(String field_names) {
		this.field_names = field_names;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public Map<String, String> getCaptions() {
		return captions;
	}

	public void setCaptions(Map<String, String> captions) {
		this.captions = captions;
	}

	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

}
