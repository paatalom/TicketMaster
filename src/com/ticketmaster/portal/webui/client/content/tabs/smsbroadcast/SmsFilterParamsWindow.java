package com.ticketmaster.portal.webui.client.content.tabs.smsbroadcast;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.VLayout;

import java.util.Date;

/**
 * Created by vano on 2/27/15.
 */
public class SmsFilterParamsWindow extends Window {
	private DynamicForm form;
	public SmsFilterParamsWindow(Integer smsInfoId, String sms, String name){
		setTitle(TicketMaster.constants.sms_filterParams());
		setWidth(540);
		setHeight(200);
		setIsModal(true);
		setShowModalMask(true);
		setShowMaximizeButton(false);
		setShowMinimizeButton(false);
		setShowCloseButton(true);
		setAutoDraw(true);
		centerInPage();

		VLayout vLayout = new VLayout();

		Criteria c = new Criteria();
		c.setAttribute("timestamp", new Date().getTime());
		c.setAttribute("sms_info_id", smsInfoId);

		form = new DynamicForm();
		form.setWidth100();
		form.setHeight100();
		form.setNumCols(4);
		form.setPadding(20);
		form.setColWidths(135, 135, 135, 135);
		form.setCellPadding(5);
		form.setDataSource(DataSource.get("SMSBroadcastDS"));
		form.setAutoFetchData(true);
		form.setFetchOperation("getFilterParams");
		form.setInitialCriteria(c);

		StaticTextItem nameField = new StaticTextItem("name", TicketMaster.constants.name());
		StaticTextItem smsField = new StaticTextItem("sms", TicketMaster.constants.sms_sms());
		StaticTextItem regionField = new StaticTextItem("region", TicketMaster.constants.sms_region());
		StaticTextItem districtField = new StaticTextItem("district", TicketMaster.constants.sms_district());
		StaticTextItem partyTypeField = new StaticTextItem("party_type", TicketMaster.constants.sms_subscriberType());
		StaticTextItem phoneTypeField = new StaticTextItem("phone_type", TicketMaster.constants.sms_phoneType());
		StaticTextItem chargeField = new StaticTextItem("charge", TicketMaster.constants.sms_charge());
		StaticTextItem genderField = new StaticTextItem("gender", TicketMaster.constants.sms_gender());

		nameField.setColSpan(3);
		smsField.setColSpan(3);
		nameField.setDefaultValue(name);
		smsField.setDefaultValue(sms);
		smsField.setTitleVAlign(VerticalAlignment.TOP);

		form.setFields(
				nameField,
				smsField,
				regionField,				phoneTypeField,
				districtField,					chargeField,
				partyTypeField,					genderField);

		vLayout.addMembers(form);

		addItem(vLayout);
	}
}
