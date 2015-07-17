package com.ticketmaster.portal.webui.client.component.mgtdialog;

public class MgtDialogOptions {

	private String saveTitle;
	private boolean isModal;
	private boolean disposeOnFocusLost;
	private boolean showFooter;

	public MgtDialogOptions() {
		isModal = true;
	}

	public String getSaveTitle() {
		return saveTitle;
	}

	public void setSaveTitle(String saveTitle) {
		this.saveTitle = saveTitle;
	}

	public boolean isModal() {
		return isModal;
	}

	public void setModal(boolean isModal) {
		this.isModal = isModal;
	}

	public boolean getDisposeOnFocusLost() {
		return disposeOnFocusLost;
	}

	public void setDisposeOnFocusLost(boolean disposeOnFocusLost) {
		this.disposeOnFocusLost = disposeOnFocusLost;
	}

	public boolean getShowFooter() {
		return showFooter;
	}

	public void setShowFooter(boolean showFooter) {
		this.showFooter = showFooter;
	}
}
