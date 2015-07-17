package com.ticketmaster.portal.webui.client.dialogs.contract;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.client.component.mgtdialog.IExceptionCallBack;
import com.ticketmaster.portal.webui.client.component.mgtdialog.IMgtDialog;
import com.ticketmaster.portal.webui.client.component.utils.CompUtils;
import com.ticketmaster.portal.webui.client.utils.CommonSingleton;
import com.ticketmaster.portal.webui.shared.utils.Constants;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.ExportDisplay;
import com.smartgwt.client.types.ExportFormat;
import com.smartgwt.client.util.EnumUtil;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Progressbar;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
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

public class DlgLimitManagement extends IMgtDialog {

	private DynamicForm dynamicForm;
	private DynamicForm dynamicForm1;
	// private SelectItem subscriberType;
	// private SelectItem batchOperationItems;
	private SelectItem thresholdType;
	public TextItem thresholdValue;
	private DateItem changeDateItem;

	private TextAreaItem commentItem;
	private TextAreaItem phonesItem;
	private ListGrid phonesStatusesGrid;
	private DataSource phonesStatusesDS;
	private Progressbar progressbar;

	protected Record contractRecord;

	private ToolStrip menuToolstrip;
	private ToolStripButton checkBtn;
	private ToolStripButton clearInvalidBtn;
	private ToolStripButton executeBtn;
	private ToolStripButton excelExportBtn;

	private Img arrowImg;

	private int done_cnt = 0;
	private Integer serviceId;

	private Map<String, Double> phonesAndLimitsMap;

	private LinkedHashMap<Integer, String> valueMap;
	private String numbersForMail = "";

