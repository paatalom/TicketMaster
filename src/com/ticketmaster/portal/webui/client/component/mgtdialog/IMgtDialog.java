package com.ticketmaster.portal.webui.client.component.mgtdialog;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.SimpleEventBus;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.layout.HLayout;

public abstract class IMgtDialog extends HLayout {

	final private HandlerManager mgtHandlerManager = new HandlerManager(this);
	private SimpleEventBus eventbus;
	protected MgtDialog mgtDialog;

	public SimpleEventBus getEventbus() {
		if (eventbus == null) {
			eventbus = new SimpleEventBus();
		}
		return eventbus;
	}

	public abstract void fillFields(Record record);

	public abstract void validateSave(IExceptionCallBack callBack);

	public abstract Record getRecordForSave();

	public abstract String getDataSourceName();

	public abstract String getSaveOperation();

	public abstract String getIdField();

	public abstract String getWindowTitle();

	public abstract boolean saveOnServer();

	public void addOnSaveClickHandler(MgtDialogSaveClickHandler handler) {
		mgtHandlerManager.addHandler(MgtDialogClickEvent.getType(), handler);
	}

	public HandlerManager getMgtHandlerManager() {
		return mgtHandlerManager;
	}

	public void setMgtDialog(MgtDialog mgtDialog) {
		this.mgtDialog = mgtDialog;
	}
}
