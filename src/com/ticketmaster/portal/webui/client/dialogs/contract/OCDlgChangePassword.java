package com.ticketmaster.portal.webui.client.dialogs.contract;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.client.component.okcancel.OkCancelDialogClickEvent;
import com.ticketmaster.portal.webui.client.component.okcancel.OkCancelDialogLayout;
import com.ticketmaster.portal.webui.client.component.utils.CompUtils;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public class OCDlgChangePassword extends OkCancelDialogLayout {

	private DynamicForm enterForm;
	private PasswordItem currentPassword;
	private PasswordItem newPassword1;
	private PasswordItem newPassword2;

	public OCDlgChangePassword(String mail) {

		setWidth(400);
		setHeight(140);
		setPadding(5);

		VLayout vLayout = new VLayout();
		vLayout.setHeight100();
		vLayout.setWidth100();
		vLayout.setMembersMargin(10);

		enterForm = new DynamicForm();
		enterForm.setAutoFocus(true);
		enterForm.setPadding(30);
		enterForm.setBorder("1px solid #a7abb4");
		enterForm.setBackgroundColor("#f1f5fb");
		enterForm.setTitleWidth(150);
		enterForm.setHeight100();
		enterForm.setTitleOrientation(TitleOrientation.LEFT);

		currentPassword = new PasswordItem();
		currentPassword.setName("current_password");
		currentPassword.setTitle(TicketMaster.constants.current_password());
		currentPassword.setWidth(160);
		currentPassword.setValue(mail);

		newPassword1 = new PasswordItem();
		newPassword1.setName("new_password");
		newPassword1.setTitle(TicketMaster.constants.new_password());
		newPassword1.setWidth(160);
		newPassword1.setValue(mail);

		newPassword2 = new PasswordItem();
		newPassword2.setName("re_type_new_password");
		newPassword2.setTitle(TicketMaster.constants.re_type_new_password());
		newPassword2.setWidth(160);
		newPassword2.setValue(mail);

		KeyPressHandler enterHandler = new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName().equals("Enter")) {
					getMgtHandlerManager().fireEvent(new OkCancelDialogClickEvent(validateAndGetRecord()));
				}
			}
		};

		currentPassword.addKeyPressHandler(enterHandler);

		enterForm.setFields(currentPassword, newPassword1, newPassword2);
		vLayout.addMembers(enterForm);
		addMember(vLayout);
	}

	@Override
	public Record validateAndGetRecord() {
		if (CompUtils.isEmpty(currentPassword.getValue())) {
			SC.warn("შეიყვანეთ მიმდინარე პაროლი");
			return null;
		} else if (CompUtils.isEmpty(newPassword1.getValue())) {
			SC.warn("შეიყვანეთ ახალი პაროლი");
			return null;
		} else if (CompUtils.isEmpty(newPassword2.getValue())) {
			SC.warn("შეიყვანეთ ახალი პაროლი");
			return null;
		} else if (!newPassword1.getValue().equals(newPassword2.getValue())) {
			SC.warn("სხვადასხვა ახალი პაროლი");
			return null;
		} else if (newPassword1.getValue().toString().length() < 6) {
			SC.warn("პაროლის სიგრძე უნდა იყოს მინიმუმ 6 სიმბოლო");
			return null;
		}
		Record record = new Record();
		record.setAttribute("current_password", currentPassword.getValue().toString());
		record.setAttribute("new_password", newPassword1.getValue().toString());
		return record;
	}

	@Override
	public String getWindowTitle() {
		return TicketMaster.constants.change_password();
	}

	@Override
	public String getOkTitle() {
		return TicketMaster.constants.change();
	}
}
