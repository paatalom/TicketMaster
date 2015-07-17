package com.ticketmaster.portal.webui.client.dialogs.report;

import java.util.Date;
import java.util.LinkedHashMap;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.client.component.okcancel.OkCancelDialogLayout;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public class OCDlgReportDetailFull extends OkCancelDialogLayout {

	private DynamicForm enterForm;
	private SelectItem side;
	private DateItem startDate;
	private DateItem endDate;
	private CheckboxItem immediate;
	private StaticTextItem pageCount;
	private TextItem price;
	private ButtonItem calculateBtn;
	private SelectItem format;

	private Boolean calculated;

	public OCDlgReportDetailFull(final Record record, final boolean showSides) {

		setWidth(425);
		setHeight(245);
		setPadding(5);

		calculated = false;

		VLayout vLayout = new VLayout();
		vLayout.setHeight100();
		vLayout.setWidth100();
		vLayout.setBackgroundColor("#f1f5fb");

		enterForm = new DynamicForm();
		enterForm.setAutoFocus(true);
		enterForm.setWidth100();
		enterForm.setHeight100();
		enterForm.setPadding(25);
		enterForm.setBorder("1px solid #a7abb4");
		enterForm.setTitleWidth(148);
		enterForm.setTitleOrientation(TitleOrientation.LEFT);
		enterForm.setNumCols(3);

		side = new SelectItem();
		side.setName("side");
		side.setTitle(TicketMaster.constants.report_type());
		side.setWidth(170);
		side.setDefaultToFirstOption(true);
		side.setColSpan(2);

		LinkedHashMap<String, String> sides = new LinkedHashMap<String, String>();
		sides.put("both", TicketMaster.constants.both_side());
		sides.put("priv", TicketMaster.constants.private_());
		sides.put("corp", TicketMaster.constants.corporate());
		side.setValueMap(sides);

		startDate = new DateItem();
		startDate.setUseTextField(true);
		startDate.setName("start_date");
		startDate.setStartDate(new Date(946670400000L));
		startDate.setWidth(170);
		startDate.setTitle(TicketMaster.constants.start_date());
		Date currDate = new Date();
		CalendarUtil.addDaysToDate(currDate, -1);
		startDate.setValue(currDate);
		startDate.setColSpan(2);

		endDate = new DateItem();
		endDate.setUseTextField(true);
		endDate.setName("end_date");
		endDate.setStartDate(new Date(946670400000L));
		endDate.setWidth(170);
		endDate.setTitle(TicketMaster.constants.end_date());
		currDate = new Date();
		endDate.setValue(currDate);
		endDate.setColSpan(2);

		immediate = new CheckboxItem();
		immediate.setName("immediate");
		immediate.setTitle(TicketMaster.constants.immediate());
		immediate.setValue(false);
		immediate.setTitleOrientation(TitleOrientation.LEFT);
		immediate.setLabelAsTitle(true);
		immediate.setWidth(30);

		pageCount = new StaticTextItem();
		pageCount.setName("pages");
		pageCount.setTitle(TicketMaster.constants.page_count());
		pageCount.setWidth(200);
		pageCount.setColSpan(2);

		price = new TextItem();
		price.setName("price");
		price.setTitle(TicketMaster.constants.price_gel());
		price.setWidth(60);
		price.setColSpan(2);

		calculateBtn = new ButtonItem(TicketMaster.constants.calculate());
		calculateBtn.setAlign(Alignment.CENTER);
		calculateBtn.setColSpan(3);
		calculateBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					calculated = false;

					DataSource ds = DataSource.get("ReportDetailFullDS");
					DSRequest dsRequest = new DSRequest();
					dsRequest.setOperationId("getReportDetailFullPagesCount");

					Criteria criteria = new Criteria();
					criteria.setAttribute("start_date",
							DateTimeFormat.getFormat("yyMMdd00000000").format(startDate.getValueAsDate()));
					criteria.setAttribute("end_date",
							DateTimeFormat.getFormat("yyMMdd99999999").format(endDate.getValueAsDate()));
					criteria.setAttribute("subscriber_id", record.getAttribute("subscriber_id"));
					criteria.setAttribute("party_id", record.getAttribute("party_id"));
					if (showSides)
						criteria.setAttribute("side", side.getValue());
					else
						criteria.setAttribute("side", "both");
					ds.fetchData(criteria, new DSCallback() {
						@Override
						public void execute(DSResponse response, Object rawData, DSRequest request) {
							double cost = 0;
							Integer pageCnt = response.getData()[0].getAttributeAsInt("id");
							pageCount.setValue(pageCnt);
							if ((Boolean) immediate.getValue())
								cost += 3;
							if (pageCnt > 0 && pageCnt <= 10)
								cost += 1;
							else if (pageCnt > 10)
								cost += pageCnt * 0.1;
							else
								cost = 0;
							price.setValue(cost);
							calculated = true;
						}
					}, dsRequest);
				} catch (Exception e) {
					e.printStackTrace();
					SC.warn(e.toString());
				}
			}
		});

		format = new SelectItem();
		format.setName("format");
		format.setTitle(TicketMaster.constants.report_format());
		format.setWidth(170);
		format.setDefaultToFirstOption(true);
		format.setColSpan(2);

		LinkedHashMap<String, String> formats = new LinkedHashMap<String, String>();
		formats.put("PDF", "PDF");
		formats.put("XLS", "Excel");
		LinkedHashMap<String, String> valueIcons = new LinkedHashMap<String, String>();
		valueIcons.put("PDF", "pdf.png");
		valueIcons.put("XLS", "excel.gif");

		format.setValueMap(formats);
		format.setValueIcons(valueIcons);

		if (showSides)
			enterForm.setItems(side, startDate, endDate, immediate, price, pageCount, calculateBtn, format);
		else
			enterForm.setItems(startDate, endDate, immediate, price, pageCount, calculateBtn, format);
		vLayout.addMember(enterForm);
		addMember(vLayout);
	}

	@Override
	public Record validateAndGetRecord() {
		Date startMinus4MM = new Date();
		CalendarUtil.addMonthsToDate(startMinus4MM, -4);
		if (!calculated) {	
			SC.warn(TicketMaster.constants.please_select() + " " + calculateBtn.getTitle());
			return null;
		} else if (startMinus4MM.compareTo(startDate.getValueAsDate()) == 1) {
			SC.warn(TicketMaster.constants.wrong_detail_start_date());
			return null;
		}
		Record record = new Record(enterForm.getValues());
		return record;
	}

	@Override
	public String getWindowTitle() {
		return TicketMaster.constants.report() + " - " + TicketMaster.constants.detail_report_full();
	}

	@Override
	public String getOkTitle() {
		return TicketMaster.constants.show();
	}
}
