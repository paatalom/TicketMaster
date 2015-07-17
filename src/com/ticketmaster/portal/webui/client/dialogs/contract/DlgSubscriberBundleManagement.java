package com.ticketmaster.portal.webui.client.dialogs.contract;

import java.util.ArrayList;
import java.util.List;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.client.component.mgtdialog.IExceptionCallBack;
import com.ticketmaster.portal.webui.client.component.mgtdialog.IMgtDialog;
import com.ticketmaster.portal.webui.client.component.utils.CompUtils;
import com.ticketmaster.portal.webui.client.utils.ClientMapUtil;
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
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.viewer.DetailViewer;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

public class DlgSubscriberBundleManagement extends IMgtDialog {

	private Record localBundleRecord;
	private Record contractRecord;

	private DynamicForm dynamicForm1;
	private DynamicForm dynamicForm2;
	private SelectItem subsBundleType;

	private TextAreaItem phonesItem;
	private Progressbar progressbar;

	private ListGrid phonesStatusesGrid;

	private ToolStrip menuToolstrip;
	private ToolStripButton checkBtn;
	private ToolStripButton clearInvalidBtn;
	private ToolStripButton executeBtn;
	private ToolStripButton excelExportBtn;

	private int done_cnt = 0;

	private boolean isDelete;

	private String numbersForMail = "";

	public DlgSubscriberBundleManagement(boolean isDelete, Record contractRecord, Record bundleRecord) {
		try {
			this.localBundleRecord = bundleRecord;
			this.contractRecord = contractRecord;
			this.isDelete = isDelete;
			setWidth(900);
			setHeight(600);
			setPadding(5);

			VLayout mainVLayout = new VLayout();
			mainVLayout.setWidth100();
			mainVLayout.setHeight100();
			mainVLayout.setMembersMargin(10);
			mainVLayout.setPadding(5);

			DetailViewer detailViewer = new DetailViewer();
			detailViewer.setWidth100();

			// DetailViewerField id = new DetailViewerField("id",
			// TicketMaster.constants.ident());
			DetailViewerField bundle_name = new DetailViewerField("bundle_name",
					TicketMaster.constants.bundle_name());
			DetailViewerField bundle_price_str = new DetailViewerField("bundle_price_str",
					TicketMaster.constants.price());
			// DetailViewerField bundle_type_descr = new
			// DetailViewerField("bundle_type_descr",
			// TicketMaster.constants.bundle_type());
			// DetailViewerField subscriber_type_group_descr = new
			// DetailViewerField("subscriber_type_group_descr",
			// TicketMaster.constants.subscriber_type_group());

			detailViewer.setFields(bundle_name, bundle_price_str);

			Record data[] = new Record[1];
			data[0] = bundleRecord;
			detailViewer.setData(data);

			mainVLayout.addMember(detailViewer);

			if (!isDelete) {
				dynamicForm1 = new DynamicForm();
				dynamicForm1.setWidth100();
				dynamicForm1.setTitleWidth(300);

				subsBundleType = new SelectItem("subsBundleType", TicketMaster.constants.bundle_type());

				Integer bundle_custom_proc_id = bundleRecord.getAttributeAsInt("bundle_custom_proc_id");
				if (bundle_custom_proc_id != null
						&& (bundle_custom_proc_id.equals(new Integer(2)) || bundle_custom_proc_id
								.equals(new Integer(3)))) {
					subsBundleType.setValueMap(ClientMapUtil.getInstance().getBundleTypesBB());
					subsBundleType.setDefaultToFirstOption(true);
				} else {
					subsBundleType.setValueMap(ClientMapUtil.getInstance().getBundleTypes());
				}

				subsBundleType.setWidth(300);

				dynamicForm1.setItems(subsBundleType);
				mainVLayout.addMember(dynamicForm1);
			}

			HLayout hLayout = new HLayout();
			hLayout.setWidth100();
			hLayout.setHeight100();

			dynamicForm2 = new DynamicForm();
			dynamicForm2.setAutoFocus(true);
			dynamicForm2.setWidth(200);
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
			phonesItem.setWidth(200);
			phonesItem.setHeight("100%");

			dynamicForm2.setFields(phonesItem);

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
					ListGridRecord countryRecord = (ListGridRecord) record;
					if (countryRecord == null) {
						return super.getCellCSSText(record, rowNum, colNum);
					}
					Integer status = countryRecord.getAttributeAsInt("status");
					if (status != null && status.intValue() < 0) {
						return "color:red;";
					}
					return super.getCellCSSText(record, rowNum, colNum);
				}
			};
			CompUtils.gridDefaultSetings(phonesStatusesGrid);
			phonesStatusesGrid.setDataSource(PhonesResultClientDS.getInstance());
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
			phoneField1.setWidth(100);

			ListGridField notification = new ListGridField();
			notification.setName("notification");
			notification.setTitle(TicketMaster.constants.notification());
			notification.setAlign(Alignment.LEFT);

			phonesStatusesGrid.setFields(phoneField1, notification);

			hLayout.setMembers(dynamicForm2, hLayoutImg, phonesStatusesGrid);

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

