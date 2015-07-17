package com.ticketmaster.portal.webui.client.component.utils;

import java.util.Map;

import com.ticketmaster.portal.webui.shared.exception.PortalException;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.events.HeaderDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.HeaderDoubleClickHandler;

public class CompUtils {
	public static void gridDefaultSetings(ListGrid grid) {
		grid.setShowRecordComponents(true);
		grid.setShowRecordComponentsByCell(true);
		grid.setWidth100();
		grid.setHeight(150);
		grid.setAlternateRecordStyles(true);
		grid.setAutoFetchData(true);
		grid.setCanEdit(false);
		grid.setCanRemoveRecords(false);
		grid.setWrapCells(true);
		grid.setFixedRecordHeights(false);
		grid.setCanDragSelectText(true);
		grid.setCanSort(false);
		grid.setCanReorderFields(false);
		grid.setCanResizeFields(false);
		grid.setShowHeaderContextMenu(false);
		grid.addHeaderDoubleClickHandler(new HeaderDoubleClickHandler() {
			@Override
			public void onHeaderDoubleClick(HeaderDoubleClickEvent event) {
				return;
			}
		});
	}

	public static void setValuesToForm(Record subscriberRecord, DynamicForm form) {
		Map<?, ?> values = subscriberRecord.toMap();
		form.setValues(values);
	}

	public static void fillTable(final ListGrid grid, Map<String, String> keyValues, Criteria criteria,
			String operationId, String datasourceName) throws PortalException {

		if (keyValues != null) {
			for (Map.Entry<String, String> entry : keyValues.entrySet()) {
				criteria.setAttribute(entry.getKey(), entry.getValue());
			}
		}
		DataSource groupDS = DataSource.get(datasourceName);
		DSRequest dsRequest = new DSRequest();
		dsRequest.setAttribute("operationId", operationId);
		grid.invalidateCache();
		groupDS.fetchData(criteria, new DSCallback() {
			@Override
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				for (int i = 0; i < response.getData().length; i++) {
					grid.getDataAsRecordList().add(response.getData()[i]);
				}
			}
		}, dsRequest);

	}

	public static void fillGrid(final ListGrid grid, Criteria criteria, String operationId) throws PortalException {
		try {
			DSRequest dsRequest = new DSRequest();
			dsRequest.setOperationId(operationId);
			grid.invalidateCache();
			grid.fetchData(criteria, new DSCallback() {
				@Override
				public void execute(DSResponse response, Object rawData, DSRequest request) {
				}
			}, dsRequest);
		} catch (Exception ex) {
			ex.printStackTrace();
			SC.warn(ex.toString());
		}
	}

	public static String[] splitString(String value) {
		String delim = "\n";
		String[] splitstrings = value.split(delim);
		return splitstrings;
	}

	public static boolean isNumber(String s) {
		if (strIsEmpty(s))
			return false;
		if (s.matches("[0-9]{1,100}"))
			return true;
		else
			return false;
	}

	public static boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean strIsEmpty(String s) {
		try {
			return (s == null || s.trim().equals(""));
		} catch (Exception e) {
			return true;
		}
	}

	public static boolean isEmpty(Object o) {
		try {
			return (o == null || o.toString().trim().equals(""));
		} catch (Exception e) {
			return true;
		}
	}

	public static boolean isValidEmailAddress(Object o) {
		if (isEmpty(o))
			return false;
		return o.toString()
				.matches(
						"^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");
	}
}
