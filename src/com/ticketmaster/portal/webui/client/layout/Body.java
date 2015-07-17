package com.ticketmaster.portal.webui.client.layout;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.client.content.tabs.TabSMSBroadcast;
import com.ticketmaster.portal.webui.client.content.tabs.TabFindContractsMain;
import com.ticketmaster.portal.webui.client.utils.CommonSingleton;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

public class Body extends VLayout {

    private TabSet mainTabPanel;
    private Menu contextMenu;

    private TabFindContractsMain tabFindSubscriber;
    private MenuItem closeTab;
    private MenuItem closeAllTab;
    private HLayout emptyLayout;
    private Header header;
    private Footer footer;

    public Body(Header header, Footer footer) {
        try {
            setWidth100();
            setHeight100();
            this.header = header;
            this.footer = footer;
        } catch (Exception e) {
            e.printStackTrace();
            SC.say(e.toString());
        }
    }

    public void drawMainPanel() {
        try {
            retrievePermissionsAndDraw();
        } catch (Exception e) {
            e.printStackTrace();
            SC.warn(e.toString());
        }
    }

    private void drawMainPanelAfterPermissions() {
        try {
            if (mainTabPanel != null) {
                removeMember(mainTabPanel);
                mainTabPanel = null;
            }
            contextMenu = new Menu();
            contextMenu.setWidth(150);

            closeTab = new MenuItem(TicketMaster.constants.close_window());
            closeTab.setIcon("icon_close.gif");
            closeTab.setIconHeight(16);
            closeTab.setIconWidth(16);

            closeAllTab = new MenuItem(TicketMaster.constants.close_all_window());
            closeAllTab.setIcon("icon_closeall.gif");
            closeAllTab.setIconHeight(16);
            closeAllTab.setIconWidth(16);
            contextMenu.setItems(closeTab, closeAllTab);

            mainTabPanel = new TabSet();
            mainTabPanel.setTabBarPosition(Side.TOP);
            mainTabPanel.setWidth100();
            mainTabPanel.setHeight100();

            tabFindSubscriber = new TabFindContractsMain(this.footer);
            TabSMSBroadcast tabSMSBroadcast = new TabSMSBroadcast();
            mainTabPanel.addTab(tabFindSubscriber);
            mainTabPanel.addTab(tabSMSBroadcast);

            closeTab.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(MenuItemClickEvent event) {
                    Tab tab = mainTabPanel.getSelectedTab();
                    mainTabPanel.removeTab(tab);
                }
            });
            closeAllTab.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(MenuItemClickEvent event) {
                    Tab tabs[] = mainTabPanel.getTabs();
                    if (tabs != null && tabs.length > 0) {
                        for (Tab tab : tabs) {
                            if (!tab.getID().equals("tabFindSubscriber")) {
                                mainTabPanel.removeTab(tab);
                            }
                        }
                    }
                }
            });
            if (emptyLayout != null) {
                removeMember(emptyLayout);
                emptyLayout = null;
            }
            addMember(mainTabPanel);

            this.header.addLogOut();
            this.footer.refresh(null, true);
        } catch (Exception e) {
            e.printStackTrace();
            SC.say(e.toString());
        }
    }

    public void drawEmptyPanel() {
        try {
            emptyLayout = new HLayout();
            emptyLayout.setWidth100();
            emptyLayout.setHeight100();

            if (mainTabPanel != null) {
                removeMember(mainTabPanel);
                mainTabPanel = null;
            }

            addMember(emptyLayout);

            this.header.removeLogOut();
            this.footer.refresh(null, false);

        } catch (Exception e) {
            e.printStackTrace();
            SC.say(e.toString());
        }
    }

    private void retrievePermissionsAndDraw() throws Exception {
        try {
            DataSource dataSource = DataSource.get("UserManagerDS");
            DSRequest dsRequest = new DSRequest();
            dsRequest.setOperationId("getSession");

            dataSource.fetchData(new Criteria(), new DSCallback() {
                @Override
                public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
                    CommonSingleton.getInstance().setMapPerms(dsResponse.getDataAsMap());
                    drawMainPanelAfterPermissions();
                }
            }, dsRequest);
        } catch (Exception e) {
            throw e;
        }
    }

    public void addTab(Tab tab) {
        tab.setContextMenu(contextMenu);
        mainTabPanel.addTab(tab);
        mainTabPanel.selectTab(tab.getID());
    }

    private void addTab(Tab tab, int index) {
        tab.setContextMenu(contextMenu);
        mainTabPanel.addTab(tab, index);
        mainTabPanel.selectTab(tab.getID());
    }

    public boolean hasTab(String tabId) {
        return mainTabPanel.getTab(tabId) != null;
    }

    public void activateTab(String tabId) {
        mainTabPanel.selectTab(tabId);
    }

    public Tab getTab(String tabId) {
        return mainTabPanel.getTab(tabId);
    }

    public void replaceTab(Tab tab1, Tab tab2) {
        int index = mainTabPanel.getTabNumber(tab1.getID());
        mainTabPanel.removeTab(tab1);
        addTab(tab2, index);
    }

    public void closeAllTab() {
        mainTabPanel.clear();
    }

    public TabSet getMainTabPanel() {
        return mainTabPanel;
    }
}
