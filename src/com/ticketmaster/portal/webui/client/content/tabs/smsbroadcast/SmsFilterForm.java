package com.ticketmaster.portal.webui.client.content.tabs.smsbroadcast;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.shared.smsBroadcast.Util;
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

import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Created by vano on 3/5/15.
 */
public class SmsFilterForm extends Tab {
	private static SmsFilterForm instance;
	private VLayout vLayout;
	private HLayout hLayout;
	private DynamicForm form1;
	private DynamicForm form2;
	private DynamicForm form3;
	private SelectItem contractsField;
	private SelectItem regionField;
	private SelectItem districtField;
	private SelectItem typeField;
	private SelectItem phoneTypeField;
	private SelectItem chargeField;
	private SelectItem genderField;
	private SelectItem senderField;
	private TextAreaItem smsField;
	private StaticTextItem smsCounter;
	private StaticTextItem smsOffText;
	private SmsInfoGrid smsInfoGrid;
	private TextItem campaignNameField;
	private SelectItem limitField;
	private CheckboxItem smsOffCheckBox;
	private String getSmsOffText(){
		return " SMS Off 91521 Text: " + senderField.getDisplayValue();
	}


	public static SmsFilterForm getInstance(){
		if(instance == null){
			instance = new SmsFilterForm();
		}
		return instance;
	}

	public SmsFilterForm(){
		setTitle(TicketMaster.constants.sms_tabFilter());
		hLayout = new HLayout();
		hLayout.setHeight100();
		hLayout.setWidth100();

		form1 = new DynamicForm();
		form1.setNumCols(2);
		form1.setPadding(5);
		form1.setWidth(330);
		form1.setColWidths(80, "*");

		form2 = new DynamicForm();
		form2.setNumCols(2);
		form2.setPadding(5);
		form2.setWidth(280);
		form2.setColWidths(130, "*");

		form3 = new DynamicForm();
		form3.setNumCols(2);
		form3.setPadding(5);
		form3.setWidth(280);
		form3.setColWidths(130, "*");

		Criteria c = new Criteria();
		c.setAttribute("tab", "filter");

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
		senderField.setDisplayField("sms_sender");
		senderField.setValueField("id");
//		senderField.setOptionCriteria(c);
		senderField.setAutoFetchData(false);
//		senderField.setDefaultToFirstOption(true);
		senderField.setWidth(250);
		senderField.setDisabled(true);
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

		smsOffText = new StaticTextItem("sms_off_text");
		smsOffText.setShowTitle(false);
		smsOffText.setColSpan(1);
		smsOffText.setValue(getSmsOffText());
		smsOffText.setAlign(Alignment.RIGHT);

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

		smsCounter = new StaticTextItem("sms_counter");
		smsCounter.setShowTitle(false);
		smsCounter.setColSpan(2);
		smsCounter.setAlign(Alignment.RIGHT);

		smsField = new TextAreaItem("sms", TicketMaster.constants.sms_sms());
		smsField.setHeight(75);
		smsField.setWidth(250);
		smsField.setTitleVAlign(VerticalAlignment.TOP);
		smsField.setRowSpan(2);
		smsField.setRequired(true);
		smsField.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent keyUpEvent) {
				countSMS();
			}
		});


		regionField = new SelectItem("region", TicketMaster.constants.sms_region());
		regionField.setOptionDataSource(DataSource.get("SMSBroadcastDS"));
		regionField.setOptionOperationId("getRegions");
		regionField.setAutoFetchData(true);
		regionField.setDisplayField("region_name_ge");
		regionField.setValueField("region_id");
		regionField.setDefaultValue(0);

		regionField.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent changedEvent) {
				changeDistricts();
			}
		});

		districtField = new SelectItem("district", TicketMaster.constants.sms_district());
		districtField.setOptionDataSource(DataSource.get("SMSBroadcastDS"));
		districtField.setOptionOperationId("getDistricts");
		districtField.setAutoFetchData(false);
		districtField.setDisplayField("district_name_ge");
		districtField.setValueField("district_id");
		districtField.setAllowEmptyValue(true);
		districtField.setDisabled(true);

		typeField = new SelectItem("type", TicketMaster.constants.sms_subscriberType());
		typeField.setOptionDataSource(DataSource.get("SMSBroadcastDS"));
		typeField.setOptionOperationId("getPartyTypes");
		typeField.setAutoFetchData(true);
		typeField.setDisplayField("name");
		typeField.setValueField("id");
		typeField.setDefaultValue(0);

		phoneTypeField = new SelectItem("phone_type", TicketMaster.constants.sms_phoneType());
		phoneTypeField.setOptionDataSource(DataSource.get("SMSBroadcastDS"));
		phoneTypeField.setOptionOperationId("getPhoneTypes");
		phoneTypeField.setAutoFetchData(true);
		phoneTypeField.setDisplayField("name");
		phoneTypeField.setValueField("id");
		phoneTypeField.setDefaultValue(0);

		chargeField = new SelectItem("charge", TicketMaster.constants.sms_charge());
		chargeField.setOptionDataSource(DataSource.get("SMSBroadcastDS"));
		chargeField.setOptionOperationId("getCharges");
		chargeField.setAutoFetchData(true);
		chargeField.setDisplayField("name");
		chargeField.setValueField("id");
		chargeField.setDefaultValue(0);

		genderField = new SelectItem("gender", TicketMaster.constants.sms_gender());
		genderField.setOptionDataSource(DataSource.get("SMSBroadcastDS"));
		genderField.setOptionOperationId("getGenders");
		genderField.setAutoFetchData(true);
		genderField.setDisplayField("name");
		genderField.setValueField("id");
		genderField.setDefaultValue(0);

		campaignNameField = new TextItem("name", TicketMaster.constants.name());

		limitField = new SelectItem("limit", TicketMaster.constants.sms_limit());
		LinkedHashMap<Integer, String> limitData = new LinkedHashMap<Integer, String>();
		limitData.put(0, TicketMaster.constants.sms_unlimited());
		limitData.put(10000, "10 000");
		limitField.setValueMap(limitData);
		limitField.setDefaultValue("0");

		IButton resetBtn = new IButton(TicketMaster.constants.sms_reset());
		resetBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent clickEvent) {
				reset();
			}
		});
		resetBtn.setIcon("all.png");
		IButton submitBtn = new IButton(TicketMaster.constants.sms_send());
		submitBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent clickEvent) {
				submit();
			}
		});
		submitBtn.setIcon("yes.png");
		form1.setFields(contractsField, senderField, smsField, smsOffCheckBox, smsOffText, smsCounter);
		form2.setFields(campaignNameField, regionField, districtField, typeField);
		form3.setFields(phoneTypeField, chargeField, genderField, limitField);

		hLayout.addMembers(form1, form2, form3);

		vLayout = new VLayout();
		HLayout bbar = new HLayout();
		bbar.setAlign(Alignment.RIGHT);
		bbar.setMembersMargin(5);
		bbar.addMembers(resetBtn, submitBtn);
		vLayout.addMembers(hLayout, bbar);
		setPane(vLayout);