			mainVLayout.addMember(hLayout2);
			mainVLayout.addMember(hLayoutProgressbar);
			mainVLayout.addMember(hLayout);

			addMember(mainVLayout);

			checkBtn.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					copyDataToGrid();
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
					copyDataToGrid();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void execOperations() {
		try {
			done_cnt = 0;
			if (!isDelete
					&& (subsBundleType.getValueAsString() == null || subsBundleType.getValueAsString().trim()
							.equals(""))) {
				SC.say(TicketMaster.constants.please_select() + "" + subsBundleType.getTitle());
				return;
			}
			Record records[] = phonesStatusesGrid.getRecords();
			if (records == null || records.length <= 0) {
				SC.warn(TicketMaster.constants.listgrid_is_empty());
				return;
			}

			progressbar.setPercentDone(0);
			final int size = records.length;

			Long subscriber_bundle_type = isDelete ? null : Long.parseLong(subsBundleType.getValueAsString());

			numbersForMail = "";

			for (final Record record : records) {
				Long subscriber_id = record.getAttributeAsLong("subscriber_id");

				if (record.getAttributeAsInt("status").equals(0)) {
					try {
						Criteria criteria = new Criteria();
						final String param_value = record.getAttributeAsString("param_value");
						criteria.setAttribute("bundle_type", subscriber_bundle_type);
						criteria.setAttribute("param_value", param_value);
						criteria.setAttribute("subscriber_id", subscriber_id);
						criteria.setAttribute("contract_id", contractRecord.getAttributeAsLong("contract_id"));
						criteria.setAttribute("contract_item_id", record.getAttributeAsLong("contract_item_id"));
						criteria.setAttribute("bundle_id", localBundleRecord.getAttributeAsLong("id"));
						DSRequest dsReq = new DSRequest();
						if (isDelete) {
							dsReq.setOperationId("removeBundleFromSubs");
						} else {
							dsReq.setOperationId("assignBundleToSubs");
						}
						DataSource dsR = DataSource.get("BundleDS");

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
					}
				}
			}
			if (numbersForMail != "") {
				String email = contractRecord.getAttribute("email");
				String mailText = getWindowTitle();
				mailText += "<br>პაკეტის სახელი: " + localBundleRecord.getAttribute("bundle_name");
				mailText += "<br>პაკეტის ტიპი: " + subsBundleType.getDisplayValue();
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
			if (CompUtils.isEmpty(phonesItem.getValue())) {
				SC.warn(TicketMaster.constants.please_enter_phones());
				return;
			}
			String bundle_type = null;
			if (!isDelete) {
				bundle_type = subsBundleType.getValueAsString();
				if (bundle_type == null || bundle_type.trim().equals("bundle_type")) {
					SC.say(TicketMaster.constants.please_select() + subsBundleType.getTitle());
					return;
				}
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

			Record recs[] = phonesStatusesGrid.getRecords();
			for (Record record : recs) {
				phonesStatusesGrid.getDataSource().removeData(record);
			}

			for (String phone : phonesList) {
				Criteria criteria = new Criteria();
				criteria.setAttribute("user_identifier", phone);
				criteria.setAttribute("contract_id", contractRecord.getAttributeAsLong("contract_id"));
				criteria.setAttribute("subscriber_type_group_id",
						localBundleRecord.getAttributeAsLong("subscriber_type_group_id"));
				criteria.setAttribute("bundle_id", localBundleRecord.getAttributeAsLong("id"));
				if (bundle_type != null) {
					criteria.setAttribute("bundle_type", bundle_type);
				}
				Long bundle_custom_proc_id = localBundleRecord.getAttributeAsLong("bundle_custom_proc_id");
				if (bundle_custom_proc_id == null) {
					bundle_custom_proc_id = 0L;
				}
				criteria.setAttribute("bundle_custom_proc_id", bundle_custom_proc_id);

				DSRequest dsRequest = new DSRequest();
				if (isDelete) {
					dsRequest.setOperationId("getFreeListByList41");
				} else {
					dsRequest.setOperationId("getFreeListByList4");
				}

				DataSource dataSource = DataSource.get("FreeListsDS");
				dataSource.fetchData(criteria, new DSCallback() {
					@Override
					public void execute(DSResponse response, Object rawData, DSRequest request) {
						Record data[] = response.getData();
						for (Record record : data) {
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
		try {
			localBundleRecord = record;
		} catch (Exception e) {
			e.printStackTrace();
			SC.warn(e.toString());
		}
	}

	@Override
	public void validateSave(IExceptionCallBack callBack) {
		callBack.fireValidation(null);
		return;
	}

	@Override
	public Record getRecordForSave() {
		try {
			return localBundleRecord;
		} catch (Exception e) {
			e.printStackTrace();
			SC.warn(e.toString());
		}
		return null;
	}

	@Override
	public String getDataSourceName() {
		return "";
	}

	@Override
	public String getSaveOperation() {
		return "";
	}

	@Override
	public String getIdField() {
		return "";
	}

	@Override
	public String getWindowTitle() {
		return isDelete ? TicketMaster.constants.delete_bundle_from_phone() : TicketMaster.constants
				.assign_bundle_to_subscribers();
	}

	@Override
	public boolean saveOnServer() {
		return true;
	}
}
