package com.ticketmaster.portal.webui.client.dialogs.contract;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class PhonesClientDS extends DataSource {

	public static PhonesClientDS getInstance() {
		return new PhonesClientDS("PhonesClientDS");
	}

	public PhonesClientDS(String id) {

		DataSourceTextField param_value = new DataSourceTextField("param_value", TicketMaster.constants.phone());
		param_value.setPrimaryKey(true);
		param_value.setRequired(true);

		DataSourceIntegerField subscriber_id = new DataSourceIntegerField("subscriber_id", "");
		subscriber_id.setHidden(true);

		DataSourceIntegerField status = new DataSourceIntegerField("status", "");
		status.setHidden(true);

		setFields(param_value, subscriber_id, status);
		setClientOnly(true);
	}
}