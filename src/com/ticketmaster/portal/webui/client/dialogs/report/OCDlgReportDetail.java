package com.ticketmaster.portal.webui.client.dialogs.report;

import java.util.Date;
import java.util.LinkedHashMap;

import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.client.component.okcancel.OkCancelDialogLayout;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;

public class OCDlgReportDetail extends OkCancelDialogLayout {

	private DynamicForm enterForm;
	private SelectItem side;
	private DateItem startDate;
	private DateItem endDate;
	private SelectItem format;
	private boolean isFull;
	private boolean isBatch;

	public OCDlgReportDetail(boolean showSides, boolean isFull, boolean isBatch) {
		try {
			setWidth(400);
			setHeight(145);
			setPadding(5);

			this.isFull = isFull;
			this.isBatch = isBatch;

			enterForm = new DynamicForm();
			enterForm.setAutoFocus(true);
			enterForm.setWidth100();
			enterForm.setHeight100();
			enterForm.setPadding(25);
			enterForm.setBorder("1px solid #a7abb4");
			enterForm.setBackgroundColor("#f1f5fb");
			enterForm.setTitleWidth(120);

			side = new SelectItem();
			side.setName("side");
			side.setTitle(TicketMaster.constants.report_type());
			side.setWidth(170);
			side.setDefaultToFirstOption(true);

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

			endDate = new DateItem();
			endDate.setUseTextField(true);
			endDate.setName("end_date");
			endDate.setStartDate(new Date(946670400000L));
			endDate.setWidth(170);
			endDate.setTitle(TicketMaster.constants.end_date());
			currDate = new Date();
			endDate.setValue(currDate);

			format = new SelectItem();
			format.setName("format");
			format.setTitle(TicketMaster.constants.report_format());
			format.setWidth(170);
			format.setDefaultToFirstOption(true);

			LinkedHashMap<String, String> formats = new LinkedHashMap<String, String>();
			formats.put("PDF", "PDF");
			formats.put("XLS", "Excel");
			LinkedHashMap<String, String> valueIcons = new LinkedHashMap<String, String>();
			valueIcons.put("PDF", "pdf.png");
			valueIcons.put("XLS", "excel.gif");

			format.setValueMap(formats);
			format.setValueIcons(valueIcons);

			if (showSides)
				enterForm.setItems(side, startDate, endDate, format);
			else
				enterForm.setItems(startDate, endDate, format);
			addMember(enterForm);
		} catch (Exception e) {
			SC.warn(e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public Record validateAndGetRecord() {
		Date startMinus4MM = new Date();
		CalendarUtil.addMonthsToDate(startMinus4MM, -4);
		if (startMinus4MM.compareTo(startDate.getValueAsDate()) == 1) {
			SC.warn(TicketMaster.constants.wrong_detail_start_date());
			return null;
		}
		Record record = new Record(enterForm.getValues());
		return record;
	}

	@Override
	public String getWindowTitle() {
		return TicketMaster.constants.report()
				+ " - "
				+ (this.isFull ? TicketMaster.constants.detail_report_full() : TicketMaster.constants
						.detail_report());
	}

	@Override
	public String getOkTitle() {
		return this.isBatch ? "OK" : TicketMaster.constants.show();
	}
}
