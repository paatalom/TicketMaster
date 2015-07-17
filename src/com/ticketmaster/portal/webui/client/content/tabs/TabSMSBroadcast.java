package com.ticketmaster.portal.webui.client.content.tabs;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.client.content.tabs.smsbroadcast.SmsBlacklistForm;
import com.ticketmaster.portal.webui.client.content.tabs.smsbroadcast.SmsFilterForm;
import com.ticketmaster.portal.webui.client.content.tabs.smsbroadcast.SmsInfoGrid;
import com.ticketmaster.portal.webui.client.content.tabs.smsbroadcast.SmsUploadForm;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * Created by vano on 2/24/15.
 */
public class TabSMSBroadcast extends Tab {
    private static TabSMSBroadcast instance = null;
    private TabSet tabPanel;
    private SmsInfoGrid smsInfoGrid;

    public TabSMSBroadcast() {
        setTitle(TicketMaster.constants.sms_tabTitle());


        VLayout mainLayout = new VLayout();
        mainLayout.setWidth(1200);

        SmsUploadForm smsUploadForm = new SmsUploadForm();
        SmsFilterForm smsFilterForm = new SmsFilterForm();
        SmsBlacklistForm smsBlacklistForm = new SmsBlacklistForm();

        tabPanel = new TabSet();
        tabPanel.setHeight(240);
        tabPanel.addTab(smsUploadForm);
        tabPanel.addTab(smsFilterForm);
        tabPanel.addTab(smsBlacklistForm);

        smsInfoGrid = new SmsInfoGrid();
        smsUploadForm.setSmsInfoGrid(smsInfoGrid);
        smsFilterForm.setSmsInfoGrid(smsInfoGrid);
        mainLayout.addMembers(tabPanel, smsInfoGrid);
        mainLayout.setMembersMargin(5);
        setPane(mainLayout);

    }

    public static TabSMSBroadcast getInstance() {
        if (instance == null) {
            instance = new TabSMSBroadcast();
        }
        return instance;
    }

//	private void loadLayout(Canvas canvas){
//		clearLayout();
//		if(!cardLayout.hasMember(canvas))
//			cardLayout.addMember(canvas);
//		canvas.setVisible(true);
//	}

//	private void clearLayout(){
//		Canvas[] members = cardLayout.getMembers();
//
//		for (int i = 0; (members != null) && (i < members.length); i++){
//			members[i].setVisible(false);
//		}
//	}

}
