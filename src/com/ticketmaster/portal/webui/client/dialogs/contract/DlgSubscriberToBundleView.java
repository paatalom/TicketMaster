package com.ticketmaster.portal.webui.client.dialogs.contract;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.client.component.mgtdialog.IExceptionCallBack;
import com.ticketmaster.portal.webui.client.component.mgtdialog.IMgtDialog;
import com.ticketmaster.portal.webui.client.component.utils.CompUtils;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ExportDisplay;
import com.smartgwt.client.types.ExportFormat;
import com.smartgwt.client.util.EnumUtil;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.events.HeaderDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.HeaderDoubleClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class DlgSubscriberToBundleView extends IMgtDialog {

	private Record localRecord;
	private ListGrid contractItemsGrid;
	private DataSource bundleDS;

	public DlgSubscriberToBundleView(final Record localRecord) {
		try {
			this.localRecord = localRecord;
			setWidth(900);
			setHeight(600);
			setPadding(5);

			VLayout mainVLayout = new VLayout();
			mainVLayout.setWidth100();
			mainVLayout.setHeight100();
			mainVLayout.setMembersMargin(10);

			ToolStrip toolStrip = new ToolStrip();
			toolStrip.setWidth100();
			toolStrip.setPadding(5);

			ToolStripButton excelExportBtn = new ToolStripButton(TicketMaster.constants.export(), "excel.gif");
			toolStrip.addButton(excelExportBtn);

			mainVLayout.addMember(toolStrip);

			bundleDS = DataSource.get("BundleDS");
			contractItemsGrid = new ListGrid();

			Criteria criteria = new Criteria();
			criteria.setAttribute("bundle_id", localRecord.getAttributeAsInt("id"));

			CompUtils.gridDefaultSetings(contractItemsGrid);
			contractItemsGrid.setHeight100();
			contractItemsGrid.setWidth100();
			contractItemsGrid.setDataSource(bundleDS);
			contractItemsGrid.setFetchOperation("getBundleSubscribers");
			contractItemsGrid.setCriteria(criteria);
			contractItemsGrid.addHeaderDoubleClickHandler(new HeaderDoubleClickHandler() {
				@Override
				public void onHeaderDoubleClick(HeaderDoubleClickEvent event) {
					return;
				}
			});

			ListGridField user_identifier = new ListGridField();
			user_identifier.setName("user_identifier");
			user_identifier.setTitle(TicketMaster.constants.phone());
			user_identifier.setWidth(80);
			user_identifier.setAlign(Alignment.CENTER);

			ListGridField contract_item_name = new ListGridField();
			contract_item_name.setName("contract_item_name");
			contract_item_name.setTitle(TicketMaster.constants.contract_item_name());

			ListGridField subscriberType = new ListGridField();
			subscriberType.setName("subscriber_type");
			subscriberType.setTitle(TicketMaster.constants.subscriber_type());
			subscriberType.setWidth(180);
			subscriberType.setAlign(Alignment.CENTER);

			ListGridField subs_bundle_type_descr = new ListGridField();
			subs_bundle_type_descr.setName("subs_bundle_type_descr");
			subs_bundle_type_descr.setTitle(TicketMaster.constants.bundle_type());
			subs_bundle_type_descr.setWidth(280);
			subs_bundle_type_descr.setAlign(Alignment.CENTER);

			ListGridField bundle_price_charge_type_descr = new ListGridField();
			bundle_price_charge_type_descr.setName("bundle_price_charge_type_descr");
			bundle_price_charge_type_descr.setTitle(TicketMaster.constants.price_type());
			bundle_price_charge_type_descr.setWidth(150);
			bundle_price_charge_type_descr.setAlign(Alignment.CENTER);

			contractItemsGrid.setFields(user_identifier, contract_item_name, subs_bundle_type_descr,
					bundle_price_charge_type_descr);

			mainVLayout.addMember(contractItemsGrid);
			addMember(mainVLayout);

			excelExportBtn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					try {
						DSRequest dsRequest = new DSRequest();

						dsRequest.setOperationId("getBundleSubscribers");
						dsRequest.setExportAs((ExportFormat) EnumUtil.getEnum(ExportFormat.values(), "xls"));
						dsRequest.setExportDisplay(ExportDisplay.DOWNLOAD);
						dsRequest.setExportFields(new String[] { "user_identifier", "contract_item_name",
								"subs_bundle_type_descr", "bundle_price_charge_type_descr" });

						Criteria criteria = new Criteria();
						criteria.setAttribute("bundle_id", localRecord.getAttributeAsInt("id"));

						contractItemsGrid.exportData(dsRequest);
					} catch (Exception e) {
						e.printStackTrace();
						SC.say(e.toString());
					} finally {
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void fillFields(Record record) {
		try {
			localRecord = record;
		} catch (Exception e) {
			e.printStackTrace();
			SC.warn(e.toString());
		}
	}

	@Override
	public void validateSave(IExceptionCallBack callBack) {
		callBack.fireValidation(null);
		return;
	}

	@Override
	public Record getRecordForSave() {
		try {
			return localRecord;
		} catch (Exception e) {
			e.printStackTrace();
			SC.warn(e.toString());
		}
		return null;
	}

	@Override
	public String getDataSourceName() {
		return "";
	}

	@Override
	public String getSaveOperation() {
		return "";
	}

	@Override
	public String getIdField() {
		return "";
	}

	@Override
	public String getWindowTitle() {
		return TicketMaster.constants.bundle_subscribers();
	}

	@Override
	public boolean saveOnServer() {
		return true;
	}
}
