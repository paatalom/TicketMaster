package com.ticketmaster.portal.webui.client.component.mgtdialog;

import com.google.gwt.event.shared.GwtEvent;
import com.smartgwt.client.data.Record;

public class MgtDialogClickEvent extends GwtEvent<MgtDialogSaveClickHandler> {

	private Record record;

	public MgtDialogClickEvent(Record record) {
		super();
		this.record = record;
	}

	public static final Type<MgtDialogSaveClickHandler> TYPE = new Type<MgtDialogSaveClickHandler>();

	@Override
	public Type<MgtDialogSaveClickHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MgtDialogSaveClickHandler handler) {
		handler.addOnSaveHandler(this);
	}

	public Record getRecord() {
		return record;
	}
	public void setRecord(Record record) {
		this.record = record;
	}

	public static Type<MgtDialogSaveClickHandler> getType() {
		return TYPE;
	}
}
