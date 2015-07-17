package com.ticketmaster.portal.webui.client.dialogs.contract;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class PhonesResultClientDS extends DataSource {

	public static PhonesResultClientDS getInstance() {
		return new PhonesResultClientDS("PhonesResultClientDS");
	}

	public PhonesResultClientDS(String id) {
		DataSourceTextField param_value = new DataSourceTextField("param_value", TicketMaster.constants.phone());
		param_value.setPrimaryKey(true);
		param_value.setRequired(true);

		DataSourceIntegerField subscriber_id = new DataSourceIntegerField("subscriber_id",
				TicketMaster.constants.ident());

		DataSourceTextField status = new DataSourceTextField("status", TicketMaster.constants.status());

		DataSourceTextField exception = new DataSourceTextField("exception", "");
		exception.setHidden(true);

		DataSourceIntegerField status1 = new DataSourceIntegerField("status1", "");
		status1.setHidden(true);

		DataSourceIntegerField done_cnt = new DataSourceIntegerField("done_cnt", "");
		done_cnt.setHidden(true);

		setFields(subscriber_id, param_value, status, exception, status1, done_cnt);
		setClientOnly(true);
	}
}