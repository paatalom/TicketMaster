package com.ticketmaster.portal.webui.client.component.okcancel;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.layout.HLayout;

public abstract class OkCancelDialogLayout extends HLayout {

	private HandlerManager handlerManager = new HandlerManager(this);
	protected OkCancelDialog ocDialog;

	public abstract Record validateAndGetRecord();

	public abstract String getWindowTitle();

	public abstract String getOkTitle();

	public void addOnOkClickHandler(OkCancelDialogOkClickHandler handler) {
		handlerManager.addHandler(OkCancelDialogClickEvent.getType(), handler);
	}

	public HandlerManager getMgtHandlerManager() {
		return handlerManager;
	}

	public void setOkCancelDialog(OkCancelDialog ocDialog) {
		this.ocDialog = ocDialog;
	}
}