	public DlgLimitManagement(Record contractRecord) {
		try {
			this.contractRecord = contractRecord;
			setWidth(900);
			setHeight(600);

			VLayout mainVLayout = new VLayout();
			mainVLayout.setWidth100();
			mainVLayout.setHeight100();
			mainVLayout.setMembersMargin(10);
			mainVLayout.setPadding(5);

			dynamicForm = new DynamicForm();
			dynamicForm.setAutoFocus(true);
			dynamicForm.setWidth100();
			dynamicForm.setPadding(10);
			dynamicForm.setTitleWidth(150);
			dynamicForm.setBorder("1px solid #a7abb4");
			dynamicForm.setNumCols(4);
			//
			// subscriberType = new SelectItem();
			// subscriberType.setName("subscriber_type");
			// subscriberType.setWidth(300);
			// subscriberType.setTitle(TicketMaster.constants.subscriber_type());
			// subscriberType.setDefaultToFirstOption(true);
			//
			// batchOperationItems = new SelectItem();
			// batchOperationItems.setName("service_code");
			// batchOperationItems.setWidth(300);
			// batchOperationItems.setTitle(TicketMaster.constants.service());
			// batchOperationItems.setDefaultToFirstOption(true);

			commentItem = new TextAreaItem();
			commentItem.setName("comment");
			commentItem.setTitle(TicketMaster.constants.comment());
			commentItem.setWidth(750);
			commentItem.setHeight(40);
			commentItem.setColSpan(4);
			// commentItem.setTitleOrientation(TitleOrientation.TOP);

			valueMap = new LinkedHashMap<Integer, String>();
			valueMap.put(0, TicketMaster.constants.zero());
			valueMap.put(1, TicketMaster.constants.limited());
			valueMap.put(2, TicketMaster.constants.unlimit());

			thresholdType = new SelectItem();
			thresholdType.setName("limit_type");
			thresholdType.setTitle(TicketMaster.constants.limit_type());
			thresholdType.setWidth(300);
			thresholdType.setValueMap(valueMap);
			thresholdType.setDisplayField("");
			thresholdType.addChangedHandler(new ChangedHandler() {
				@Override
				public void onChanged(ChangedEvent event) {
					setLimitValue(event.getValue());
				}
			});

			thresholdValue = new TextItem();
			thresholdValue.setName("threshold_value");
			thresholdValue.setTitle(TicketMaster.constants.limit_gel());
			thresholdValue.setWidth(300);
			thresholdValue.disable();

			Map<String, Object> criteriaSTMap = new TreeMap<String, Object>();
			criteriaSTMap.put("languageId", CommonSingleton.getInstance().getLanguageId());
			criteriaSTMap.put("contract_id", contractRecord.getAttributeAsLong("contract_id"));
			// ClientUtils.fillCombo(subscriberType, "DescriptionDS",
			// "getSubscriberTypes", "id", "description",
			// criteriaSTMap);

			final Map<String, Object> criteriaBOMap = new TreeMap<String, Object>();
			criteriaBOMap.put("languageId", CommonSingleton.getInstance().getLanguageId());
			criteriaBOMap.put("contract_type_id", contractRecord.getAttributeAsLong("contract_type_id"));
			criteriaBOMap.put("subscriber_type_id", -1000);

			// ClientUtils.fillCombo(batchOperationItems, "DescriptionDS",
			// "getBatchOperations", "id", "description",
			// criteriaBOMap);
			// batchOperationItems.setDefaultToFirstOption(true);
			//
			// ClientUtils.makeDependancy(subscriberType, "subscriber_type_id",
			// criteriaBOMap, batchOperationItems);
			//
			// subscriberType.setDefaultToFirstOption(true);

			changeDateItem = new DateItem();
			changeDateItem.setName("change_date");
			changeDateItem.setStartDate(new Date(946670400000L));
			changeDateItem.setWidth(300);
			changeDateItem.setUseTextField(true);
			changeDateItem.setTitle(TicketMaster.constants.change_date());
			changeDateItem.setValue(new Date());
			// subscriberType, batchOperationItems,
			dynamicForm.setItems(thresholdType, thresholdValue, changeDateItem, commentItem);

			dynamicForm1 = new DynamicForm();
			dynamicForm1.setWidth(200);
			dynamicForm1.setPadding(0);
			dynamicForm1.setHeight100();
			dynamicForm1.setBorder("1px solid #a7abb4");
			dynamicForm1.setGroupTitle(TicketMaster.constants.phone_numbers_list());
			dynamicForm1.setIsGroup(true);
			dynamicForm1.setTitlePrefix(" ");
			dynamicForm1.setTitleSuffix(" ");

			phonesItem = new TextAreaItem();
			phonesItem.setTitle(" ");
			phonesItem.setName("phones");
			phonesItem.setWidth(200);
			phonesItem.setHeight("100%");

			dynamicForm1.setFields(phonesItem);

			arrowImg = new Img("arrow_right.png", 32, 32);
			arrowImg.setCursor(Cursor.HAND);
			arrowImg.setLayoutAlign(Alignment.CENTER);
			arrowImg.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					copyDataToGrid(false);
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
			phonesStatusesDS = com.ticketmaster.portal.webui.client.ds.contract.PhonesListResultDS.getInstance();
			CompUtils.gridDefaultSetings(phonesStatusesGrid);
			phonesStatusesGrid.setDataSource(phonesStatusesDS);
			phonesStatusesGrid.setAutoFetchData(true);
			phonesStatusesGrid.setWidth100();
			phonesStatusesGrid.setHeight100();
			phonesStatusesGrid.setCanReorderRecords(true);
			phonesStatusesGrid.setCanSelectAll(false);
			phonesStatusesGrid.setCanSelectText(true);
			phonesStatusesGrid.setCanDragSelectText(true);
			phonesStatusesGrid.setCanRemoveRecords(true);
			phonesStatusesGrid.setShowRowNumbers(true);

			ListGridField subscriberId = new ListGridField();
			subscriberId.setName("subscriber_id");
			subscriberId.setTitle(TicketMaster.constants.ident());
			subscriberId.setAlign(Alignment.LEFT);
			subscriberId.setWidth(100);

			ListGridField phoneField1 = new ListGridField();
			phoneField1.setName("param_value");
			phoneField1.setTitle(TicketMaster.constants.phone());
			phoneField1.setAlign(Alignment.LEFT);
			phoneField1.setWidth(100);

			ListGridField notification = new ListGridField();
			notification.setName("notification");
			notification.setTitle(TicketMaster.constants.notification());
			notification.setAlign(Alignment.LEFT);

			phonesStatusesGrid.setFields(subscriberId, phoneField1, notification);

			HLayout hLayout = new HLayout();
			hLayout.setWidth100();
			hLayout.setHeight100();

			hLayout.setMembers(dynamicForm1, hLayoutImg, phonesStatusesGrid);

			HLayout hLayout2 = new HLayout();
			hLayout2.setWidth100();
			hLayout2.setAlign(Alignment.RIGHT);
			hLayout2.setMembersMargin(10);

			checkBtn = new ToolStripButton();
			checkBtn.setTitle(TicketMaster.constants.check());
			checkBtn.setIcon("check.png");

			clearInvalidBtn = new ToolStripButton();
			clearInvalidBtn.setTitle(TicketMaster.constants.clear_invalid_phones());
			clearInvalidBtn.setIcon("mobile_invalid.png");

			executeBtn = new ToolStripButton();
			executeBtn.setTitle(TicketMaster.constants.execute());
			executeBtn.setIcon("exec.png");

			excelExportBtn = new ToolStripButton();
			excelExportBtn.setTitle(TicketMaster.constants.export());
			excelExportBtn.setIcon("excel.gif");

			menuToolstrip = new ToolStrip();
			menuToolstrip.setWidth100();
			hLayout2.addMember(menuToolstrip);

			menuToolstrip.addButton(checkBtn);
			menuToolstrip.addSeparator();
			menuToolstrip.addButton(clearInvalidBtn);
			menuToolstrip.addSeparator();
			menuToolstrip.addButton(excelExportBtn);
			menuToolstrip.addFill();
			menuToolstrip.addButton(executeBtn);

			progressbar = new Progressbar();
			progressbar.setVertical(false);
			progressbar.setHeight(24);
			progressbar.setWidth100();

			HLayout hLayoutProgressbar = new HLayout();
			hLayoutProgressbar.setWidth100();
			hLayoutProgressbar.setAlign(Alignment.RIGHT);
			hLayoutProgressbar.setMembersMargin(10);

			hLayoutProgressbar.addMember(progressbar);
			mainVLayout.addMembers(dynamicForm, hLayout2, hLayoutProgressbar, hLayout);
			addMember(mainVLayout);

			checkBtn.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					copyDataToGrid(false);
				}
			});

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

			excelExportBtn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					copyDataToGrid(true);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setLimitValue(Object value) {
		Integer limValue = Integer.valueOf(value.toString());
		switch (limValue) {
		case 0:
			thresholdValue.setValue(0);
			thresholdValue.disable();
			break;
		case 1:
			thresholdValue.setValue("");
			thresholdValue.enable();
			dynamicForm.focusInItem(thresholdValue);
			break;
		case 2:
			thresholdValue.setValue(100000);
			thresholdValue.disable();
			break;
		default:
			break;
		}
	}

	private void execOperations() {
		try {
			if (changeDateItem.getValue() == null) {
				SC.warn(TicketMaster.constants.please_select() + " : " + changeDateItem.getTitle());
				return;
			}
			done_cnt = 0;
			// if (CompUtils.isEmpty(batchOperationItems.getValue())) {
			// SC.warn(TicketMaster.constants.please_select() + " " +
			// batchOperationItems.getTitle());
			// return;
			// }
			Record records[] = phonesStatusesGrid.getRecords();
			if (records == null || records.length <= 0) {
				SC.warn(TicketMaster.constants.listgrid_is_empty());
				return;
			}

			progressbar.setPercentDone(0);
			final int size = records.length;

			numbersForMail = "";

			for (final Record record : records) {
				Long subscriber_id = record.getAttributeAsLong("subscriber_id");

				if (record.getAttributeAsInt("status").equals(0)) {
					try {
						Criteria criteria = new Criteria();
						criteria.setAttribute("subscriber_id", subscriber_id);
						criteria.setAttribute("service_id", 1563L);
						criteria.setAttribute("change_date", changeDateItem.getValueAsDate());
						if (record.getAttribute("limit") != null) {
							criteria.setAttribute("limit", record.getAttribute("limit"));
						}
						if (commentItem != null && commentItem.getValueAsString() != null) {
							criteria.setAttribute("comment", commentItem.getValueAsString());
						}
						criteria.setAttribute("schedule_id", record.getAttributeAsString("schedule_id"));

						DSRequest dsReq = new DSRequest();
						dsReq.setOperationId("execBatchOperation");
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
				mailText += "<br>ლიმიტის ტიპი: " + valueMap.get(thresholdType.getValue());
				mailText += "<br>ლიმიტი: " + thresholdValue.getValueAsString();
				mailText += "<br>შეცვლის თარიღი: " + changeDateItem.getValue().toString();
				mailText += "<br>ორგანიზაცია: " + contractRecord.getAttribute("party_name");
				mailText += "<br>Contract_ID: " + contractRecord.getAttribute("contract_id");
				mailText += "<br>ანგარიშის მმართველი: " + email;
				mailText += "<br>ნომრები:";
				mailText += "<br>" + numbersForMail;
				System.out.println(mailText);

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

	private void copyDataToGrid(boolean isExport) {
		try {
			if (CompUtils.isEmpty(thresholdType.getValue())) {
				SC.warn(TicketMaster.constants.please_select() + " " + thresholdType.getTitle());
				return;
			}
			if (CompUtils.isEmpty(thresholdValue.getValue())) {
				SC.warn(TicketMaster.constants.please_select() + " " + thresholdValue.getTitle());
				return;
			}
			if (CompUtils.isEmpty(phonesItem.getValue())) {
				SC.warn(TicketMaster.constants.please_enter_phones());
				return;
			}
			Date limitDate = null;
			try {
				limitDate = changeDateItem.getValueAsDate();
			} catch (Exception e) {
				SC.warn(TicketMaster.constants.incorrect_field() + changeDateItem.getTitle());
				return;
			}
			if (limitDate == null) {
				SC.warn(TicketMaster.constants.please_enter() + changeDateItem.getTitle());
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
			serviceId = Integer.valueOf(1563);

			for (String phone_str : phonesArr) {
				String phone_limit[] = null;

				if (serviceId.equals(1562) || serviceId.equals(1563)) {
					phone_limit = phone_str.split("\\s+");
					if (phone_limit.length == 2) {
						phone_str = phone_limit[0];
					}
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
				if (serviceId.equals(1562) || serviceId.equals(1563)) {
					Double limit = null;
					try {
						if (phone_limit.length == 2) {
							limit = Double.parseDouble(phone_limit[1]);
						} else if (!CompUtils.isEmpty(thresholdValue)) {
							limit = Double.parseDouble(thresholdValue.getValue().toString());
						}
					} catch (NumberFormatException e) {
						SC.warn(TicketMaster.constants.invalid_phone() + " : " + phone_str);
						return;
					}
					phonesAndLimitsMap.put(phone_str.trim(), limit);
				}
				phonesList.add(phone_str);
			}
			if (phonesList.size() > Constants.LIST_BATCH_SIZE) {
				SC.warn(TicketMaster.constants.phone_list_max_size() + " : " + Constants.LIST_BATCH_SIZE);
				return;
			}
			String phone_list = phonesList.toString().replace("[", "").replace("]", "");
			Criteria criteria = new Criteria();
			criteria.setAttribute("languageId", CommonSingleton.getInstance().getLanguageId());
			criteria.setAttribute("phone_list", phone_list);
			criteria.setAttribute("subscriber_type_id", 41L);
			criteria.setAttribute("contract_id", contractRecord.getAttributeAsLong("contract_id"));
			criteria.setAttribute("thrgttid", serviceId);
			criteria.setAttribute("limit_date", limitDate);

			DataSource dataSource = DataSource.get("FreeListsDS");
			DSRequest dsRequest = new DSRequest();
			dsRequest.setOperationId("getFreeListForBatchOperations");

			if (isExport) {
				dsRequest.setExportAs((ExportFormat) EnumUtil.getEnum(ExportFormat.values(), "xls"));
				dsRequest.setExportDisplay(ExportDisplay.DOWNLOAD);
				dsRequest.setExportFields(new String[] { "subscriber_id", "param_value", "notification", "status" });

				dataSource.exportData(criteria, dsRequest, new DSCallback() {
					@Override
					public void execute(DSResponse response, Object rawData, DSRequest request) {
					}
				});
			} else {
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
								if ((serviceId.equals(1562) || serviceId.equals(1563))
										&& record.getAttributeAsInt("status").equals(0)) {
									record.setAttribute("limit",
											phonesAndLimitsMap.get(record.getAttribute("param_value")));
									if (record.getAttribute("limit") == null) {
										record.setAttribute("status", -1);
										record.setAttribute("notification", TicketMaster.constants.please_enter()
												+ TicketMaster.constants.limit());
									} else {
										record.setAttribute("notification",
												phonesAndLimitsMap.get(record.getAttribute("param_value")) + " "
														+ TicketMaster.constants.limit());
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							phonesStatusesGrid.getDataSource().addData(record);
						}
					}
				}, dsRequest);
			}
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
		return TicketMaster.constants.limit_management();
	}

	@Override
	public boolean saveOnServer() {
		return false;
	}
}
