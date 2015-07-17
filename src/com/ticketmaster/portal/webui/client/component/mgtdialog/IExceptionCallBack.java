package com.ticketmaster.portal.webui.client.component.mgtdialog;

public interface IExceptionCallBack {

	void fireValidation(Exception ex);

	void fireValidation(Exception ex, IMgtDialog dialog, MgtDialogOptions options);
}
