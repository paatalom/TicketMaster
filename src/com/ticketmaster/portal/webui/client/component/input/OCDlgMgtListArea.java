package com.ticketmaster.portal.webui.client.component.input;

import java.util.ArrayList;
import java.util.List;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.client.component.okcancel.OkCancelDialogLayout;
import com.ticketmaster.portal.webui.client.component.utils.CompUtils;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.layout.VLayout;

public class OCDlgMgtListArea extends OkCancelDialogLayout {

	private TextAreaItem textArea;
	private DynamicForm form;

	public OCDlgMgtListArea(String value) {
		try {
			setWidth(300);
			setHeight(400);

			VLayout vLayout = new VLayout();
			vLayout.setWidth100();
			vLayout.setHeight100();
			vLayout.setBackgroundColor("#f1f5fb");

			form = new DynamicForm();
			form.setAutoFocus(true);
			form.setWidth100();
			form.setHeight100();
			form.setNumCols(1);
			form.setTitleWidth(30);
			form.setTitleOrientation(TitleOrientation.TOP);

			StaticTextItem staticTextItem = new StaticTextItem();
			staticTextItem.setHeight(50);
			staticTextItem.setTitle("");
			staticTextItem.setValue(TicketMaster.constants.enter_valid_phone_list());
			staticTextItem.setShowTitle(false);
			staticTextItem.setTextBoxStyle("colorRedAndBold");

			textArea = new TextAreaItem();
			textArea.setName("payment_due_date_id");
			textArea.setWidth(297);
			textArea.setHeight("100%");
			textArea.setShowTitle(false);

			form.setItems(staticTextItem, textArea);
			vLayout.addMember(form);
			addMember(vLayout);

			if (value != null && !value.trim().equals("")) {
				textArea.setValue(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			SC.warn(e.toString());
		}
	}

	public String getListText() {
		String text = textArea.getValueAsString();
		if (CompUtils.isEmpty(text))
			return "";
		String array[] = text.split("\\s+");
		if (array == null || array.length <= 0)
			return "";
		List<String> list = new ArrayList<String>();
		for (String str : array) {
			list.add(str);
		}
		String listAsText = list.toString().replace("[", "").replace("]", "");
		return listAsText;
	}

	@Override
	public Record validateAndGetRecord() {
		return new Record();
	}

	@Override
	public String getWindowTitle() {
		return TicketMaster.constants.subscribers_list();
	}

	@Override
	public String getOkTitle() {
		return "OK";
	}
}