//		loadSenders();
	}

	private void submit(){
		if(!form1.validate() || !form2.validate() || !form3.validate()) return ;

		Criteria c = form1.getValuesAsCriteria();
		c.addCriteria(form2.getValuesAsCriteria());
		c.addCriteria(form3.getValuesAsCriteria());

		if(!isValidFilter()){
			SC.say(TicketMaster.constants.sms_warning(), TicketMaster.constants.sms_wrongFilter());
			return ;
		}

		DataSource ds = DataSource.get("SMSBroadcastDS");
		DSRequest request = new DSRequest();
		request.setOperationId("sendFilteredSms");
		request.setOperationType(DSOperationType.FETCH);

		ds.fetchData(c, new DSCallback() {
			@Override
			public void execute(DSResponse dsResponse, Object o, DSRequest dsRequest) {
				String status = dsResponse.getData()[0].getAttributeAsString("status");
				if(status != null && status.isEmpty()){
					SC.say(TicketMaster.constants.status(), TicketMaster.constants.sms_success());
					getSmsInfoGrid().refresh(contractsField.getValueAsString());
					reset();
					smsOffText.setValue(getSmsOffText());
					countSMS();
				} else {
					SC.say(TicketMaster.constants.status(), status);
				}
			}
		}, request);
	}
	private void reset(){
		form1.reset();
		form2.reset();
		form3.reset();
	}

	private void changeDistricts(){
		Integer regionId = (Integer) regionField.getValue();
		Criteria c = new Criteria();
		c.setAttribute("timestamp", new Date().getTime());
		c.setAttribute("region_id", regionId);
		districtField.setOptionCriteria(c);
		districtField.setDisabled(regionId == 0);
		districtField.fetchData(new DSCallback() {
			@Override
			public void execute(DSResponse dsResponse, Object o, DSRequest dsRequest) {
				districtField.setValue(0);
			}
		});
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

	private boolean isValidFilter(){
		Integer region = (Integer) regionField.getValue();
		Integer district = (Integer) districtField.getValue();
		Integer type = (Integer) typeField.getValue();
		Integer phoneType = (Integer) phoneTypeField.getValue();
		Integer charge = (Integer) chargeField.getValue();
		Integer gender = (Integer) genderField.getValue();

		return Util.isValidFilter(region, district, type, phoneType, charge, gender);
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
		c.setAttribute("tab", "filter");
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
}
