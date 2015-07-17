package com.ticketmaster.portal.webui.client.content.tabs.smsbroadcast;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.FileItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;

import java.util.LinkedHashMap;


/**
 * Created by vano on 2/24/15.
 */
public class SmsUploadForm extends Tab{
	private static SmsUploadForm instance;
	private VLayout vLayout;
	private HLayout layout;
	private DynamicForm form;
	private SelectItem contractsField;
	private SelectItem senderField;
	private TextAreaItem smsField;
	private TextItem nameField;
	private FileItem fileField;
	private StaticTextItem smsOffText;
	private StaticTextItem smsCounter;
	private SmsInfoGrid smsInfoGrid;
	private CheckboxItem smsOffCheckBox;
	private SelectItem categoryCombo;
	private StaticTextItem blankField1;
	private StaticTextItem blankField2;
	private StaticTextItem blankField3;
	private StaticTextItem blankField4;
	private String getSmsOffText(){
		return " SMS Off 91521 Text: " + senderField.getDisplayValue();
	}

	public static SmsUploadForm getInstance(){
		if(instance == null){
			instance = new SmsUploadForm();
		}
		return instance;
	}

	public SmsUploadForm(){
		setTitle(TicketMaster.constants.sms_tabUpload());

		layout = new HLayout();
		layout.setWidth100();
		layout.setHeight100();

		form = new DynamicForm();
		form.setNumCols(4);
		form.setPadding(5);
		form.setWidth(690);
		form.setDataSource(DataSource.get("SMSBroadcastDS"));
		form.setColWidths(80, 250, 130, "*");

		Criteria c = new Criteria();
		c.setAttribute("tab", "uploadNumbers");

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
				smsInfoGrid.refresh(contractsField.getValueAsString());
			}
		});

		senderField = new SelectItem("sender", TicketMaster.constants.sms_from());
//		senderField.setOptionDataSource(DataSource.get("SMSBroadcastDS"));
//		senderField.setOptionOperationId("getSenders");
//		senderField.setOptionCriteria(c);
		senderField.setDisplayField("sms_sender");
		senderField.setValueField("id");
		senderField.setAutoFetchData(false);
		senderField.setDisabled(true);
//		senderField.setDefaultToFirstOption(true);
		senderField.setWidth(250);
		senderField.setRequired(true);
//		senderField.setAddUnknownValues(false);
//		senderField.setFetchMissingValues(true);
		senderField.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent changedEvent) {
				smsOffText.setValue(getSmsOffText());
				countSMS();
			}
		});

		smsOffCheckBox = new CheckboxItem("sms_off_check_box", "SMS OFF");
		smsOffCheckBox.setDefaultValue(true);
		smsOffCheckBox.setShowTitle(false);
		smsOffCheckBox.setColSpan(1);
		smsOffCheckBox.setWidth("100%");
		smsOffCheckBox.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent changedEvent) {
				countSMS();
			}
		});

		smsOffText = new StaticTextItem("sms_off_text");
		smsOffText.setShowTitle(false);
		smsOffText.setColSpan(1);
		smsOffText.setValue(getSmsOffText());
		smsOffText.setAlign(Alignment.RIGHT);

		smsCounter = new StaticTextItem("sms_counter");
		smsCounter.setShowTitle(false);
		smsCounter.setColSpan(2);
		smsCounter.setAlign(Alignment.RIGHT);

		smsField = new TextAreaItem("sms", TicketMaster.constants.sms_sms());
		smsField.setHeight(75);
		smsField.setWidth(250);
		smsField.setRowSpan(4);
		smsField.setTitleVAlign(VerticalAlignment.TOP);
		smsField.setRequired(true);
		smsField.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent keyUpEvent) {
				countSMS();
			}
		});


		nameField = new TextItem("name", TicketMaster.constants.name());

		fileField = new FileItem("file", TicketMaster.constants.sms_file());
		fileField.setRequired(true);

		blankField1 = new StaticTextItem();
		blankField1.setShowTitle(false);
		blankField1.setColSpan(2);

