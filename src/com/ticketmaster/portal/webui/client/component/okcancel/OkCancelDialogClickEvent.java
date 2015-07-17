package com.ticketmaster.portal.webui.client.component.okcancel;

import com.google.gwt.event.shared.GwtEvent;
import com.smartgwt.client.data.Record;

public class OkCancelDialogClickEvent extends GwtEvent<OkCancelDialogOkClickHandler> {

	private Record record;

	public OkCancelDialogClickEvent(Record record) {
		super();
		this.record = record;
	}

	public static final Type<OkCancelDialogOkClickHandler> TYPE = new Type<OkCancelDialogOkClickHandler>();

	@Override
	public Type<OkCancelDialogOkClickHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(OkCancelDialogOkClickHandler handler) {
		handler.addOnOkHandler(this);
	}

	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		this.record = record;
	}

	public static Type<OkCancelDialogOkClickHandler> getType() {
		return TYPE;
	}
}
