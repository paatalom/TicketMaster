package com.ticketmaster.portal.webui.client.dialogs;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Cookies;
import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.client.utils.ClientMapUtil;
import com.ticketmaster.portal.webui.client.utils.ClientUtils;
import com.ticketmaster.portal.webui.client.utils.CommonSingleton;
import com.ticketmaster.portal.webui.shared.utils.Constants;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.rpc.RPCCallback;
import com.smartgwt.client.rpc.RPCManager;
import com.smartgwt.client.rpc.RPCRequest;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.PickerIcon.Picker;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.HLayout;

public class DlgLogin extends Window {

	private HLayout hLayout;
	private Img img;
	private DynamicForm form;
	private TextItem userNameItem;
	private PasswordItem passwordItem;
	private SelectItem languageItem;
	private ButtonItem buttonItem;

	public DlgLogin() {
		try {

			setWidth(480);
			setHeight(157);
			setTitle(TicketMaster.constants.authentification());
			setShowMinimizeButton(false);
			setIsModal(true);
			setShowModalMask(true);
			setShowCloseButton(false);
			setCanDrag(false);
			setCanDragReposition(false);
			setCanDragResize(false);
			setCanDragScroll(false);
			centerInPage();

			hLayout = new HLayout();
			hLayout.setWidth100();
			hLayout.setHeight100();

			img = new Img("Login2.png", 128, 128);

			form = new DynamicForm();
			form.setHeight100();
			form.setWidth100();
			form.setPadding(10);
			form.setAutoFocus(true);

			userNameItem = new TextItem();
			userNameItem.setTitle(TicketMaster.constants.username());
			userNameItem.setWidth(230);

			passwordItem = new PasswordItem();
			passwordItem.setWidth(230);
			passwordItem.setTitle(TicketMaster.constants.password());
			passwordItem.addKeyPressHandler(new KeyPressHandler() {
				@Override
				public void onKeyPress(KeyPressEvent event) {
					if (event.getKeyName() != null && event.getKeyName().equals("Enter")) {
						login();
					}
				}
			});
			PickerIcon piPasswordRecovery = new PickerIcon(new Picker("password_recovery.png"),
					new FormItemClickHandler() {
						@Override
						public void onFormItemClick(FormItemIconClickEvent event) {
							SC.ask(TicketMaster.constants.ask_password_recovery(), new BooleanCallback() {
								@Override
								public void execute(Boolean value) {
									if (value) {
										try {
											String location = "https://corporate.magticom.ge/PortalVerification/faces/password-recovery.xhtml?umail="
													+ userNameItem.getValueAsString();
											com.google.gwt.user.client.Window.Location.replace(location);
										} catch (Exception e) {
											e.printStackTrace();
											SC.warn(e.toString());
										}
									}
								}
							});
						}
					});
			piPasswordRecovery.setPrompt(TicketMaster.constants.password_recovery());
			passwordItem.setIcons(piPasswordRecovery);

			languageItem = new SelectItem();
			languageItem.setTitle(TicketMaster.constants.language());
			languageItem.setType("comboBox");
			languageItem.setValueMap(ClientMapUtil.getInstance().getLanguages());
			languageItem.setDefaultToFirstOption(true);
			languageItem.setWidth(230);

			buttonItem = new ButtonItem();
			buttonItem.setTitle(TicketMaster.constants.login());
			buttonItem.setColSpan(2);
			buttonItem.setAlign(Alignment.RIGHT);
			buttonItem.setWidth(80);

			buttonItem.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					login();
				}
			});

			languageItem.addChangedHandler(new ChangedHandler() {
				@Override
				public void onChanged(ChangedEvent event) {
					changeLanguage(Integer.parseInt(languageItem.getValueAsString()));
				}
			});

			form.setFields(userNameItem, passwordItem, languageItem, buttonItem);
			hLayout.addMembers(img, form);
			addItem(hLayout);
			setCookieValues();

		} catch (Exception e) {
			e.printStackTrace();
			SC.say(e.toString());
		}
	}

	private void changeLanguage(Integer languageId) {
		try {
			String currentLocale = LocaleInfo.getCurrentLocale().getLocaleName();
			if (currentLocale != null && currentLocale.equals("ka") && languageId.intValue() == 1) {
				return;
			}
			if (currentLocale != null && currentLocale.equals("en") && languageId.intValue() == 2) {
				return;
			}
			UrlBuilder newUrl = com.google.gwt.user.client.Window.Location.createUrlBuilder();
			switch (languageId.intValue()) {
			case 1:
				newUrl.setParameter("locale", "ka");
				break;
			case 2:
				newUrl.setParameter("locale", "en");
				break;
			default:
				break;
			}
			saveLanguageCookie();
			com.google.gwt.user.client.Window.Location.assign(newUrl.buildString());
		} catch (Exception e) {
			e.printStackTrace();
			SC.say(e.toString());
		}
	}

	private void login() {
		if (buttonItem.getDisabled() == null || !buttonItem.getDisabled()) {
			try {
				buttonItem.setDisabled(true);
				final String userName = userNameItem.getValueAsString();
				if (userName == null || userName.trim().equals("")) {
					SC.warn(TicketMaster.constants.enterUsername());
					return;
				}
				final String user_password = passwordItem.getValueAsString();
				if (user_password == null || user_password.trim().equals("")) {
					SC.warn(TicketMaster.constants.enterPassword());
					return;
				}
				RPCRequest request = new RPCRequest();
				request.setContainsCredentials(true);
				request.setActionURL(Constants.CREDENTIALS_URL_AUTH);
				request.setUseSimpleHttp(true);
				request.setShowPrompt(false);
				Map<String, String> params = new HashMap<String, String>();
				params.put("j_username", userName);
				params.put("j_password", user_password);
				Long languageId = languageItem.getValueAsString().equals("1") ? 2L : 1L;
				ClientUtils.setLanguageId(languageId.intValue());
				params.put("languageId", languageId.toString());
				request.setParams(params);
				RPCManager.sendRequest(request, new RPCCallback() {
					@Override
					public void execute(RPCResponse response, Object rawData, RPCRequest request) {
						try {
							if (response.getStatus() == RPCResponse.STATUS_SUCCESS) {
								retrievePermissions();
								RPCManager.resendTransaction();
								saveCookies();
								TicketMaster.getMainLayout().getBody().drawMainPanel();
								DlgLogin.this.destroy();
							} else if (response.getStatus() == RPCResponse.STATUS_LOGIN_INCORRECT) {
								String error = TicketMaster.constants.invalid_username_or_password();
								buttonItem.setDisabled(false);
								SC.warn(error);
								return;
							} else if (response.getStatus() == RPCResponse.STATUS_MAX_LOGIN_ATTEMPTS_EXCEEDED) {
								String error = TicketMaster.constants.max_attempts_exceeded();
								buttonItem.setDisabled(false);
								SC.warn(error);
								return;
							}
						} catch (Exception e) {
							e.printStackTrace();
							buttonItem.setDisabled(false);
							SC.warn(e.toString());
							return;
						}
					}
				});
			} catch (Exception e) {
				SC.say(e.toString());
				buttonItem.setDisabled(false);
			}
		}
	}

	private void retrievePermissions() throws Exception {
		try {
			DataSource dataSource = DataSource.get("UserManagerDS");
			DSRequest dsRequest = new DSRequest();
			dsRequest.setOperationId("getSession");

			dataSource.fetchData(new Criteria(), new DSCallback() {
				@Override
				public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
					CommonSingleton.getInstance().setMapPerms(dsResponse.getDataAsMap());
				}
			}, dsRequest);
		} catch (Exception e) {
			throw e;
		}
	}

	private void setCookieValues() {
		try {
			String objUserName = Cookies.getCookie("portalUserInfo");
			if (objUserName != null) {
				userNameItem.setValue(objUserName);
				form.focusInItem(passwordItem);
			}
			String language = Cookies.getCookie("portalLanguage");
			if (language != null) {
				try {
					Integer lang = Integer.parseInt(language);

					switch (lang.intValue()) {
					case 1:
						languageItem.setValue("1");
						break;
					case 2:
						languageItem.setValue("2");
						break;
					default:
						break;
					}

					if (lang.intValue() != 1) {
						changeLanguage(2);
					}

				} catch (NumberFormatException e) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveLanguageCookie() {
		try {
			String language = languageItem.getValueAsString();
			if (language == null || language.trim().equals("")) {
				return;
			}
			Date now = new Date();
			long nowLong = now.getTime();
			nowLong = nowLong + (1000 * 60 * 60 * 24 * 7);// 7 days
			now.setTime(nowLong);
			Cookies.setCookie("portalLanguage", language, now);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void saveCookies() {
		try {
			String objUserName = userNameItem.getValueAsString();
			if (objUserName == null || objUserName.trim().equals("")) {
				return;
			}
			Date now = new Date();
			long nowLong = now.getTime();
			nowLong = nowLong + (1000 * 60 * 60 * 24 * 7);// 7 days
			now.setTime(nowLong);
			Cookies.setCookie("portalUserInfo", userNameItem.getValueAsString(), now);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
