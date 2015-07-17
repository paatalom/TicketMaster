package com.ticketmaster.portal.webui.client.dialogs.report;

import java.util.Date;
import java.util.LinkedHashMap;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.client.component.okcancel.OkCancelDialogClickEvent;
import com.ticketmaster.portal.webui.client.component.okcancel.OkCancelDialogLayout;
import com.ticketmaster.portal.webui.client.component.utils.CompUtils;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;

public class OCDlgReportCorpBill extends OkCancelDialogLayout {

    private DynamicForm enterForm;
    private SelectItem report;
    private TextItem billId;
    private SelectItem language;
    private SelectItem format;

    private String billIdValue;

    public OCDlgReportCorpBill() {
        try {
            setWidth(400);
            setHeight(145);
            setPadding(5);

            enterForm = new DynamicForm();
            enterForm.setAutoFocus(true);
            enterForm.setWidth100();
            enterForm.setHeight100();
            enterForm.setPadding(25);
            enterForm.setBorder("1px solid #a7abb4");
            enterForm.setBackgroundColor("#f1f5fb");
            enterForm.setTitleWidth(90);

            billId = new TextItem();
            billId.setName("bill_id");
            billId.setTitle("Bill ID");
            Date currDate = new Date();
            CalendarUtil.addMonthsToDate(currDate, -1);
            billIdValue = DateTimeFormat.getFormat("yyMM").format(currDate);
            billId.setValue(billIdValue);
            billId.setWidth(200);

            language = new SelectItem();
            language.setName("language");
            language.setTitle(TicketMaster.constants.language());
            language.setWidth(200);
            language.setDefaultToFirstOption(true);

            LinkedHashMap<String, String> languages = new LinkedHashMap<String, String>();

            languages.put("ge", "ქართული");
            languages.put("en", "English");

            language.setValueMap(languages);

            report = new SelectItem();
            report.setName("ReportName");
            report.setTitle(TicketMaster.constants.bill_type());
            report.setWidth(200);
            report.setDefaultToFirstOption(true);

            LinkedHashMap<String, String> reports = new LinkedHashMap<String, String>();
            reports.put("ContractBill", TicketMaster.constants.bill_report());
            reports.put("ChargeTablePivot", TicketMaster.constants.charge_table());

            report.setValueMap(reports);

            format = new SelectItem();
            format.setName("format");
            format.setTitle(TicketMaster.constants.report_format());
            format.setWidth(200);
            format.setDefaultToFirstOption(true);

            LinkedHashMap<String, String> formats = new LinkedHashMap<String, String>();
            formats.put("PDF", "PDF");
            formats.put("XLS", "Excel");
            LinkedHashMap<String, String> valueIcons = new LinkedHashMap<String, String>();
            valueIcons.put("PDF", "pdf.png");
            valueIcons.put("XLS", "excel.gif");

            format.setValueMap(formats);
            format.setValueIcons(valueIcons);

            billId.addKeyPressHandler(new KeyPressHandler() {
                @Override
                public void onKeyPress(KeyPressEvent event) {
                    if (event.getKeyName().equals("Enter")) {
                        getMgtHandlerManager().fireEvent(new OkCancelDialogClickEvent(validateAndGetRecord()));
                    }
                }
            });

            enterForm.setItems(report, billId, language, format);
            addMember(enterForm);
        } catch (Exception e) {
            SC.warn(e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public Record validateAndGetRecord() {
        if (CompUtils.isEmpty(report.getValue())) {
            SC.warn(TicketMaster.constants.please_select() + " " + report.getTitle());
            return null;
        } else if (CompUtils.isEmpty(billId.getValue())) {
            SC.warn(TicketMaster.constants.please_enter() + " " + billId.getTitle());
            return null;
        } else if (Integer.parseInt(billIdValue) < Integer.parseInt(billId.getValueAsString())) {
            SC.warn(TicketMaster.constants.incorrect_field() + " " + billId.getTitle());
            return null;
        } else if (CompUtils.isEmpty(language.getValue())) {
            SC.warn(TicketMaster.constants.please_select() + " " + language.getTitle());
            return null;
        }
        Record record = new Record(enterForm.getValues());
        return record;
    }

    @Override
    public String getWindowTitle() {
        return TicketMaster.constants.report() + " - " + TicketMaster.constants.bill_report();
    }

    @Override
    public String getOkTitle() {
        return TicketMaster.constants.show();
    }
}
