package com.ticketmaster.portal.webui.client.dialogs.contract;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class PhonesListResultDS extends DataSource {

	public static PhonesListResultDS getInstance() {
		return new PhonesListResultDS("PhonesListResultDS");
	}

	public PhonesListResultDS(String id) {
		DataSourceTextField param_value = new DataSourceTextField("param_value", TicketMaster.constants.phone());
		param_value.setPrimaryKey(true);
		param_value.setRequired(true);

		DataSourceIntegerField subscriber_id = new DataSourceIntegerField("subscriber_id",
				TicketMaster.constants.ident());

		DataSourceTextField notification = new DataSourceTextField("notification",
				TicketMaster.constants.notification());

		DataSourceIntegerField status = new DataSourceIntegerField("status", TicketMaster.constants.status());
		status.setHidden(true);

		DataSourceIntegerField limit = new DataSourceIntegerField("limit", TicketMaster.constants.limit());
		limit.setHidden(true);

		setFields(subscriber_id, param_value, notification, status, limit);
		setClientOnly(true);
	}
}