//		blankField1.setRowSpan(2);
		blankField2 = new StaticTextItem();
		blankField2.setShowTitle(false);
		blankField2.setColSpan(2);
		blankField2.setRowSpan(2);
		blankField3 = new StaticTextItem();
		blankField3.setShowTitle(false);
		blankField3.setColSpan(2);
		blankField3.setRowSpan(2);
		blankField4 = new StaticTextItem();
		blankField4.setShowTitle(false);
		blankField4.setColSpan(2);

		IButton resetBtn = new IButton(TicketMaster.constants.sms_reset());
		resetBtn.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent clickEvent) {
				reset();
			}
		});
		resetBtn.setIcon("all.png");

		IButton submitBtn = new IButton(TicketMaster.constants.sms_send());
		submitBtn.setIcon("yes.png");

		submitBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent clickEvent) {
				submit();
			}
		});

		categoryCombo = new SelectItem("category", TicketMaster.constants.sms_category());
		categoryCombo.setOptionDataSource(DataSource.get("SMSBroadcastDS"));
		categoryCombo.setDefaultToFirstOption(true);
		categoryCombo.setDisabled(true);
		categoryCombo.setDisplayField("description");
		categoryCombo.setValueField("layer");
//		categoryCombo.setRequired(true);
		categoryCombo.setOptionOperationId("getCategories");
		categoryCombo.setAutoFetchData(true);

		form.setFields(contractsField, nameField,
				senderField, fileField,
				smsField,
				categoryCombo,
				blankField3,
				blankField4,
				smsOffCheckBox, smsOffText, blankField2,
				smsCounter);
		layout.addMembers(form);
		vLayout = new VLayout();
		HLayout bbar = new HLayout();
		bbar.setAlign(Alignment.RIGHT);
		bbar.setMembersMargin(5);
		bbar.addMembers(resetBtn, submitBtn);
		vLayout.addMembers(layout, bbar);
		setPane(vLayout);
		checkDelivery();
		//loadSenders();
	}

	private void submit(){
		if(!form.validate()) return ;

		DSRequest request = new DSRequest();
		request.setOperationId("uploadNumbers");
		request.setOperationType(DSOperationType.ADD);
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
				smsInfoGrid.refresh(contractsField.getValueAsString());
				smsOffText.setValue(getSmsOffText());
				countSMS();

			}
		}, request);

	}
	private void reset(){
		form.reset();
	}

	private void countSMS(){
		String v = smsField.getValueAsString();
		if(v == null) v = "";

		if(smsOffCheckBox.getValueAsBoolean()) {
			v += getSmsOffText();
		}

		int t = isASCII(v) ? 146 : 56;
		int sms_count = v.length() / t + (v.length() > 0 ? 1 : 0);
		int char_count = v.length() % t;

		smsCounter.setValue((t - char_count) + " / " + sms_count);
		if(sms_count > 2){
			SC.say(TicketMaster.constants.warning(), TicketMaster.constants.sms_smsCountLimitWarning());
			v = smsField.getValueAsString();
			smsField.setValue(v.substring(0, 2*t - getSmsOffText().length()-1));
			countSMS();
		}
	}
	private boolean isASCII(String str) {
		return str.matches("^[\\x00-\\x7F]*$");
	}
	public SmsInfoGrid getSmsInfoGrid() {
		return smsInfoGrid;
	}

	public void setSmsInfoGrid(SmsInfoGrid smsInfoGrid) {
		this.smsInfoGrid = smsInfoGrid;
	}

	private void loadSenders(){
		DataSource ds = DataSource.get("SMSBroadcastDS");
		DSRequest request = new DSRequest();
		request.setOperationType(DSOperationType.FETCH);
		request.setOperationId("getSenders");
		Criteria c = new Criteria();
		c.setAttribute("contract_id", contractsField.getValue());
		c.setAttribute("tab", "uploadNumbers");
		ds.fetchData(c, new DSCallback() {
			@Override
			public void execute(DSResponse dsResponse, Object o, DSRequest dsRequest) {
				LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
				Record[] data = dsResponse.getData();
				for(int i = 0; i < data.length; i++){
					values.put(data[i].getAttributeAsString("id"), data[i].getAttributeAsString("sms_sender"));
				}
				senderField.setValueMap(values);
				senderField.setValue(data[0].getAttributeAsString("id"));
				smsOffText.setValue(getSmsOffText());
				countSMS();
			}
		}, request);
	}

	private void checkDelivery(){
		DataSource ds = DataSource.get("SMSBroadcastDS");
		DSRequest request = new DSRequest();
		request.setOperationId("checkDelivery");
		request.setOperationType(DSOperationType.FETCH);
		ds.fetchData(new Criteria(), new DSCallback() {
			@Override
			public void execute(DSResponse dsResponse, Object o, DSRequest dsRequest) {
				Boolean noDelivery = dsResponse.getData().length > 0 ? dsResponse.getData()[0].getAttributeAsBoolean("no_delivery") : false;
				if (noDelivery) {
					categoryCombo.setDisabled(false);
				}
			}
		}, request);
	}
}
