package com.ticketmaster.portal.webui.client.content.tabs.smsbroadcast;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DateDisplayFormat;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import java.util.Date;

/**
 * Created by vano on 2/27/15.
 */
public class SmsInfoGrid extends VLayout {
	private ListGrid grid;
	private ToolStripButton refreshBtn;
	private Label totalLabel;
	private String contractId;
	public SmsInfoGrid(){


		ToolStrip toolStrip = new ToolStrip();
		refreshBtn = new ToolStripButton(TicketMaster.constants.sms_refresh());
		refreshBtn.setID("smsInfoRefreshBtn");
		refreshBtn.setIcon("restoreIcon.gif");
		toolStrip.addButton(refreshBtn);

		totalLabel = new Label();
		totalLabel.setAlign(Alignment.RIGHT);
		totalLabel.setWidth100();
		totalLabel.setPadding(5);
		toolStrip.addMembers(totalLabel);

		grid = new ListGrid(){
			@Override
			protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {
				String fieldName = this.getFieldName(colNum);
				final String error = record.getAttributeAsString("error");
				if (fieldName.equals("error_btn")) {
					if (error != null && !error.isEmpty()) {
						ImgButton btn = new ImgButton();
						btn.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent clickEvent) {
								SC.say(error + "<br><br>" + TicketMaster.constants.sms_sendFailed());
							}
						});
						btn.setSrc("warning.png");
						btn.setWidth(16);
						btn.setHeight(16);
						btn.setShowRollOver(false);
						btn.setShowDown(false);
						return btn;
					}
				}
				return null;
			}
		};
		grid.setShowRecordComponents(true);
		grid.setShowRecordComponentsByCell(true);
		grid.setDataSource(DataSource.get("SMSBroadcastDS"));
		grid.setAutoFetchData(false);
		grid.setFetchOperation("getSMSInfo");
		ListGridField sendDateField = new ListGridField("send_date", TicketMaster.constants.sms_date(), 120);
		sendDateField.setType(ListGridFieldType.DATETIME);
		sendDateField.setDateFormatter(DateDisplayFormat.TOEUROPEANSHORTDATETIME);
		grid.setFields(
				new ListGridField("id", "ID", 50),
				new ListGridField("name", TicketMaster.constants.name()),
				new ListGridField("sms_text", TicketMaster.constants.sms_sms(), 300),
				new ListGridField("sms_sender", TicketMaster.constants.sms_from()),
				new ListGridField("type", TicketMaster.constants.sms_type()),
				new ListGridField("sms_count", TicketMaster.constants.sms_size()),
				//new ListGridField("state", TicketMaster.constants.sms_state()),
				sendDateField,
				new ListGridField("delivery_count", TicketMaster.constants.sms_delivered()),
				new ListGridField("error_btn", " ", 18)
		);

		grid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent cellDoubleClickEvent) {
				ListGridRecord record = cellDoubleClickEvent.getRecord();
				Integer typeId = record.getAttributeAsInt("type_id");
				String sms = record.getAttributeAsString("sms_text");
				String name = record.getAttributeAsString("name");
				System.out.println(typeId);
				if(typeId == 2){
					new SmsFilterParamsWindow(record.getAttributeAsInt("id"), sms, name);
				} else {
					new SmsQueueWindow(record.getAttributeAsInt("id"), sms, name);
				}
			}
		});

		refreshBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent clickEvent) {
				refresh(contractId);
			}
		});

		addMembers(toolStrip, grid);
		setMembersMargin(-1);
		//refresh();
	}

	public void refresh(String contractId){
		if(this.contractId == null || this.contractId.isEmpty()) {
			if (contractId == null || contractId.isEmpty()) {
				this.contractId = "0";
			} else {
				this.contractId = contractId;
			}
		}
		Criteria c = new Criteria();
		c.setAttribute("contract_id", this.contractId);
		c.setAttribute("timestamp", new Date().getTime());
		grid.fetchData(c);

		DSRequest request = new DSRequest();
		DataSource ds = DataSource.get("SMSBroadcastDS");
		request.setOperationId("getTotalDelivery");
		request.setOperationType(DSOperationType.FETCH);
		ds.fetchData(c, new DSCallback() {
			@Override
			public void execute(DSResponse dsResponse, Object o, DSRequest dsRequest) {
				totalLabel.setContents(TicketMaster.constants.sms_totalDelivery() + ": " + dsResponse.getData()[0].getAttributeAsString("delivery_count"));
			}
		}, request);


	}

}
