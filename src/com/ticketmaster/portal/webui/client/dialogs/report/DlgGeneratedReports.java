package com.ticketmaster.portal.webui.client.dialogs.report;

import java.util.Date;
import java.util.LinkedHashMap;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.client.component.mgtdialog.IExceptionCallBack;
import com.ticketmaster.portal.webui.client.component.mgtdialog.IMgtDialog;
import com.ticketmaster.portal.webui.client.utils.ClientMapUtil;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt	.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.HeaderDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.HeaderDoubleClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public class DlgGeneratedReports extends IMgtDialog {

	private ListGrid jobsGrid;
	private ButtonItem searchButton;
	private SelectItem yearItem;
	private SelectItem monthItem;

	public DlgGeneratedReports() {
		try {
			setWidth(1000);
			setHeight(600);
			setPadding(5);

			VLayout vLayout = new VLayout();
			vLayout.setWidth100();
			vLayout.setHeight100();
			vLayout.setMembersMargin(5);

			DynamicForm form = new DynamicForm();
			form.setAutoFocus(true);
			form.setWidth100();
			form.setHeight(60);
			form.setPadding(15);
			form.setBorder("1px solid #a7abb4");
			form.setBackgroundColor("#f1f5fb");
			form.setTitleWidth(50);
			form.setNumCols(16);

			yearItem = new SelectItem();
			yearItem.setName("year");
			yearItem.setTitle(TicketMaster.constants.year());
			yearItem.setWidth(150);
			Integer currentYear = Integer.valueOf(DateTimeFormat.getFormat("yyyy").format(new Date()));
			LinkedHashMap<Integer, Integer> years = new LinkedHashMap<Integer, Integer>();
			for (int i = 2014; i < currentYear; i++) {
				years.put(i, i);
			}
			yearItem.setValueMap(years);
			yearItem.setValue(currentYear);

			monthItem = new SelectItem();
			monthItem.setName("month");
			monthItem.setTitle(TicketMaster.constants.month());
			monthItem.setWidth(200);
			monthItem.setValueMap(ClientMapUtil.getInstance().getMonths(2L));
			Integer currentMonth = Integer.valueOf(DateTimeFormat.getFormat("MM").format(new Date()));
			monthItem.setValue(currentMonth);

			searchButton = new ButtonItem();
			searchButton.setTitle(TicketMaster.constants.search());
			searchButton.setStartRow(false);
			searchButton.setWidth(80);
			searchButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
				@Override
				public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
					search();
				}
			});

			form.setItems(yearItem, monthItem, searchButton);

			jobsGrid = new ListGrid() {
				@Override
				protected Canvas createRecordComponent(final ListGridRecord record, final Integer colNum) {
					final String fieldName = this.getFieldName(colNum);
					if (fieldName.equals("download")) {
						final Long status = record.getAttributeAsLong("status");
						if (status.equals(2L)) {
							ImgButton downloadImgBtn = new ImgButton();
							downloadImgBtn.setShowDown(false);
							downloadImgBtn.setShowRollOver(false);
							downloadImgBtn.setSrc("download.png");
							downloadImgBtn.setHeight(16);
							downloadImgBtn.setWidth(16);
							downloadImgBtn.setPrompt(TicketMaster.constants.download());
							downloadImgBtn.addClickHandler(new ClickHandler() {
								public void onClick(ClickEvent event) {
									try {
										DataSource reportDS = DataSource.get("ReportDS");
										DSRequest dsRequest = new DSRequest();
										dsRequest.setDownloadResult(true);
										dsRequest.setOperationId("downloadFile");
										Criteria criteria = new Criteria();
										criteria.setAttribute("param_id", record.getAttribute("id"));
										criteria.setAttribute("contract_id", record.getAttribute("contract_id"));
										reportDS.fetchData(criteria, new DSCallback() {
											@Override
											public void execute(DSResponse response, Object rawData, DSRequest request) {
											}
										}, dsRequest);
									} catch (Exception e) {
										e.printStackTrace();
										SC.warn(e.toString());
									}
								}
							});
							return downloadImgBtn;
						} else {
							return null;
						}
					} else if (fieldName.equals("format")) {
						final Long format = record.getAttributeAsLong("ext_type");
						if (format.equals(1L) || format.equals(2L)) {
							Img extImg = new Img();
							extImg.setShowDown(false);
							extImg.setShowRollOver(false);
							if (format.equals(1L))
								extImg.setSrc("excel.gif");
							if (format.equals(2L))
								extImg.setSrc("pdf.png");
							extImg.setHeight(16);
							extImg.setWidth(16);
							return extImg;
						} else {
							return null;
						}
					}
					return super.createRecordComponent(record, colNum);
				}
			};
			jobsGrid.setShowRecordComponents(true);
			jobsGrid.setShowRecordComponentsByCell(true);
			jobsGrid.setCanDragSelectText(true);
			jobsGrid.setCanEdit(false);
			jobsGrid.setCanRemoveRecords(false);
			jobsGrid.setCanReorderFields(false);
			jobsGrid.setCanResizeFields(false);
			jobsGrid.setWidth100();
			jobsGrid.setHeight100();
			jobsGrid.setAutoFetchData(false);
			jobsGrid.setDataSource(DataSource.get("ReportDS"));
			jobsGrid.setFetchOperation("getReportsGenerators");
			jobsGrid.addHeaderDoubleClickHandler(new HeaderDoubleClickHandler() {
				@Override
				public void onHeaderDoubleClick(HeaderDoubleClickEvent event) {
					return;
				}
			});

			ListGridField partyNameTin = new ListGridField();
			partyNameTin.setName("party_name");
			partyNameTin.setTitle(TicketMaster.constants.party());

			ListGridField contractId = new ListGridField();
			contractId.setName("contract_id");
			contractId.setTitle(TicketMaster.constants.contract());
			contractId.setWidth(80);
			contractId.setAlign(Alignment.LEFT);

			ListGridField itemsCount = new ListGridField();
			itemsCount.setName("items_count");
			itemsCount.setTitle(TicketMaster.constants.quantity());
			itemsCount.setWidth(70);

			ListGridField reportType = new ListGridField();
			reportType.setName("report_type");
			reportType.setWidth(120);
			reportType.setTitle(TicketMaster.constants.type());

			ListGridField startDate = new ListGridField();
			startDate.setName("start_date");
			startDate.setTitle(TicketMaster.constants.start_date());
			startDate.setWidth(110);
			startDate.setAlign(Alignment.CENTER);

			ListGridField endDate = new ListGridField();
			endDate.setName("end_date");
			endDate.setTitle(TicketMaster.constants.end_date());
			endDate.setWidth(110);
			endDate.setAlign(Alignment.CENTER);

			ListGridField recDate = new ListGridField();
			recDate.setName("rec_date");
			recDate.setTitle(TicketMaster.constants.date());
			recDate.setWidth(120);
			recDate.setAlign(Alignment.CENTER);

			ListGridField status = new ListGridField();
			status.setName("status_txt");
			status.setTitle(TicketMaster.constants.status());
			status.setWidth(100);

			ListGridField format = new ListGridField();
			format.setName("format");
			format.setTitle(" ");
			format.setWidth(20);

			ListGridField download = new ListGridField();
			download.setName("download");
			download.setTitle(" ");
			download.setWidth(20);

			jobsGrid.setFields(partyNameTin, contractId, itemsCount, reportType, startDate, endDate, recDate, status,
					format, download);

			vLayout.addMembers(form, jobsGrid);
			addMember(vLayout);
		} catch (Exception e) {
			e.printStackTrace();
			SC.warn(e.toString());
		}
	}

	@Override
	public void fillFields(Record record) {
		search();
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
		return TicketMaster.constants.generated_reports();
	}

	@Override
	public boolean saveOnServer() {
		return false;
	}

	private void search() {
		try {
			jobsGrid.invalidateCache();
			Criteria criteria = new Criteria();
			criteria.setAttribute("year", yearItem.getValue());
			criteria.setAttribute("month", monthItem.getValue());
			jobsGrid.setCriteria(criteria);

			DSRequest dsRequest = new DSRequest();
			dsRequest.setOperationId("getReportsGenerators");
			jobsGrid.fetchData(criteria, new DSCallback() {
				@Override
				public void execute(DSResponse response, Object rawData, DSRequest request) {
				}
			}, dsRequest);
		} catch (Exception e) {
			e.printStackTrace();
			SC.warn(e.toString());
		}
	}
}
