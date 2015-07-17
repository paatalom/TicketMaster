package com.ticketmaster.portal.webui.client.dialogs.contract;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.client.component.mgtdialog.IExceptionCallBack;
import com.ticketmaster.portal.webui.client.component.mgtdialog.IMgtDialog;
import com.ticketmaster.portal.webui.client.component.mgtdialog.MgtDialog;
import com.ticketmaster.portal.webui.client.component.mgtdialog.MgtDialogClickEvent;
import com.ticketmaster.portal.webui.client.component.mgtdialog.MgtDialogSaveClickHandler;
import com.ticketmaster.portal.webui.client.component.utils.CompUtils;
import com.ticketmaster.portal.webui.client.utils.CommonSingleton;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.HeaderDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.HeaderDoubleClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class DlgBundleManagement extends IMgtDialog {

	protected Record contractRecord;

	private ListGrid bundlesGrid;

	public DlgBundleManagement(Record contractRecord) {
		try {
			this.contractRecord = contractRecord;
			setWidth(900);
			setHeight(600);

			VLayout mainVLayout = new VLayout();
			mainVLayout.setWidth100();
			mainVLayout.setHeight100();
			mainVLayout.setMembersMargin(10);
			mainVLayout.setPadding(5);

			ToolStrip toolStrip = new ToolStrip();
			toolStrip.setWidth100();
			toolStrip.setPadding(5);
			mainVLayout.addMember(toolStrip);

			ToolStripButton assignButton = new ToolStripButton();
			assignButton.setTitle(TicketMaster.constants.assign_bundle_to_subscribers());
			assignButton.setIcon("add.png");

			ToolStripButton remAssignButton = new ToolStripButton();
			remAssignButton.setTitle(TicketMaster.constants.delete_bundle_from_phone());
			remAssignButton.setIcon("delete.png");

			ToolStripButton findSubsBundleButton = new ToolStripButton();
			findSubsBundleButton.setTitle(TicketMaster.constants.bundle_subscribers());
			findSubsBundleButton.setIcon("find.png");

			// CORP_CONTRACT_EDIT_BUNDLE_MANAGEMENT
			if (CommonSingleton.getInstance().hasPermission("100000")) {
				toolStrip.addButton(assignButton);
				toolStrip.addButton(remAssignButton);
			}
			toolStrip.addButton(findSubsBundleButton);

			bundlesGrid = new ListGrid() {
				@Override
				protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
					String fieldName = getFieldName(colNum);
					if (fieldName.equals("bundle_price_str") || fieldName.equals("subscr_fee_str")) {
						String balanceTxt;
						if (fieldName.equals("bundle_price_str"))
							balanceTxt = record.getAttributeAsString("bundle_price_str");
						else
							balanceTxt = record.getAttributeAsString("subscr_fee_str");
						Double value = Double.parseDouble(balanceTxt);
						if (value <= 0) {
							return "font-family: Verdana; color:#d64949; font-size:11px; font-weight:bold;";
						} else {
							return "font-family: Verdana; color:#287fd6; font-size:11px; font-weight:bold;";
						}
					} else {
						return "color:black; font-size:11px;";
					}
				}
			};
			Criteria criteria = new Criteria();
			criteria.setAttribute("contract_id", contractRecord.getAttributeAsInt("contract_id"));

			DataSource bundleDS = DataSource.get("BundleDS");

			CompUtils.gridDefaultSetings(bundlesGrid);
			bundlesGrid.setDataSource(bundleDS);
			bundlesGrid.setFetchOperation("getContractBundles");
			bundlesGrid.setCriteria(criteria);
			bundlesGrid.setWidth100();
			bundlesGrid.setHeight100();
			bundlesGrid.setSelectionType(SelectionStyle.SINGLE);
			bundlesGrid.addHeaderDoubleClickHandler(new HeaderDoubleClickHandler() {
				@Override
				public void onHeaderDoubleClick(HeaderDoubleClickEvent event) {
					return;
				}
			});
			bundlesGrid.setSelectionType(SelectionStyle.MULTIPLE);

			ListGridField bundleId = new ListGridField();
			bundleId.setName("id");
			bundleId.setTitle(TicketMaster.constants.ident());
			bundleId.setWidth(70);
			bundleId.setAlign(Alignment.LEFT);

			ListGridField bundleName = new ListGridField();
			bundleName.setName("bundle_name");
			bundleName.setTitle(TicketMaster.constants.bundle_name());

			ListGridField bundlePrice = new ListGridField();
			bundlePrice.setName("bundle_price_str");
			bundlePrice.setTitle(TicketMaster.constants.price());
			bundlePrice.setWidth(130);
			bundlePrice.setAlign(Alignment.RIGHT);

			ListGridField subscrFee = new ListGridField();
			subscrFee.setName("subscr_fee_str");
			subscrFee.setTitle(TicketMaster.constants.subscr_fee());
			subscrFee.setWidth(130);
			subscrFee.setAlign(Alignment.RIGHT);

			ListGridField bundle_type_descr = new ListGridField();
			bundle_type_descr.setName("bundle_type_descr");
			bundle_type_descr.setTitle(TicketMaster.constants.bundle_type());
			bundle_type_descr.setWidth(170);
			bundle_type_descr.setAlign(Alignment.LEFT);

			bundlesGrid.setFields(bundleId, bundleName, bundle_type_descr, bundlePrice, subscrFee);

			mainVLayout.addMember(bundlesGrid);

			findSubsBundleButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					final ListGridRecord listGridRecord = bundlesGrid.getSelectedRecord();
					if (listGridRecord == null) {
						SC.say(TicketMaster.constants.please_select_record());
						return;
					}

					Integer bundle_type = listGridRecord.getAttributeAsInt("bundle_type");
					if (bundle_type == null || !bundle_type.equals(1)) {
						SC.say(TicketMaster.constants.this_is_not_subs_bundle());
						return;
					}

					showBundleSubscribers(listGridRecord);
				}
			});

			assignButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					final ListGridRecord listGridRecord = bundlesGrid.getSelectedRecord();
					if (listGridRecord == null) {
						SC.say(TicketMaster.constants.please_select_record());
						return;
					}

					Integer bundle_type = listGridRecord.getAttributeAsInt("bundle_type");
					if (bundle_type == null || !bundle_type.equals(1)) {
						SC.say(TicketMaster.constants.this_is_not_subs_bundle());
						return;
					}

					showAssignBundleSubscribers(false, listGridRecord);
				}
			});

			remAssignButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					final ListGridRecord listGridRecord = bundlesGrid.getSelectedRecord();
					if (listGridRecord == null) {
						SC.say(TicketMaster.constants.please_select_record());
						return;
					}
					Integer bundle_type = listGridRecord.getAttributeAsInt("bundle_type");
					if (bundle_type == null || !bundle_type.equals(1)) {
						SC.say(TicketMaster.constants.this_is_not_subs_bundle());
						return;
					}
					showAssignBundleSubscribers(true, listGridRecord);
				}
			});
			addMember(mainVLayout);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showAssignBundleSubscribers(boolean isDelete, ListGridRecord record) {
		try {
			DlgSubscriberBundleManagement contrItemDlg = new DlgSubscriberBundleManagement(isDelete, contractRecord,
					record);
			MgtDialog.showDialog(contrItemDlg, record, true);
			contrItemDlg.addOnSaveClickHandler(new MgtDialogSaveClickHandler() {
				@Override
				public void addOnSaveHandler(MgtDialogClickEvent evnt) {

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			SC.warn(e.toString());
		}
	}

	private void showBundleSubscribers(ListGridRecord record) {
		try {
			DlgSubscriberToBundleView contrItemDlg = new DlgSubscriberToBundleView(record);
			MgtDialog.showDialog(contrItemDlg, record, true);
			contrItemDlg.addOnSaveClickHandler(new MgtDialogSaveClickHandler() {
				@Override
				public void addOnSaveHandler(MgtDialogClickEvent evnt) {

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			SC.warn(e.toString());
		}
	}

	@Override
	public void fillFields(Record record) {
	}

	@Override
	public void validateSave(IExceptionCallBack callBack) {
	}

	@Override
	public Record getRecordForSave() {
		return null;
	}

	@Override
	public String getDataSourceName() {
		return null;
	}

	@Override
	public String getSaveOperation() {
		return null;
	}

	@Override
	public String getIdField() {
		return null;
	}

	@Override
	public String getWindowTitle() {
		return TicketMaster.constants.package_management();
	}

	@Override
	public boolean saveOnServer() {
		return false;
	}
}
