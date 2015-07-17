package com.ticketmaster.portal.webui.client.content.tabs.smsbroadcast;

import com.google.gwt.user.client.Window;
import com.ticketmaster.portal.webui.client.TicketMaster;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FileItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;

import java.util.LinkedHashMap;


/**
 * Created by vano on 2/24/15.
 */
public class SmsBlacklistForm extends Tab{
	private static SmsBlacklistForm instance;
	private VLayout vLayout;
	private HLayout layout;
	private DynamicForm form;
	private SelectItem contractsField;
	private FileItem fileField;
	private SelectItem senderField;
	private StaticTextItem warningField;

	public static SmsBlacklistForm getInstance(){
		if(instance == null){
			instance = new SmsBlacklistForm();
		}
		return instance;
	}

	public SmsBlacklistForm(){
		setTitle(TicketMaster.constants.sms_tabBlacklist());
		layout = new HLayout();
		layout.setHeight100();
		layout.setWidth100();


		form = new DynamicForm();
		form.setWidth100();
		form.setDataSource(DataSource.get("SMSBroadcastDS"));
		form.setPadding(5);
		form.setNumCols(2);
		form.setColWidths(80, "*");

		fileField = new FileItem("file", TicketMaster.constants.sms_file());
		fileField.setRequired(true);

		contractsField = new SelectItem("contract_id", TicketMaster.constants.sms_contract());
		contractsField.setDisplayField("party_name");
		contractsField.setValueField("contract_id");
		contractsField.setRequired(true);
		contractsField.setWidth(250);
		contractsField.setOptionDataSource(DataSource.get("SubscriberDS"));
		contractsField.setOptionOperationId("searchPartiesShort");
		contractsField.setAutoFetchData(true);
		contractsField.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent changedEvent) {
				loadSenders();
				senderField.setDisabled(false);
			}
		});

		senderField = new SelectItem("sender", TicketMaster.constants.sms_from());
//		senderField.setOptionDataSource(DataSource.get("SMSBroadcastDS"));
//		senderField.setOptionOperationId("getSenders");
		senderField.setDisplayField("sms_sender");
		senderField.setValueField("id");
		senderField.setAutoFetchData(false);
		senderField.setWidth(250);
		senderField.setDisabled(true);
		senderField.setRequired(true);

		warningField = new StaticTextItem();
		warningField.setShowTitle(false);
		warningField.setDefaultValue(TicketMaster.constants.sms_blackListWarning());
		warningField.setColSpan(2);
		warningField.setHeight(70);

		IButton resetBtn = new IButton(TicketMaster.constants.sms_reset());
		IButton submitBtn = new IButton(TicketMaster.constants.sms_send());
		IButton exportBtn = new IButton(TicketMaster.constants.sms_export());

		resetBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent clickEvent) {
				reset();
			}
		});
		submitBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent clickEvent) {
				submit();
			}
		});
		exportBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent clickEvent) {
				export();
			}
		});

		resetBtn.setIcon("all.png");
		submitBtn.setIcon("yes.png");
		exportBtn.setIcon("excel.gif");

		form.setFields(contractsField, senderField, fileField, warningField);

		layout.addMembers(form);

		vLayout = new VLayout();
		HLayout bbar = new HLayout();
		bbar.setMembersMargin(5);
		bbar.setAlign(Alignment.RIGHT);
		bbar.addMembers(exportBtn, resetBtn, submitBtn);

		vLayout.addMembers(layout, bbar);

		setPane(vLayout);
	}

	private void submit(){
		if(!form.validate()) return;

		DSRequest request = new DSRequest();
		request.setOperationId("uploadBlackList");
		request.setOperationType(DSOperationType.ADD);
		//request.setDataSource("SMSBroadcastDS");

		form.saveData(new DSCallback() {
			@Override
			public void execute(DSResponse dsResponse, Object o, DSRequest dsRequest) {
				String status;
				if(dsResponse.getData().length > 0) {
					status = dsResponse.getData()[0].getAttributeAsString("status");
				} else {
					status = "unknown error";
				}
				if(status.isEmpty()) {
					status = TicketMaster.constants.sms_success();
				}
				SC.say(TicketMaster.constants.status(), status);
			}
		}, request);
	}

	private void reset(){
		form.reset();
	}

	private void export(){
		Window.open("./SMSBroadcast/ExportBlackList?sender=" + senderField.getValueAsString(), "_blank", "");
	}
	private void loadSenders(){
		DataSource ds = DataSource.get("SMSBroadcastDS");
		DSRequest request = new DSRequest();
		request.setOperationType(DSOperationType.FETCH);
		request.setOperationId("getSenders");
		Criteria c = new Criteria();
		c.setAttribute("contract_id", contractsField.getValue());
		c.setAttribute("tab", "blackList");
		ds.fetchData(c, new DSCallback() {
			@Override
			public void execute(DSResponse dsResponse, Object o, DSRequest dsRequest) {
				LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
				Record[] data = dsResponse.getData();
				for (int i = 0; i < data.length; i++) {
					values.put(data[i].getAttributeAsString("id"), data[i].getAttributeAsString("sms_sender"));
				}
				senderField.setValueMap(values);
				senderField.setValue(data[0].getAttributeAsString("id"));
			}
		}, request);
	}
}
