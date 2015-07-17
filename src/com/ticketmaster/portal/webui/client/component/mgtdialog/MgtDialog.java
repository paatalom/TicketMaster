package com.ticketmaster.portal.webui.client.component.mgtdialog;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.client.utils.CommonSingleton;
import com.smartgwt.client.core.Function;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ClickMaskMode;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.KeyPressEvent;
import com.smartgwt.client.widgets.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class MgtDialog extends Window {

	public MgtDialog(MgtDialogOptions options) {
		setWidth(800);
		setHeight(600);
		setTitle("MgtDialog");

		if (options != null && !options.isModal()) {
			setIsModal(false);
			// setCanDrag(true);
			// setCanDragReposition(true);
			setCanDragResize(false);
			setCanDragScroll(false);
			setShowModalMask(false);
		} else {
			setShowModalMask(true);
			setIsModal(true);
			// setCanDrag(false);
			// setCanDragReposition(false);
			setCanDragResize(false);
			setCanDragScroll(false);
		}

		setKeepInParentRect(true);
		setShowCloseButton(true);

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});

		addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName() != null && event.getKeyName().equals("Escape")) {
					destroy();
				}
			}
		});
	}

	public static MgtDialog showDialog(final IMgtDialog iMgtPanel, final Record dataRecord, boolean readOnly,
			boolean showTitle, MgtDialogOptions options) {

		try {
			iMgtPanel.fillFields(dataRecord);
			final MgtDialog mgtDialog = new MgtDialog(options);
			iMgtPanel.setMgtDialog(mgtDialog);
			if (!showTitle) {
				mgtDialog.setAttribute("styleName", "", true);
				mgtDialog.setShowHeader(showTitle);
			}

			int panelWidth = (iMgtPanel.getWidth() + 20);
			int panelHeight;
			if (options != null && options.getShowFooter())
				panelHeight = (iMgtPanel.getHeight() + 70);
			else
				panelHeight = (iMgtPanel.getHeight() + 40);
			mgtDialog.setWidth(panelWidth);
			mgtDialog.setHeight(panelHeight);
			mgtDialog.centerInPage();

			String title = iMgtPanel.getWindowTitle();
			boolean bundlTTPerm = CommonSingleton.getInstance().hasPermission("100003");
			if (bundlTTPerm) {
				String className = iMgtPanel.getClass().getName();
				int index = className.lastIndexOf(".");
				className = className.substring(index + 1);
				title += ", ClassName: " + className;
			}
			mgtDialog.setTitle(title);
			mgtDialog.setShowTitle(showTitle);

			iMgtPanel.setWidth100();
			iMgtPanel.setHeight100();

			VLayout container = new VLayout();
			container.setHeight100();
			container.setWidth100();
			container.addMember(iMgtPanel);

			VLayout footer = new VLayout();
			footer.setPadding(5);
			footer.setHeight(30);
			footer.setWidth100();
			footer.setMembersMargin(5);

			IButton closeBtn = new IButton(TicketMaster.constants.close());
			IButton saveBtn = new IButton(options != null && options.getSaveTitle() != null ? options.getSaveTitle()
					: TicketMaster.constants.save());

			HLayout layoutSpacer = new HLayout();
			layoutSpacer.setWidth100();
			layoutSpacer.setHeight(1);
			layoutSpacer.setBorder("1px solid #89a2c4");

			HLayout buttonsLayout = new HLayout();
			buttonsLayout.setWidth100();
			buttonsLayout.setHeight100();
			buttonsLayout.setAlign(Alignment.RIGHT);
			buttonsLayout.setMembersMargin(5);

			if (options != null && options.getDisposeOnFocusLost()) {
				mgtDialog.showClickMask(new Function() {

					@Override
					public void execute() {
						mgtDialog.destroy();
					}
				}, ClickMaskMode.HARD, new Canvas[] {});
			}

			if (readOnly) {
				buttonsLayout.addMembers(closeBtn);
			} else {
				buttonsLayout.addMembers(saveBtn, closeBtn);
			}

			footer.addMembers(layoutSpacer, buttonsLayout);
			if (options != null && options.getShowFooter() && showTitle)
				container.addMember(footer);

			mgtDialog.addItem(container);
			mgtDialog.show();

			closeBtn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					mgtDialog.destroy();
				}
			});
			saveBtn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					mgtDialog.callSave(iMgtPanel);
				}
			});
			return mgtDialog;
		} catch (Exception e) {
			e.printStackTrace();
			SC.say(e.toString());
			return null;
		}

	}

	public static MgtDialog showDialog(final IMgtDialog iMgtPanel, final Record dataRecord, boolean readOnly,
			boolean showTitle) {
		return showDialog(iMgtPanel, dataRecord, readOnly, showTitle, null);
	}

	public void callSave(final IMgtDialog iMgtPanel) {
		try {
			IExceptionCallBack callBack = new IExceptionCallBack() {
				@Override
				public void fireValidation(Exception ex) {
					if (ex != null) {
						SC.warn(ex.getMessage());
						return;
					} else {
						executeSave(iMgtPanel);
					}
				}

				@Override
				public void fireValidation(Exception ex, IMgtDialog dialog, MgtDialogOptions options) {
					if (dialog != null) {
						MgtDialog.showDialog(dialog, null, true, options);
						return;
					}
					if (ex != null) {
						SC.warn(ex.getMessage());
						return;
					} else {
						executeSave(iMgtPanel);
					}
				}
			};
			iMgtPanel.validateSave(callBack);
		} catch (Exception e) {
			e.printStackTrace();
			SC.warn(e.toString());
		}
	}

	private void executeSave(final IMgtDialog iMgtPanel) {
		try {
			if (iMgtPanel.saveOnServer()) {

				final Record record = iMgtPanel.getRecordForSave();
				if (record == null) {
					SC.say(TicketMaster.constants.warning(), "Record Is Null. Check getRecordForSave() Method.");
					return;
				}
				saveOnServer(iMgtPanel, this, record);
			} else {
				final Record record = iMgtPanel.getRecordForSave();
				if (record == null) {
					SC.say(TicketMaster.constants.warning(), "Record Is Null. Check getRecordForSave() Method.");
					return;
				}
				iMgtPanel.getMgtHandlerManager().fireEvent(new MgtDialogClickEvent(record));
			}
		} catch (Exception e) {
			e.printStackTrace();
			SC.say(e.toString());
		}
	}

	public static MgtDialog showDialog(final IMgtDialog iMgtPanel, final Record dateRecord, boolean readOnly) {
		return showDialog(iMgtPanel, dateRecord, readOnly, true, null);
	}

	public static MgtDialog showDialog(final IMgtDialog iMgtPanel, final Record dateRecord, boolean readOnly,
			MgtDialogOptions dialogOptions) {
		return showDialog(iMgtPanel, dateRecord, readOnly, true, dialogOptions);
	}

	private static void saveOnServer(final IMgtDialog iMgtPanel, final MgtDialog mgtDialog, final Record record) {
		String idField = iMgtPanel.getIdField();
		if (idField == null || idField.trim().equals("")) {
			SC.say(TicketMaster.constants.warning(), "ID Field Is Null. Check getIdField() Method.");
			return;
		}

		String dsName = iMgtPanel.getDataSourceName();
		if (dsName == null || dsName.trim().equals("")) {
			SC.say(TicketMaster.constants.warning(), "DataSource Name Is Null. Check getDataSourceName() Method.");
			return;
		}

		String saveOper = iMgtPanel.getSaveOperation();
		if (saveOper == null || saveOper.trim().equals("")) {
			SC.say(TicketMaster.constants.warning(), "Save Operation Is Null. Check getIdField() Method.");
			return;
		}

		try {
			DataSource dataSource = DataSource.get(dsName);
			Long idValue = record.getAttributeAsLong(idField);

			DSRequest req = new DSRequest();
			req.setAttribute("operationId", saveOper);

			com.smartgwt.client.rpc.RPCManager.startQueue();

			if (idValue == null) {
				dataSource.addData(record, new DSCallback() {
					@Override
					public void execute(DSResponse response, Object rawData, DSRequest request) {
						Record data[] = response.getData();
						Record responseRecord = data != null && data.length > 0 ? data[0] : null;
						iMgtPanel.getMgtHandlerManager().fireEvent(new MgtDialogClickEvent(responseRecord));
						mgtDialog.destroy();
					}
				}, req);
			} else {
				dataSource.updateData(record, new DSCallback() {
					@Override
					public void execute(DSResponse response, Object rawData, DSRequest request) {
						Record data[] = response.getData();
						Record responseRecord = data != null && data.length > 0 ? data[0] : null;
						iMgtPanel.getMgtHandlerManager().fireEvent(new MgtDialogClickEvent(responseRecord));
						mgtDialog.destroy();
					}
				}, req);
			}

			com.smartgwt.client.rpc.RPCManager.sendQueue();

		} catch (Exception e) {
			e.printStackTrace();
			SC.say(e.toString());
		}
	}
}
