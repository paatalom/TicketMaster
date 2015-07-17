package com.ticketmaster.portal.webui.client.content.tabs.smsbroadcast;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DateDisplayFormat;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

import java.util.Date;

/**
 * Created by vano on 2/27/15.
 */
public class SmsQueueWindow extends Window {
	private DynamicForm form;
	private ListGrid grid;
	private StaticTextItem smsField;

	public SmsQueueWindow(final Integer smsInfoId, String sms, String name){
		setTitle(TicketMaster.constants.sms_numbers());
		setWidth(600);
		setHeight(500);
		setIsModal(true);
		setShowModalMask(true);
		setShowMaximizeButton(true);
		setShowMinimizeButton(false);
		setShowCloseButton(true);
		setAutoDraw(true);
		centerInPage();

		VLayout vLayout = new VLayout();
		vLayout.setWidth100();
		vLayout.setHeight100();
		form = new DynamicForm();
		form.setPadding(15);
		form.setWidth100();
		form.setNumCols(2);
		smsField = new StaticTextItem("sms", TicketMaster.constants.sms_sms());
		smsField.setValue(sms);
		smsField.setTitleVAlign(VerticalAlignment.TOP);

		StaticTextItem nameField = new StaticTextItem("name", TicketMaster.constants.name());
		nameField.setValue(name);
		form.setFields(nameField, smsField);

		Criteria c = new Criteria();
		c.setAttribute("timestamp", new Date().getTime());
		c.setAttribute("sms_info_id", smsInfoId);

		grid = new ListGrid();
		grid.setWidth100();
		grid.setHeight100();
		grid.setDataSource(DataSource.get("SMSBroadcastDS"));
		grid.setFetchOperation("getNumbers");
		grid.setCriteria(c);
		grid.setAutoFetchData(true);
		ListGridField sendDateField = new ListGridField("send_date", TicketMaster.constants.sms_sendDate(), 120);
		sendDateField.setDateFormatter(DateDisplayFormat.TOEUROPEANSHORTDATETIME);
		ListGridField deliveryDateField = new ListGridField("delivery_date", TicketMaster.constants.sms_deliveryDate(), 120);
		deliveryDateField.setDateFormatter(DateDisplayFormat.TOEUROPEANSHORTDATETIME);
		grid.setFields(
				new ListGridField("id", "ID", 60),
				new ListGridField("phone_number", TicketMaster.constants.sms_number()),
				new ListGridField("state", TicketMaster.constants.sms_state()),
				sendDateField,
				deliveryDateField
		);

		IButton exportBtn = new IButton(TicketMaster.constants.sms_export());
		exportBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent clickEvent) {
				com.google.gwt.user.client.Window.open("./SMSBroadcast/ExportNumbers?sms_info_id=" + smsInfoId, "_self", "");
			}
		});
		exportBtn.setIcon("excel.gif");

		HLayout bbar = new HLayout();
		bbar.setAlign(Alignment.RIGHT);
		bbar.setMembersMargin(5);
		bbar.setPadding(5);
		bbar.addMembers(exportBtn);
		vLayout.addMembers(form, grid, bbar);

		addItem(vLayout);
	}
}
