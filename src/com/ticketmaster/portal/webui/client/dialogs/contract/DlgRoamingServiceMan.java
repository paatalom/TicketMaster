package com.ticketmaster.portal.webui.client.dialogs.contract;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.client.component.mgtdialog.IExceptionCallBack;
import com.ticketmaster.portal.webui.client.component.mgtdialog.IMgtDialog;
import com.ticketmaster.portal.webui.client.component.utils.CompUtils;
import com.ticketmaster.portal.webui.client.utils.ClientUtils;
import com.ticketmaster.portal.webui.shared.utils.Constants;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Progressbar;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class DlgRoamingServiceMan extends IMgtDialog {

	private DynamicForm dynamicForm1;
	private DynamicForm dynamicForm2;

	private SelectItem roamGttListItem;
	private DateItem startDateItem;
	private DateItem endDateItem;
	private SelectItem thresholdType;
	private TextItem thresholdValueItem;

	private TextAreaItem commentItem;
	private TextAreaItem phonesItem;
	private ListGrid phonesStatusesGrid;
	private Progressbar progressbar;

	protected Record contractRecord;

	private ToolStripButton checkBtn;
	private ToolStripButton clearInvalidBtn;
	private ToolStripButton executeBtn;

	private Img arrowImg;

	private int done_cnt = 0;
	private Integer roamGttId;
	private Map<String, Double> phonesAndLimitsMap = null;
	private DateTimeFormat fmt = DateTimeFormat.getFormat("yyyyMMdd");
	private DateTimeFormat fmtNormal = DateTimeFormat.getFormat("dd.MM.yyyy");

	private LinkedHashMap<Integer, String> valueMap;
	private String numbersForMail = "";

	private boolean disabled;

	public DlgRoamingServiceMan(Record contractRecord) {
		try {
			this.contractRecord = contractRecord;
			setWidth(900);
			setHeight(600);

			VLayout mainVLayout = new VLayout();
			mainVLayout.setWidth100();
			mainVLayout.setHeight100();
			mainVLayout.setMembersMargin(10);
			mainVLayout.setPadding(5);

			dynamicForm1 = new DynamicForm();
			dynamicForm1.setAutoFocus(true);
			dynamicForm1.setWidth100();
			dynamicForm1.setPadding(10);
			dynamicForm1.setTitleWidth(250);
			dynamicForm1.setBorder("1px solid #a7abb4");
			dynamicForm1.setNumCols(6);

			roamGttListItem = new SelectItem();
			roamGttListItem.setName("service_code");
			roamGttListItem.setWidth(571);
			roamGttListItem.setTitle(TicketMaster.constants.service());
			roamGttListItem.setColSpan(6);

			commentItem = new TextAreaItem();
			commentItem.setName("comment");
			commentItem.setTitle(TicketMaster.constants.comment());
			commentItem.setWidth(571);
			commentItem.setHeight(50);
			commentItem.setColSpan(6);

			final Map<String, Object> criteriaBOMap = new TreeMap<String, Object>();

			ClientUtils.fillCombo(roamGttListItem, "DescriptionDS", "getCorpRoamingGenTrans", "id", "description",
					criteriaBOMap);

			startDateItem = new DateItem();
			startDateItem.setName("start_date");
			startDateItem.setStartDate(new Date(946670400000L));
			startDateItem.setWidth(200);
			startDateItem.setUseTextField(true);
			startDateItem.setTitle(TicketMaster.constants.start_date());
			startDateItem.setValue(new Date());

			endDateItem = new DateItem();
			endDateItem.setName("end_date");
			endDateItem.setStartDate(new Date(946670400000L));
			endDateItem.setWidth(200);
			endDateItem.setUseTextField(true);
			endDateItem.setTitle(TicketMaster.constants.end_date_till());
			Date endDate = new Date();
			CalendarUtil.addMonthsToDate(endDate, 1);
			endDateItem.setValue(endDate);

			valueMap = new LinkedHashMap<Integer, String>();
			valueMap.put(0, TicketMaster.constants.zero());
			valueMap.put(1, TicketMaster.constants.limited());
			valueMap.put(2, TicketMaster.constants.unlimit());

			thresholdType = new SelectItem();
			thresholdType.setName("limit_type");
			thresholdType.setTitle(TicketMaster.constants.limit_type());
			thresholdType.setWidth(200);
			thresholdType.setValueMap(valueMap);
			thresholdType.setValue(0);
			thresholdType.addChangedHandler(new ChangedHandler() {
				@Override
				public void onChanged(ChangedEvent event) {
					setLimitValue(thresholdType.getValueAsString());
				}
			});

			thresholdValueItem = new TextItem();
			thresholdValueItem.setName("threshold_value");
			thresholdValueItem.setTitle(TicketMaster.constants.limit_gel());
			thresholdValueItem.setWidth(200);
			thresholdValueItem.setValue(0);
			thresholdValueItem.disable();

			dynamicForm1.setItems(roamGttListItem, commentItem, thresholdType, thresholdValueItem, new SpacerItem(),
					startDateItem, endDateItem);

			HLayout hLayout = new HLayout();
			hLayout.setWidth100();
			hLayout.setHeight100();

			dynamicForm2 = new DynamicForm();
			dynamicForm2.setWidth(180);
			dynamicForm2.setPadding(0);
			dynamicForm2.setHeight100();
			dynamicForm2.setBorder("1px solid #a7abb4");
			dynamicForm2.setGroupTitle(TicketMaster.constants.phone_numbers_list());
			dynamicForm2.setIsGroup(true);
			dynamicForm2.setTitlePrefix(" ");
			dynamicForm2.setTitleSuffix(" ");

			phonesItem = new TextAreaItem();
			phonesItem.setTitle(" ");
			phonesItem.setName("phones");
			phonesItem.setWidth(180);
			phonesItem.setHeight("100%");

			dynamicForm2.setFields(phonesItem);

			arrowImg = new Img("arrow_right.png", 32, 32);
			arrowImg.setCursor(Cursor.HAND);
			arrowImg.setLayoutAlign(Alignment.CENTER);
			arrowImg.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					copyDataToGrid();
				}
			});

			HLayout hLayoutImg = new HLayout();
			hLayoutImg.setAlign(Alignment.CENTER);
			hLayoutImg.addMember(arrowImg);

			phonesStatusesGrid = new ListGrid() {
				@Override
				protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
					if (record == null) {
						return super.getCellCSSText(record, rowNum, colNum);
					}
					Integer status = record.getAttributeAsInt("status");
					if (status != null) {
						if (status.equals(-1)) {
							return "color:red;";
						} else if (status.equals(1)) {
							return "color:green;";
						}
					}
					return super.getCellCSSText(record, rowNum, colNum);
				}
			};
			CompUtils.gridDefaultSetings(phonesStatusesGrid);
			phonesStatusesGrid.setDataSource(PhonesListResultDS.getInstance());
			phonesStatusesGrid.setAutoFetchData(true);
			phonesStatusesGrid.setWidth100();
			phonesStatusesGrid.setHeight100();
			phonesStatusesGrid.setCanReorderRecords(true);
			phonesStatusesGrid.setCanSelectAll(false);
			phonesStatusesGrid.setCanSelectText(true);
			phonesStatusesGrid.setCanDragSelectText(true);
			phonesStatusesGrid.setCanRemoveRecords(true);
			phonesStatusesGrid.setShowRowNumbers(true);

			ListGridField phoneField1 = new ListGridField("param_value", TicketMaster.constants.phone());
			phoneField1.setAlign(Alignment.LEFT);
			phoneField1.setWidth(70);

			// ListGridField roaming_service = new
			// ListGridField("roaming_service",
			// TicketMaster.constants.voice_roaming_service());
			// roaming_service.setAlign(Alignment.LEFT);
			// roaming_service.setWidth(180);
			//
			// ListGridField gprs_roaming_service = new
			// ListGridField("gprs_roaming_service",
			// TicketMaster.constants.gprs_roaming_service());
			// gprs_roaming_service.setAlign(Alignment.LEFT);
			// gprs_roaming_service.setWidth(200);

			ListGridField notification = new ListGridField("notification", TicketMaster.constants.notification());
			notification.setAlign(Alignment.LEFT);

			phonesStatusesGrid.setFields(phoneField1, notification);

			hLayout.setMembers(dynamicForm2, hLayoutImg, phonesStatusesGrid);

			HLayout hLayout2 = new HLayout();
			hLayout2.setWidth100();
			hLayout2.setAlign(Alignment.RIGHT);
			hLayout2.setMembersMargin(10);

			HLayout hLayoutProgressbar = new HLayout();
			hLayoutProgressbar.setWidth100();
			hLayoutProgressbar.setAlign(Alignment.RIGHT);
			hLayoutProgressbar.setMembersMargin(10);

			checkBtn = new ToolStripButton();
			checkBtn.setTitle(TicketMaster.constants.check());
			checkBtn.setIcon("check.png");

			clearInvalidBtn = new ToolStripButton();
			clearInvalidBtn.setTitle(TicketMaster.constants.clear_invalid_phones());
			clearInvalidBtn.setIcon("mobile_invalid.png");
			clearInvalidBtn.setWidth(196);

			executeBtn = new ToolStripButton();
			executeBtn.setTitle(TicketMaster.constants.execute());
			executeBtn.setIcon("exec.png");
			executeBtn.setWidth(200);

			ToolStrip menuToolstrip = new ToolStrip();
			menuToolstrip.setWidth100();
			hLayout2.addMember(menuToolstrip);

			menuToolstrip.addButton(checkBtn);
			menuToolstrip.addSeparator();
			menuToolstrip.addButton(clearInvalidBtn);
			menuToolstrip.addFill();
			menuToolstrip.addButton(executeBtn);

			progressbar = new Progressbar();
			progressbar.setVertical(false);
			progressbar.setHeight(24);
			progressbar.setWidth100();

			hLayoutProgressbar.addMember(progressbar);

			mainVLayout.addMembers(dynamicForm1, hLayout2, hLayoutProgressbar, hLayout);
			addMember(mainVLayout);

			clearInvalidBtn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Record recs[] = phonesStatusesGrid.getRecords();
					for (Record record : recs) {
						Integer status = record.getAttributeAsInt("status");
						if (status != null && status.intValue() < 0) {
							phonesStatusesGrid.getDataSource().removeData(record);
						}
					}
				}
			});

			executeBtn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					execOperations();
				}
			});

			checkBtn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					try {
						copyDataToGrid();
					} catch (Exception e) {
						e.printStackTrace();
						SC.say(e.toString());
					}
				}
			});

			roamGttListItem.addChangedHandler(new ChangedHandler() {
				@Override
				public void onChanged(ChangedEvent event) {
					Object value = event.getValue();
					if (value != null) {
						Long gttId = Long.parseLong(value.toString());
						disabled = gttId.equals(Constants.GTT_ROAM_DEL_VOICE_GPRS)
								|| gttId.equals(Constants.GTT_ROAM_DEL_GPRS);

						startDateItem.setDisabled(disabled);
						endDateItem.setDisabled(disabled);
						thresholdType.setDisabled(disabled);
						thresholdValueItem.setDisabled(disabled);
						if (!disabled) {
							setLimitValue(thresholdType.getValueAsString());
						}
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void execOperations() {
		try {
			if (CompUtils.isEmpty(roamGttListItem.getValue())) {
				SC.warn(TicketMaster.constants.please_select() + " : " + roamGttListItem.getTitle());
				return;
			}
			if (CompUtils.isEmpty(phonesItem.getValue())) {
				SC.warn(TicketMaster.constants.please_enter_phones());
				return;
			}

			if (!thresholdType.isDisabled()) {
				if (thresholdValueItem.getValueAsString() == null || thresholdValueItem.getValueAsString().equals("")) {
					SC.warn(TicketMaster.constants.please_enter() + " : " + thresholdValueItem.getTitle());
					return;
				}
				try {
					Double.parseDouble(thresholdValueItem.getValueAsString());
				} catch (NumberFormatException e) {
					SC.warn(TicketMaster.constants.incorrect_field() + " : " + thresholdValueItem.getTitle());
					return;
				}

				if (startDateItem.getValue() == null) {
					SC.warn(TicketMaster.constants.please_select() + " : " + startDateItem.getTitle());
					return;
				}
				try {
					startDateItem.getValueAsDate();
				} catch (Exception e) {
					SC.warn(TicketMaster.constants.incorrect_field() + " : " + startDateItem.getTitle());
					return;
				}

				Integer startDateInt = Integer.parseInt(fmt.format(startDateItem.getValueAsDate()));
				Integer currDateInt = Integer.parseInt(fmt.format(new Date()));
				if (startDateInt.intValue() < currDateInt) {
					SC.warn(TicketMaster.constants.start_date_must_be_after_sysdate());
					return;
				}

				if (!endDateItem.getValueAsDate().after(startDateItem.getValueAsDate())) {
					SC.warn(TicketMaster.constants.end_date_must_be_after_start_date());
					return;
				}

				if (endDateItem.getValue() == null) {
					SC.warn(TicketMaster.constants.please_select() + " : " + endDateItem.getTitle());
					return;
				}
				try {
					endDateItem.getValueAsDate();
				} catch (Exception e) {
					SC.warn(TicketMaster.constants.incorrect_field() + " : " + endDateItem.getTitle());
					return;
				}
			}

			done_cnt = 0;
			if (CompUtils.isEmpty(roamGttListItem.getValue())) {
				SC.warn(TicketMaster.constants.please_select() + " " + roamGttListItem.getTitle());
				return;
			}
			Record records[] = phonesStatusesGrid.getRecords();
			if (records == null || records.length <= 0) {
				SC.warn(TicketMaster.constants.listgrid_is_empty());
				return;
			}

			for (Record record : records) {
				record.getAttributeAsInt("");
				Integer status = record.getAttributeAsInt("status");
				if (status.intValue() < 0) {
					SC.warn(TicketMaster.constants.please_delete_incorrect_phones());
					return;
				}
			}

			progressbar.setPercentDone(0);
			final int size = records.length;

			numbersForMail = "";

			for (final Record record : records) {
				Long subscriber_id = record.getAttributeAsLong("subscriber_id");

				if (record.getAttributeAsInt("status").equals(0)) {
					try {
						Long gttId = Long.parseLong(roamGttListItem.getValueAsString());
						Criteria criteria = new Criteria();
						criteria.setAttribute("subscriber_id", subscriber_id);
						criteria.setAttribute("roamgttid", gttId);
						Long ignore_params = 0L;

						if (gttId.equals(Constants.GTT_ROAM_ADD_GPRS)
								|| gttId.equals(Constants.GTT_ROAM_DEL_VOICE_GPRS)
								|| gttId.equals(Constants.GTT_ROAM_DEL_GPRS)) {
							ignore_params = 1L;
						}

						criteria.setAttribute("ignore_params", ignore_params);
						if (!disabled) {
							if (startDateItem.getValueAsDate() != null) {
								criteria.setAttribute("start_date", startDateItem.getValueAsDate());
							}
							if (endDateItem.getValueAsDate() != null) {
								criteria.setAttribute("end_date", endDateItem.getValueAsDate());
							}
						}
						if (phonesAndLimitsMap.get(record.getAttribute("param_value")) != null) {
							criteria.setAttribute("limit", phonesAndLimitsMap.get(record.getAttribute("param_value")));
						}

						criteria.setAttribute("schedule_id", record.getAttribute("schedule_id"));
						criteria.setAttribute("comment", commentItem.getValueAsString());
						DSRequest dsReq = new DSRequest();
						dsReq.setOperationId("roamingServiceOperation");
						DataSource dsR = DataSource.get("BatchOperationsDS");

						dsR.fetchData(criteria, new DSCallback() {
							@Override
							public void execute(DSResponse response, Object rawData, DSRequest request) {
								if (response.getTotalRows() > 0) {
									if (response.getData()[0].getAttributeAsInt("id").equals(1)) {
										updateRecordStatus(record, 1, size);
									}
								}
							}
						}, dsReq);
						numbersForMail += record.getAttribute("param_value") + "<br>";
					} catch (Exception e) {
						updateRecordStatus(record, -1, size);
						e.printStackTrace();
					}
				}
			}
			if (numbersForMail != "") {
				String email = contractRecord.getAttribute("email");
				String mailText = getWindowTitle();
				mailText += "<br>სერვისი: " + roamGttListItem.getSelectedRecord().getAttribute("description");
				if (!disabled) {
					mailText += "<br>ლიმიტის ტიპი: " + valueMap.get(thresholdType.getValue());
					mailText += "<br>ლიმიტი: " + thresholdValueItem.getValueAsString();
					mailText += "<br>საწყისი თარიღი: " + startDateItem.getValue().toString();
					mailText += "<br>დასრულების თარიღი(მდე): " + endDateItem.getValue().toString();
				}
				mailText += "<br>ორგანიზაცია: " + contractRecord.getAttribute("party_name");
				mailText += "<br>Contract_ID: " + contractRecord.getAttribute("contract_id");
				mailText += "<br>მომხმარებელი: " + email;
				mailText += "<br>ნომრები:";
				mailText += "<br>" + numbersForMail;
				Criteria criteriaMail = new Criteria();
				criteriaMail.setAttribute("email", email);
				criteriaMail.setAttribute("subject",
						getWindowTitle() + " Contract_ID - " + contractRecord.getAttribute("contract_id"));
				criteriaMail.setAttribute("text", mailText);
				DSRequest dsReq = new DSRequest();
				dsReq.setOperationId("sendCorpNotificationMail");
				DataSource dsR = DataSource.get("BatchOperationsDS");

				dsR.fetchData(criteriaMail, new DSCallback() {
					@Override
					public void execute(DSResponse response, Object rawData, DSRequest request) {
					}
				}, dsReq);
			}
		} catch (Exception e) {
			e.printStackTrace();
			SC.warn(e.toString());
		}
	}

	private void updateRecordStatus(Record record, Integer statusValue, int size) {
		try {
			String notif;
			if (statusValue.equals(1)) {
				notif = TicketMaster.constants.success();
			} else {
				notif = TicketMaster.constants.failed();
			}
			done_cnt++;
			int percent = (int) (((double) done_cnt / (double) size) * (double) 100);
			progressbar.setPercentDone(percent);
			record.setAttribute("status", statusValue);
			record.setAttribute("notification", notif);
			if (done_cnt == size) {
				phonesStatusesGrid.redraw();
			}
		} catch (Exception e) {
			e.printStackTrace();
			SC.warn(e.toString());
		}
	}

	private void copyDataToGrid() {
		try {
			if (CompUtils.isEmpty(roamGttListItem.getValue())) {
				SC.warn(TicketMaster.constants.please_select() + " : " + roamGttListItem.getTitle());
				return;
			}
			if (CompUtils.isEmpty(phonesItem.getValue())) {
				SC.warn(TicketMaster.constants.please_enter_phones());
				return;
			}
			if (thresholdValueItem.getValueAsString() == null || thresholdValueItem.getValueAsString().equals("")) {
				SC.warn(TicketMaster.constants.please_enter() + " : " + thresholdValueItem.getTitle());
				return;
			}
			Double thresholdValue = null;
			try {
				thresholdValue = Double.parseDouble(thresholdValueItem.getValueAsString());
			} catch (NumberFormatException e) {
				SC.warn(TicketMaster.constants.incorrect_field() + " : " + thresholdValueItem.getTitle());
				return;
			}

			if (startDateItem.getValue() == null) {
				SC.warn(TicketMaster.constants.please_select() + " : " + startDateItem.getTitle());
				return;
			}
			final Date startDate;
			try {
				startDate = startDateItem.getValueAsDate();
			} catch (Exception e) {
				SC.warn(TicketMaster.constants.incorrect_field() + " : " + startDateItem.getTitle());
				return;
			}

			Integer startDateInt = Integer.parseInt(fmt.format(startDateItem.getValueAsDate()));
			Integer currDateInt = Integer.parseInt(fmt.format(new Date()));
			if (startDateInt.intValue() < currDateInt) {
				SC.warn(TicketMaster.constants.start_date_must_be_after_sysdate());
				return;
			}

			if (!endDateItem.getValueAsDate().after(startDateItem.getValueAsDate())) {
				SC.warn(TicketMaster.constants.end_date_must_be_after_start_date());
				return;
			}

			if (endDateItem.getValue() == null) {
				SC.warn(TicketMaster.constants.please_select() + " : " + endDateItem.getTitle());
				return;
			}
			final Date endDate;
			try {
				endDate = endDateItem.getValueAsDate();
			} catch (Exception e) {
				SC.warn(TicketMaster.constants.incorrect_field() + " : " + endDateItem.getTitle());
				return;
			}

			String phones = phonesItem.getValueAsString();
			phonesAndLimitsMap = new TreeMap<String, Double>();

			String phonesArr[] = phones.split("\n");
			if (phonesArr == null || phonesArr.length <= 0) {
				SC.warn(TicketMaster.constants.please_enter_phones());
				return;
			}

			List<String> phonesList = new ArrayList<String>();
			roamGttId = Integer.valueOf(roamGttListItem.getValue().toString());

			for (String phone_str : phonesArr) {
				String phone_limit[] = phone_str.split("\\s+");
				if (phone_limit != null && phone_limit.length == 2) {
					phone_str = phone_limit[0];
				}
				try {
					Long.parseLong(phone_str);
				} catch (NumberFormatException e) {
					SC.warn(TicketMaster.constants.invalid_phone() + " : " + phone_str);
					return;
				}
				if (phonesList.contains(phone_str)) {
					SC.warn(TicketMaster.constants.duplicated_phone() + " : " + phone_str);
					return;
				}

				Double limit = null;
				try {
					if (phone_limit != null && phone_limit.length == 2) {
						limit = Double.parseDouble(phone_limit[1]);
					} else if (!CompUtils.isEmpty(thresholdValueItem.getValueAsString())) {
						limit = Double.parseDouble(thresholdValueItem.getValueAsString());
					}
				} catch (NumberFormatException e) {
					SC.warn(TicketMaster.constants.invalid_phone() + " : " + phone_str);
					return;
				}
				phonesAndLimitsMap.put(phone_str.trim(), limit);

				phonesList.add(phone_str);
			}
			if (phonesList.size() > Constants.LIST_BATCH_SIZE) {
				SC.warn(TicketMaster.constants.phone_list_max_size() + " : " + Constants.LIST_BATCH_SIZE);
				return;
			}
			String phone_list = phonesList.toString().replace("[", "").replace("]", "");
			Criteria criteria = new Criteria();
			criteria.setAttribute("phone_list", phone_list);
			criteria.setAttribute("subscriber_type_id", Constants.SUBSCRIBER_TYPE_MOBILE);
			criteria.setAttribute("contract_id", contractRecord.getAttributeAsLong("contract_id"));
			criteria.setAttribute("roamgttid", roamGttId);
			criteria.setAttribute("thresholdValue", thresholdValue);
			criteria.setAttribute("start_date", startDate);
			criteria.setAttribute("end_date", endDate);

			DSRequest dsRequest = new DSRequest();
			dsRequest.setOperationId("getRoamingList");

			DataSource dataSource = DataSource.get("FreeListsDS");
			dataSource.fetchData(criteria, new DSCallback() {

				@Override
				public void execute(DSResponse response, Object rawData, DSRequest request) {
					Record data[] = response.getData();
					Record recs[] = phonesStatusesGrid.getRecords();
					for (Record record : recs) {
						phonesStatusesGrid.getDataSource().removeData(record);
					}
					for (Record record : data) {
						try {
							String val = roamGttListItem.getValueAsString();
							boolean disabled = false;
							if (val != null && !val.trim().equals("")) {
								Long gttId = Long.parseLong(val);
								disabled = gttId.equals(Constants.GTT_ROAM_ADD_GPRS)
										|| gttId.equals(Constants.GTT_ROAM_DEL_GPRS)
										|| gttId.equals(Constants.GTT_ROAM_DEL_VOICE_GPRS);
							}

							Integer status = record.getAttributeAsInt("status");
							if (status != null && status.intValue() >= 0) {
								if (!disabled) {
									Object limit = phonesAndLimitsMap.get(record.getAttribute("param_value"));
									record.setAttribute(
											"notification",
											(limit == null ? "" : (TicketMaster.constants.limit() + " : " + limit
													.toString()))
													+ (startDate == null ? ""
															: (limit == null ? TicketMaster.constants.start_date()
																	+ " : " : ", "
																	+ TicketMaster.constants.start_date() + " : ")
																	+ fmtNormal.format(startDate))
													+ (endDate == null ? ""
															: (startDate == null && limit == null ? TicketMaster.constants
																	.end_date() + " : "
																	: ", " + TicketMaster.constants.end_date()
																			+ " : ")
																	+ fmtNormal.format(endDate)));
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						phonesStatusesGrid.getDataSource().addData(record);
					}
				}
			}, dsRequest);
		} catch (Exception e) {
			e.printStackTrace();
			SC.warn(e.toString());
		}
	}

	@Override
	public void fillFields(Record record) {
	}

	@Override
	public void validateSave(IExceptionCallBack callBack) {
	}

	@Override
	public Record getRecordForSave() {
		return null;
	}

	@Override
	public String getDataSourceName() {
		return null;
	}

	@Override
	public String getSaveOperation() {
		return null;
	}

	@Override
	public String getIdField() {
		return null;
	}

	@Override
	public String getWindowTitle() {
		return TicketMaster.constants.roaming_service_management();
	}

	@Override
	public boolean saveOnServer() {
		return false;
	}

	private void setLimitValue(String value) {
		try {
			Integer limValue = Integer.valueOf(value.toString());
			switch (limValue) {
			case 0:
				thresholdValueItem.setValue(0);
				thresholdValueItem.disable();
				break;
			case 1:
				thresholdValueItem.setValue("");
				thresholdValueItem.enable();
				break;
			case 2:
				thresholdValueItem.setValue(100000);
				thresholdValueItem.disable();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			SC.say(e.toString());
		}
	}
}
