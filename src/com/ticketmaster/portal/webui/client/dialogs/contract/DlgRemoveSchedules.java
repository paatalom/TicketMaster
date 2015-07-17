package com.ticketmaster.portal.webui.client.dialogs.contract;

import java.util.ArrayList;
import java.util.List;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.client.component.mgtdialog.IExceptionCallBack;
import com.ticketmaster.portal.webui.client.component.mgtdialog.IMgtDialog;
import com.ticketmaster.portal.webui.client.component.utils.CompUtils;
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
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class DlgRemoveSchedules extends IMgtDialog {
	private DynamicForm dynamicForm;

	private TextAreaItem phonesItem;
	private ListGrid phonesStatusesGrid;
	private Progressbar progressbar;

	protected Record contractRecord;

	private ToolStripButton checkBtn;
	private ToolStripButton clearInvalidBtn;
	private ToolStripButton executeBtn;
	private int done_cnt = 0;

	public static final int TYPE_ROAMING = 1;
	public static final int TYPE_LIMIT = 2;
	private int type;

	private String numbersForMail = "";

	public DlgRemoveSchedules(Record contractRecord, int type) {
		try {
			setWidth(900);
			setHeight(600);

			this.contractRecord = contractRecord;
			this.type = type;

			VLayout mainVLayout = new VLayout();
			mainVLayout.setWidth100();
			mainVLayout.setHeight100();
			mainVLayout.setMembersMargin(10);
			mainVLayout.setPadding(5);

			HLayout hLayout = new HLayout();
			hLayout.setWidth100();
			hLayout.setHeight100();

			dynamicForm = new DynamicForm();
			dynamicForm.setAutoFocus(true);
			dynamicForm.setWidth(200);
			dynamicForm.setPadding(0);
			dynamicForm.setHeight100();
			dynamicForm.setBorder("1px solid #a7abb4");
			dynamicForm.setGroupTitle(TicketMaster.constants.phone_numbers_list());
			dynamicForm.setIsGroup(true);
			dynamicForm.setTitlePrefix(" ");
			dynamicForm.setTitleSuffix(" ");

			phonesItem = new TextAreaItem();
			phonesItem.setTitle(" ");
			phonesItem.setName("phones");
			phonesItem.setWidth(200);
			phonesItem.setHeight("100%");

			dynamicForm.setFields(phonesItem);

			Img arrowImg = new Img("arrow_right.png", 32, 32);
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
			phoneField1.setWidth(80);

			ListGridField notification = new ListGridField("notification", TicketMaster.constants.notification());
			notification.setAlign(Alignment.LEFT);

			phonesStatusesGrid.setFields(phoneField1, notification);

			hLayout.setMembers(dynamicForm, hLayoutImg, phonesStatusesGrid);

			HLayout hLayout2 = new HLayout();
			hLayout2.setWidth100();
			hLayout2.setAlign(Alignment.RIGHT);
			hLayout2.setMembersMargin(10);

			HLayout hLayoutProgressbar = new HLayout();
			hLayoutProgressbar.setWidth100();
			hLayoutProgressbar.setAlign(Alignment.RIGHT);
			hLayoutProgressbar.setMembersMargin(10);

			checkBtn = new ToolStripButton(TicketMaster.constants.check(), "check.png");
			clearInvalidBtn = new ToolStripButton(TicketMaster.constants.clear_invalid_phones(),
					"mobile_invalid.png");
			clearInvalidBtn.setWidth(196);

			executeBtn = new ToolStripButton(TicketMaster.constants.execute(), "exec.png");
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

			mainVLayout.addMembers(hLayout2, hLayoutProgressbar, hLayout);
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

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void execOperations() {
		try {
			if (CompUtils.isEmpty(phonesItem.getValue())) {
				SC.warn(TicketMaster.constants.please_enter_phones());
				return;
			}
			done_cnt = 0;
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
						Criteria criteria = new Criteria();
						criteria.setAttribute("subscriber_id", subscriber_id);
						criteria.setAttribute("schedule_id_list", record.getAttributeAsString("schedule_id_list"));

						DSRequest dsReq = new DSRequest();
						dsReq.setOperationId("remRoamingSchedules");
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
				mailText += "<br>ორგანიზაცია: " + contractRecord.getAttribute("party_name");
				mailText += "<br>Contract_ID: " + contractRecord.getAttribute("contract_id");
				mailText += "<br>ანგარიშის მმართველი: " + email;
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
			if (CompUtils.isEmpty(phonesItem.getValue())) {
				SC.warn(TicketMaster.constants.please_enter_phones());
				return;
			}
			String phones = phonesItem.getValueAsString();
			String phonesArr[] = phones.split("\n");
			if (phonesArr == null || phonesArr.length <= 0) {
				SC.warn(TicketMaster.constants.please_enter_phones());
				return;
			}

			List<String> phonesList = new ArrayList<String>();
			for (String phone_str : phonesArr) {
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
			criteria.setAttribute("remove_type", this.type);

			DSRequest dsRequest = new DSRequest();
			dsRequest.setOperationId("getSubsScheduleList");

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
							Integer status = record.getAttributeAsInt("status");
							if (status != null && status.intValue() >= 0) {
								record.setAttribute("notification", "");
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
		if (type == TYPE_ROAMING) {
			return TicketMaster.constants.roaming_service_rem_shedules();
		} else if (type == TYPE_LIMIT) {
			return TicketMaster.constants.remove_limit_shedule();
		}
		return "";
	}

	@Override
	public boolean saveOnServer() {
		return false;
	}
}
