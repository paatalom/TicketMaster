package com.ticketmaster.portal.webui.client.component.okcancel;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.client.utils.CommonSingleton;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.KeyPressEvent;
import com.smartgwt.client.widgets.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class OkCancelDialog extends Window {

	static OkCancelDialog okCancelDialog;

	public OkCancelDialog() {
		setShowMinimizeButton(true);
		setIsModal(true);
		// setCanDrag(false);
		// setCanDragReposition(false);
		setKeepInParentRect(true);
		setCanDragResize(false);
		setCanDragScroll(false);
	}

	public static void showDialog(final OkCancelDialogLayout okCancelDialogLayout) {
		showDialog(okCancelDialogLayout, true);
	}

	public static void showDialog(final OkCancelDialogLayout okCancelDialogLayout, final boolean closeOnOK) {
		try {
			okCancelDialog = new OkCancelDialog();
			okCancelDialogLayout.setOkCancelDialog(okCancelDialog);

			okCancelDialog.setWidth(okCancelDialogLayout.getWidth() + 10);
			okCancelDialog.setHeight(okCancelDialogLayout.getHeight() + 95);

			String title = okCancelDialogLayout.getWindowTitle();
			boolean bundlTTPerm = CommonSingleton.getInstance().hasPermission("100003	");
			if (bundlTTPerm) {
				String className = okCancelDialogLayout.getClass().getName();
				int index = className.lastIndexOf(".");
				className = className.substring(index + 1);
				title += " - " + className;
			}
			okCancelDialog.setTitle(title);

			okCancelDialogLayout.setWidth100();
			okCancelDialogLayout.setHeight100();

			okCancelDialog.centerInPage();

			okCancelDialog.addKeyPressHandler(new KeyPressHandler() {
				@Override
				public void onKeyPress(KeyPressEvent event) {
					if (event.getKeyName() != null && event.getKeyName().equals("Escape")) {
						okCancelDialog.destroy();
					}
				}
			});

			VLayout container = new VLayout();
			container.setHeight100();
			container.setWidth100();
			container.addMember(okCancelDialogLayout);

			// footer >>>>> >>>>>
			VLayout footerVLayout = new VLayout();
			footerVLayout.setPadding(5);
			footerVLayout.setHeight(30);
			footerVLayout.setWidth100();
			footerVLayout.setMembersMargin(5);

			HLayout spacerHLayout = new HLayout();
			spacerHLayout.setWidth100();
			spacerHLayout.setHeight(1);
			spacerHLayout.setBorder("1px solid #89a2c4");

			HLayout buttonsHLayout = new HLayout();
			buttonsHLayout.setWidth100();
			buttonsHLayout.setHeight100();
			buttonsHLayout.setAlign(Alignment.RIGHT);
			buttonsHLayout.setMembersMargin(5);

			IButton closeBtn = new IButton(TicketMaster.constants.close());
			IButton okBtn = new IButton(okCancelDialogLayout.getOkTitle());

			closeBtn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					okCancelDialog.destroy();
				}
			});

			okBtn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Record r = okCancelDialogLayout.validateAndGetRecord();
					if (r != null) {
						(okCancelDialogLayout.getMgtHandlerManager()).fireEvent(new OkCancelDialogClickEvent(r));
						if (closeOnOK)
							okCancelDialog.destroy();
					}
				}
			});

			buttonsHLayout.setMembers(okBtn, closeBtn);
			footerVLayout.setMembers(spacerHLayout, buttonsHLayout);
			// footer <<<<< <<<<<

			container.addMember(footerVLayout);
			okCancelDialog.addItem(container);

			okCancelDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
			SC.warn(e.toString());
		}
	}

	public OkCancelDialog getDialog() {
		return okCancelDialog;
	}
}
