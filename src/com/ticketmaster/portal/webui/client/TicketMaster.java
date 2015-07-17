package com.ticketmaster.portal.webui.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.core.KeyIdentifier;
import com.smartgwt.client.data.*;
import com.smartgwt.client.rpc.LoginRequiredCallback;
import com.smartgwt.client.rpc.RPCManager;
import com.smartgwt.client.rpc.RPCRequest;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.DateUtil;
import com.smartgwt.client.util.KeyCallback;
import com.smartgwt.client.util.Page;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.HeaderSpan;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.ticketmaster.portal.webui.client.dialogs.DlgLogin;
import com.ticketmaster.portal.webui.client.layout.MainLayout;
import com.ticketmaster.portal.webui.client.utils.ClientUtils;

public class TicketMaster implements EntryPoint {

	public static AppConstants constants = (AppConstants) GWT.create(AppConstants.class);
	private static MainLayout mainLayout;

	@SuppressWarnings("deprecation")
	@Override
	public void onModuleLoad() {
		DateUtil.setShortDateDisplayFormatter(DateUtil.TOEUROPEANSHORTDATE);
		mainLayout = new MainLayout();
		ClientUtils.registerTitleValue();
		DataSource userManagerDS = DataSource.get("UserManagerDS");
		ListGrid partyGrid = new ListGrid();
		partyGrid.setShowHeaderContextMenu(false);
		partyGrid.setWidth(1260);
		partyGrid.setHeight100();
		partyGrid.setAlternateRecordStyles(true);
		partyGrid.setDataSource(userManagerDS);
		partyGrid.setFetchOperation("getAllUsers");
		partyGrid.setAutoFetchData(true);
		partyGrid.setShowFilterEditor(false);
		partyGrid.setCanRemoveRecords(false);
		partyGrid.setCanHover(true);
		partyGrid.setShowHover(true);
		partyGrid.setShowHoverComponents(true);
		partyGrid.setWrapCells(true);
		partyGrid.setFixedRecordHeights(false);
		partyGrid.setCanDragSelectText(true);
		partyGrid.setCanSort(true);
		partyGrid.setCanReorderFields(false);
		partyGrid.setCanHover(true);
		partyGrid.setShowHover(true);
		partyGrid.setShowHoverComponents(true);
		partyGrid.setCanSelectAll(false);
		partyGrid.setSelectionType(SelectionStyle.SINGLE);
		partyGrid.setHeaderHeight(40);

		ListGridField id = new ListGridField();
		id.setName("id");
		id.setTitle("ID");
		id.setWidth(70);
		id.setAlign(Alignment.LEFT);

		ListGridField USER_NAME = new ListGridField();
		USER_NAME.setName("USER_NAME");
		USER_NAME.setTitle("USER_NAME");
		USER_NAME.setWidth(70);
		USER_NAME.setAlign(Alignment.LEFT);

		ListGridField Password = new ListGridField();
		Password.setName("Password");
		Password.setTitle("Password");
		Password.setWidth(70);
		Password.setAlign(Alignment.LEFT);


		partyGrid.setFields(id, USER_NAME, Password);

		RootPanel.get().add(partyGrid);


//
//		try {
//			DataSource userManagerDS = DataSource.get("UserManagerDS");
//			DSRequest dsRequest = new DSRequest();
//			dsRequest.setOperationId("getAllUsers");
//			dsRequest.setWillHandleError(true);
//			userManagerDS.invalidateCache();
//			userManagerDS.fetchData(new Criteria(), new DSCallback() {
//				@Override
//				public void execute(DSResponse response, Object rawData, DSRequest request) {
//					mainLayout.getBody().drawMainPanel();
//				}
//			}, dsRequest);
//		} catch (Exception e) {
//			SC.say(e.toString());
//		}
//
//		RPCManager.setLoginRequiredCallback(new LoginRequiredCallback() {
//			@Override
//			public void loginRequired(int i, RPCRequest rpcRequest, RPCResponse rpcResponse) {
//				mainLayout.getBody().drawEmptyPanel();
//				DlgLogin dlgLogin = new DlgLogin();
//				dlgLogin.draw();
//			}
//		});
//
//		// Enable Debugger
//		KeyIdentifier debugKey = new KeyIdentifier();
//		debugKey.setCtrlKey(true);
//		debugKey.setKeyName("D");
//
//		Page.registerKey(debugKey, new KeyCallback() {
//			public void execute(String keyName) {
//				SC.showConsole();
//			}
//		});
	}

	public static MainLayout getMainLayout() {
		return mainLayout;
	}
}
