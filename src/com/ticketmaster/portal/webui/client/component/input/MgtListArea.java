package com.ticketmaster.portal.webui.client.component.input;

import java.util.ArrayList;
import java.util.List;

import com.ticketmaster.portal.webui.client.component.okcancel.OkCancelDialog;
import com.ticketmaster.portal.webui.client.component.okcancel.OkCancelDialogClickEvent;
import com.ticketmaster.portal.webui.client.component.okcancel.OkCancelDialogOkClickHandler;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;

public class MgtListArea extends TextItem {

	public MgtListArea() {
		PickerIcon listEnter = new PickerIcon(new PickerIcon.Picker("list_picker.png"), new FormItemClickHandler() {
			public void onFormItemClick(FormItemIconClickEvent event) {
				showDlg(event);
			}
		});
		setPickerIconHeight(16);
		setPickerIconWidth(16);
		listEnter.setPrompt("Phones list");
		setIcons(listEnter);
	}

	private void showDlg(FormItemIconClickEvent event) {
		try {
			final OCDlgMgtListArea ocDlgMgtListArea = new OCDlgMgtListArea(getValueAsString());
			OkCancelDialog.showDialog(ocDlgMgtListArea);
			ocDlgMgtListArea.addOnOkClickHandler(new OkCancelDialogOkClickHandler() {
				@Override
				public void addOnOkHandler(OkCancelDialogClickEvent event) {
					if (ocDlgMgtListArea.getListText() != null)
						setValue(ocDlgMgtListArea.getListText());
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			SC.say(e.toString());
		}
	}

	public List<String> getListPhones() {
		List<String> list = new ArrayList<String>();
		if (getValue() == null || getValue().toString() == null || getValue().toString().trim().equals("")) {
			return list;
		}
		String array[] = getValue().toString().split(", ");
		for (String str : array) {
			list.add(str);
		}
		return list;
	}
}
