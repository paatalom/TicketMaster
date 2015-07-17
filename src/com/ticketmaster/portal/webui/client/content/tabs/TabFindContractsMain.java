package com.ticketmaster.portal.webui.client.content.tabs;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.client.component.input.MgtListArea;
import com.ticketmaster.portal.webui.client.component.jasper.JasperReports;
import com.ticketmaster.portal.webui.client.component.mgtdialog.MgtDialog;
import com.ticketmaster.portal.webui.client.component.okcancel.OkCancelDialog;
import com.ticketmaster.portal.webui.client.component.okcancel.OkCancelDialogClickEvent;
import com.ticketmaster.portal.webui.client.component.okcancel.OkCancelDialogOkClickHandler;
import com.ticketmaster.portal.webui.client.dialogs.charts.DlgChartDashBoard;
import com.ticketmaster.portal.webui.client.dialogs.report.DlgGeneratedReports;
import com.ticketmaster.portal.webui.client.dialogs.report.OCDlgReportCorpBill;
import com.ticketmaster.portal.webui.client.dialogs.report.OCDlgReportDetail;
import com.ticketmaster.portal.webui.client.layout.Footer;
import com.ticketmaster.portal.webui.client.utils.ClientUtils;
import com.ticketmaster.portal.webui.client.utils.CommonSingleton;
import com.ticketmaster.portal.webui.client.utils.ReportParams;
import com.smartgwt.client.data.*;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ExportDisplay;
import com.smartgwt.client.types.ExportFormat;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.EnumUtil;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.HeaderDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.HeaderDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.toolbar.ToolStripMenuButton;
import com.ticketmaster.portal.webui.client.dialogs.contract.*;

import java.util.*;

public class TabFindContractsMain extends Tab {

    private ListGrid contractsGrid;
    private ListGrid phonesGrid;

    private MgtListArea userIdent;
    private SelectItem contractItem;
    private SelectItem thresholdType;
    private Criteria findCriteria = null;

    public TabFindContractsMain(final Footer footer) {
        try {
            // Used permissions in project
            // MANAGE_SERVICE 2204
            // CREDIT_CONTROL_LIMIT_CHANGE 2212
            // VIEW_DETAIL_REPORT 2215
            // CORP_CONTRACT_EDIT_BUNDLE_MANAGEMENT 100000

            VLayout mainLayout = new VLayout();
            mainLayout.setWidth(900);
            mainLayout.setHeight100();

            LayoutSpacer layoutSpacer = new LayoutSpacer();
            layoutSpacer.setHeight(3);
            mainLayout.addMember(layoutSpacer);

            setTitle(TicketMaster.constants.organization_list());

            DataSource subscriberDS = DataSource.get("SubscriberDS");

            contractsGrid = new ListGrid() {
                @Override
                protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
                    return "cursor: pointer";
                }
            };
            contractsGrid.setWidth(1200);
            contractsGrid.setHeight(100);
            contractsGrid.setAlternateRecordStyles(true);
            contractsGrid.setDataSource(subscriberDS);
            contractsGrid.setFetchOperation("searchParties");
            contractsGrid.setAutoFetchData(false);
            contractsGrid.setShowFilterEditor(false);
            contractsGrid.setCanEdit(false);
            contractsGrid.setCanRemoveRecords(false);
            contractsGrid.setWrapCells(true);
            contractsGrid.setFixedRecordHeights(false);
            contractsGrid.setCanReorderFields(false);
            contractsGrid.setCanSort(false);
            contractsGrid.setCanSelectText(true);
            contractsGrid.setCanDragSelectText(true);
            contractsGrid.setSelectionType(SelectionStyle.SINGLE);
            contractsGrid.setCanResizeFields(false);
            contractsGrid.setShowHeaderContextMenu(false);
            contractsGrid.addHeaderDoubleClickHandler(new HeaderDoubleClickHandler() {
                @Override
                public void onHeaderDoubleClick(HeaderDoubleClickEvent event) {
                }
            });

            ListGridField rowNumberField = new ListGridField();
            rowNumberField.setAlign(Alignment.RIGHT);
            rowNumberField.setWidth(30);
            contractsGrid.setRowNumberFieldProperties(rowNumberField);

            ListGridField contractDescription = new ListGridField();
            contractDescription.setName("contract_description");
            contractDescription.setTitle(TicketMaster.constants.contract_description());
            contractDescription.setWidth(200);

            ListGridField org_ident_no = new ListGridField();
            org_ident_no.setName("org_ident_no");
            org_ident_no.setTitle(TicketMaster.constants.org_ident_no_short());
            org_ident_no.setWidth(120);

            ListGridField partyName = new ListGridField();
            partyName.setName("party_name");
            partyName.setTitle(TicketMaster.constants.party_name());

            ListGridField party_address = new ListGridField();
            party_address.setName("party_address");
            party_address.setTitle(TicketMaster.constants.address());
            party_address.setWidth(250);

            ListGridField contract_id = new ListGridField();
            contract_id.setName("contract_id");
            contract_id.setTitle("Contract ID");
            contract_id.setWidth(70);

            ListGridField contract_status = new ListGridField();
            contract_status.setName("contract_status");
            contract_status.setTitle(TicketMaster.constants.status());
            contract_status.setWidth(150);

            ListGridField total_debt = new ListGridField();
            total_debt.setName("total_debt");
            total_debt.setTitle(TicketMaster.constants.debt());
            total_debt.setWidth(100);

            contractDescription.setAlign(Alignment.LEFT);
            org_ident_no.setAlign(Alignment.LEFT);
            contract_id.setAlign(Alignment.LEFT);
            partyName.setAlign(Alignment.LEFT);
            party_address.setAlign(Alignment.LEFT);
            total_debt.setAlign(Alignment.RIGHT);
            contractDescription.setAlign(Alignment.RIGHT);

            contractsGrid.setFields(contract_id, org_ident_no, partyName, party_address, contract_status, total_debt,
                    contractDescription);

            LayoutSpacer layoutSpacer6 = new LayoutSpacer();
            layoutSpacer6.setHeight(10);

            ToolStrip toolStrip = new ToolStrip();
            toolStrip.setWidth(1200);
            toolStrip.setPadding(5);

            ToolStrip toolStrip1 = new ToolStrip();
            toolStrip1.setWidth100();
            toolStrip1.setPadding(5);

            // ToolStripButton detailReportFull = new
            // ToolStripButton(TicketMaster.constants.detail_report_full(),"report1.png");
            ToolStripButton excelButton = new ToolStripButton();
            excelButton.setTitle(TicketMaster.constants.export());
            excelButton.setIcon("excel.gif");

            ToolStripButton exportAccounts = new ToolStripButton();
            exportAccounts.setTitle(TicketMaster.constants.export_with_accounts());
            exportAccounts.setIcon("excel.gif");

            ToolStripButton exportRoamings = new ToolStripButton();
            exportRoamings.setTitle(TicketMaster.constants.roaming_export());
            exportRoamings.setIcon("excel.gif");

            toolStrip1.addButton(excelButton);
            toolStrip1.addButton(exportAccounts);
            toolStrip1.addButton(exportRoamings);
            // toolStrip1.addButton(detailReport);
            // toolStrip1.addButton(detailReportFull);

            // detailReportFull.addClickHandler(new ClickHandler() {
            // @Override
            // public void onClick(ClickEvent event) {
            // generateDetailReport(true, true, true);
            // }
            // });

            ToolStripButton refreshBtn = new ToolStripButton(TicketMaster.constants.refresh(), "refresh.png");

            Menu billMenu = new Menu();

            MenuItem contractBillBtn = new MenuItem(TicketMaster.constants.bill());
            contractBillBtn.setIcon("report1.png");

            MenuItem detailReport = new MenuItem(TicketMaster.constants.detail_report());
            detailReport.setIcon("report1.png");

            MenuItem genReports = new MenuItem(TicketMaster.constants.generated_reports());
            genReports.setIcon("report1.png");

            // VIEW_DETAIL_REPORT
            if (CommonSingleton.getInstance().hasPermission("2215"))
                billMenu.setItems(contractBillBtn, detailReport, genReports);
            else
                billMenu.setItems(contractBillBtn, genReports);

            ToolStripMenuButton reportsMenu = new ToolStripMenuButton(TicketMaster.constants.reports(), billMenu);
            reportsMenu.setIcon("report1.png");

            // ToolStripButton contractBillBtn = new
            // ToolStripButton(TicketMaster.constants.bill(), "report1.png");
            // ToolStripButton genReports = new
            // ToolStripButton(TicketMaster.constants.generated_reports(),
            // "report1.png");
            // ToolStripButton detailReport = new
            // ToolStripButton(TicketMaster.constants.detail_report(),
            // "report1.png");

            ToolStripButton limitBtn = new ToolStripButton(TicketMaster.constants.set_limit(), "limit2.png");

            ToolStripButton bundlesBtn = new ToolStripButton(TicketMaster.constants.packages(), "package.png");

            MenuItem roamingServManagement = new MenuItem();
            roamingServManagement.setTitle(TicketMaster.constants.roaming_service_management());
            roamingServManagement.setIcon("earth.png");
            MenuItem roamingServRemSched = new MenuItem();
            roamingServRemSched.setTitle(TicketMaster.constants.roaming_service_rem_shedules());
            roamingServRemSched.setIcon("earth.png");
            MenuItem roamEndDateAndLimitExt = new MenuItem();
            roamEndDateAndLimitExt.setTitle(TicketMaster.constants.roaming_end_date_and_limit_ext());
            roamEndDateAndLimitExt.setIcon("earth.png");

            Menu mobileSubscriberRoamMenu = new Menu();
            mobileSubscriberRoamMenu.setItems(roamingServManagement, roamingServRemSched, roamEndDateAndLimitExt);

            MenuItem roamingMenu = new MenuItem();
            roamingMenu.setTitle(TicketMaster.constants.roaming());
            roamingMenu.setIcon("earth.png");
            roamingMenu.setSubmenu(mobileSubscriberRoamMenu);

            Menu servicesMenu = new Menu();
            servicesMenu.setTitle(TicketMaster.constants.roaming());
            servicesMenu.setItems(roamingMenu);

            ToolStripMenuButton services = new ToolStripMenuButton();
            services.setTitle(TicketMaster.constants.services());
            services.setMenu(servicesMenu);

            ToolStripButton charts = new ToolStripButton(TicketMaster.constants.chart_name(), "pie_chart.png");

            // toolStrip.addButton(refreshBtn);
            // toolStrip.addSeparator();
            toolStrip.addMenuButton(reportsMenu);
            // CREDIT_CONTROL_LIMIT_CHANGE
            if (CommonSingleton.getInstance().hasPermission("2212")) {
                toolStrip.addSeparator();
                toolStrip.addButton(limitBtn);
            }
            toolStrip.addSeparator();
            toolStrip.addButton(bundlesBtn);
            toolStrip.addSeparator();
            // MANAGE_SERVICE
            if (CommonSingleton.getInstance().hasPermission("2204"))
                toolStrip.addMenuButton(services);
            // toolStrip.addButton(charts);

            contractBillBtn.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
                @Override
                public void onClick(MenuItemClickEvent event) {
                    getContractBill();
                }
            });

            detailReport.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
                @Override
                public void onClick(MenuItemClickEvent event) {
                    generateDetailReport(true, false, true);
                }
            });

            genReports.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
                @Override
                public void onClick(MenuItemClickEvent event) {
                    openGeneratedReports();
                }
            });

            charts.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    DlgChartDashBoard dlgGeneratedReports = new DlgChartDashBoard();
                    MgtDialog.showDialog(dlgGeneratedReports, null, true);
                }
            });

            refreshBtn.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    contractsGrid.invalidateCache();
                    contractsGrid.fetchData();
                }
            });

            limitBtn.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setLimitToPhones();
                }
            });

            bundlesBtn.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    bundleManagement();
                }
            });

            roamingServManagement.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
                @Override
                public void onClick(MenuItemClickEvent event) {
                    try {
                        Record record = contractsGrid.getSelectedRecord();
                        if (record != null)
                            MgtDialog.showDialog(new DlgRoamingServiceMan(record), null, true);
                        else
                            SC.warn(TicketMaster.constants.please_select_contract_record());
                    } catch (Exception e) {
                        e.printStackTrace();
                        SC.warn(e.toString());
                    }
                }
            });
            roamingServRemSched.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
                @Override
                public void onClick(MenuItemClickEvent event) {
                    try {
                        Record record = contractsGrid.getSelectedRecord();
                        if (record != null)
                            MgtDialog.showDialog(new DlgRemoveSchedules(record, 1), null, true);
                        else
                            SC.warn(TicketMaster.constants.please_select_contract_record());
                    } catch (Exception e) {
                        e.printStackTrace();
                        SC.warn(e.toString());
                    }
                }
            });
            roamEndDateAndLimitExt.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
                @Override
                public void onClick(MenuItemClickEvent event) {
                    try {
                        Record record = contractsGrid.getSelectedRecord();
                        if (record != null)
                            MgtDialog.showDialog(new DlgRoamEndDateAndLimtiExt(record), null, true);
                        else
                            SC.warn(TicketMaster.constants.please_select_contract_record());
                    } catch (Exception e) {
                        e.printStackTrace();
                        SC.warn(e.toString());
                    }
                }
            });
            LayoutSpacer layoutSpacer2 = new LayoutSpacer();
            layoutSpacer2.setHeight(10);

            VLayout subsDataLayout = new VLayout();
            subsDataLayout.setWidth(1200);
            subsDataLayout.setHeight100();
            subsDataLayout.setIsGroup(true);
            subsDataLayout.setGroupTitle(TicketMaster.constants.subscribers());
            subsDataLayout.setPadding(10);
            subsDataLayout.setMembersMargin(10);

            DynamicForm searchForm = new DynamicForm();
            searchForm.setAutoFocus(true);
            searchForm.setWidth100();
            searchForm.setNumCols(6);
            searchForm.setWrapItemTitles(false);
            searchForm.setTitleWidth(150);

            userIdent = new MgtListArea();
            userIdent.setName("user_identifier");
            userIdent.setTitle(TicketMaster.constants.phone());
            userIdent.setWidth(250);

            contractItem = new SelectItem();
            contractItem.setName("contract_item");
            contractItem.setTitle(TicketMaster.constants.contract_item());
            contractItem.setAllowEmptyValue(true);
            contractItem.setWidth(250);

            thresholdType = new SelectItem();
            thresholdType.setName("threshold_type");
            thresholdType.setTitle(TicketMaster.constants.limit_type());
            thresholdType.setAllowEmptyValue(true);
            thresholdType.setWidth(250);

            LinkedHashMap<String, String> thrTypes = new LinkedHashMap<String, String>();
            thrTypes.put("1", TicketMaster.constants.mono());
            thrTypes.put("2", TicketMaster.constants.limited_custom());
            thrTypes.put("3", TicketMaster.constants.vip());
            thresholdType.setValueMap(thrTypes);

            searchForm.setFields(userIdent, contractItem, thresholdType);

            HLayout formsHLayout = new HLayout();
            formsHLayout.setWidth100();
            formsHLayout.setHeight(38);
            formsHLayout.setPadding(5);
            formsHLayout.setBackgroundColor("#f1f5fb");
            formsHLayout.setBorder("1px solid #a7abb4");
            formsHLayout.addMembers(searchForm);

            IButton searchButton = new IButton();
            searchButton.setTitle(TicketMaster.constants.search());

            IButton clearButton = new IButton();
            clearButton.setTitle(TicketMaster.constants.clear());

            HLayout hLayout2 = new HLayout();
            hLayout2.setHeight(1);
            hLayout2.setMembersMargin(10);
            hLayout2.setAlign(Alignment.RIGHT);
            hLayout2.setMembers(searchButton, clearButton);
            hLayout2.setWidth100();

            phonesGrid = new ListGrid();
            phonesGrid.setWidth100();
            phonesGrid.setHeight100();
            phonesGrid.setAlternateRecordStyles(true);
            phonesGrid.setShowRecordComponents(true);
            phonesGrid.setShowRecordComponentsByCell(true);
            phonesGrid.setDataSource(subscriberDS);
            phonesGrid.setFetchOperation("searchContractSubscribers");
            phonesGrid.setAutoFetchData(false);
            phonesGrid.setShowFilterEditor(false);
            phonesGrid.setCanEdit(false);
            phonesGrid.setCanRemoveRecords(false);
            phonesGrid.setWrapCells(true);
            phonesGrid.setFixedRecordHeights(false);
            phonesGrid.setCanReorderFields(false);
            phonesGrid.setCanSort(true);
            phonesGrid.setCanSelectText(true);
            phonesGrid.setCanDragSelectText(true);
            phonesGrid.setSelectionType(SelectionStyle.SINGLE);
            phonesGrid.setShowRowNumbers(true);
            phonesGrid.setShowFilterEditor(false);
            phonesGrid.addHeaderDoubleClickHandler(new HeaderDoubleClickHandler() {
                @Override
                public void onHeaderDoubleClick(HeaderDoubleClickEvent event) {
                }
            });

            ListGridField user_identifier = new ListGridField();
            user_identifier.setName("user_identifier");
            user_identifier.setTitle(TicketMaster.constants.phone());
            user_identifier.setWidth(100);

            ListGridField privateBalance = new ListGridField();
            privateBalance.setName("private_balance");
            privateBalance.setTitle(TicketMaster.constants.priv_bal());
            privateBalance.setWidth(120);
            privateBalance.setAlign(Alignment.RIGHT);

            // ListGridField privateThreshold = new ListGridField();
            // privateThreshold.setName("private_threshold");
            // privateThreshold.setTitle(TicketMaster.constants.priv_thr());
            // privateThreshold.setWidth(100);

            ListGridField corporateBalance = new ListGridField();
            corporateBalance.setName("corporate_balance");
            corporateBalance.setTitle(TicketMaster.constants.corp_bal());
            corporateBalance.setWidth(120);
            corporateBalance.setAlign(Alignment.RIGHT);

            ListGridField corporateThreshold = new ListGridField();
            corporateThreshold.setName("corporate_threshold_txt");
            corporateThreshold.setTitle(TicketMaster.constants.corp_thr());
            corporateThreshold.setAlign(Alignment.RIGHT);
            corporateThreshold.setWidth(120);
            corporateThreshold.setAlign(Alignment.RIGHT);

            ListGridField additionalBalance = new ListGridField();
            additionalBalance.setName("additional_balance");
            additionalBalance.setTitle(TicketMaster.constants.additional_bal());
            additionalBalance.setWidth(130);
            additionalBalance.setAlign(Alignment.RIGHT);

//          ListGridField internetBalance = new ListGridField();
//          internetBalance.setName("internet_balance_txt");
//          internetBalance.setTitle(TicketMaster.constants.internet_balance());
//          internetBalance.setWidth(150);
//          internetBalance.setAlign(Alignment.RIGHT);
//
//          ListGridField internetExpDate = new ListGridField();
//          internetExpDate.setName("internet_exp_date");
//          internetExpDate.setTitle(TicketMaster.constants.internet_end_date());
//          internetExpDate.setWidth(130);
//          internetExpDate.setAlign(Alignment.CENTER);
//
//          ListGridField voiceEndDate = new ListGridField();
//          voiceEndDate.setName("voice_end_date");
//          voiceEndDate.setTitle(TicketMaster.constants.roaming_end_date());
//          voiceEndDate.setWidth(130);
//          voiceEndDate.setAlign(Alignment.CENTER);

//          ListGridField gprsEndDate = new ListGridField();
//          gprsEndDate.setName("gprs_end_date");
//          gprsEndDate.setTitle("GPRS " + TicketMaster.constants.roaming());
//          gprsEndDate.setWidth(130);
//          gprsEndDate.setAlign(Alignment.CENTER);

//			ListGridField travellerEndDate = new ListGridField();
//			travellerEndDate.setName("traveller_end_date");
//			travellerEndDate.setTitle(TicketMaster.constants.traveller());
//			travellerEndDate.setWidth(130);
//			travellerEndDate.setAlign(Alignment.CENTER);

//          ListGridField roamingDeposit = new ListGridField();
//          roamingDeposit.setName("roaming_deposit");
//          roamingDeposit.setTitle(TicketMaster.constants.roaming_balance());
//          roamingDeposit.setWidth(130);
//          roamingDeposit.setAlign(Alignment.RIGHT);
//
//          ListGridField roamingLimit = new ListGridField();
//          roamingLimit.setName("roaming_limit");
//          roamingLimit.setTitle(TicketMaster.constants.roaming_limit());
//          roamingLimit.setWidth(140);
//          roamingLimit.setAlign(Alignment.RIGHT);

//          ListGridField regDate = new ListGridField();
//          regDate.setName("reg_date_txt");
//          regDate.setTitle(TicketMaster.constants.registration_date());
//          regDate.setWidth(160);

//          ListGridField statusDescr = new ListGridField();
//          statusDescr.setName("subscriber_status_descr");
//          statusDescr.setTitle(TicketMaster.constants.status());
//          statusDescr.setAlign(Alignment.RIGHT);
//          statusDescr.setWidth(160);

//          ListGridField contrItemName = new ListGridField();
//          contrItemName.setName("contract_item_name");
//          contrItemName.setTitle(TicketMaster.constants.contract_item_name());

            phonesGrid.setFields(user_identifier, privateBalance, corporateBalance, corporateThreshold, additionalBalance);

            subsDataLayout.setMembers(formsHLayout, hLayout2, toolStrip1, phonesGrid);

            mainLayout.addMembers(layoutSpacer6, toolStrip, contractsGrid, layoutSpacer2, subsDataLayout);

            setPane(mainLayout);
            fillPartyData();

            contractsGrid.addRecordClickHandler(new RecordClickHandler() {
                @Override
                public void onRecordClick(RecordClickEvent event) {
                    Record record = contractsGrid.getSelectedRecord();
                    if (record == null) {
                        footer.refresh(null, false);
                        SC.say(TicketMaster.constants.please_select_record());
                        return;
                    }
                    openContract(record);
                    footer.refresh(record, false);
                }
            });

            clearButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    contractItem.clearValue();
                    userIdent.clearValue();
                    thresholdType.clearValue();
                }
            });

            searchButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    findSubscribers(false);
                }
            });

            excelButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    findSubscribers(true);
                }
            });

            exportAccounts.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    exportWithAccounts();
                }
            });

            exportRoamings.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    roamingExport();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            SC.warn(e.toString());
        }
    }

    private void generateDetailReport(final boolean showSides, final boolean isFull, final boolean isBatch) {
        try {
            Record contractRecord = contractsGrid.getSelectedRecord();
            if (contractRecord == null || contractRecord.getAttributeAsLong("contract_id") == null) {
                SC.say(TicketMaster.constants.please_select_contract_record());
                return;
            }

            if (findCriteria == null) {
                SC.say(TicketMaster.constants.please_search_subscribers_for_report());
                return;
            }

            final Long contract_id = contractRecord.getAttributeAsLong("contract_id");

            OCDlgReportDetail ocReportDetailFull = new OCDlgReportDetail(showSides, isFull, isBatch);
            OkCancelDialog.showDialog(ocReportDetailFull);
            ocReportDetailFull.addOnOkClickHandler(new OkCancelDialogOkClickHandler() {
                @Override
                public void addOnOkHandler(OkCancelDialogClickEvent event) {
                    detailGenerator(event.getRecord(), isFull ? 1L : 0L, contract_id);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            SC.say(e.toString());
        }
    }

    private void detailGenerator(final Record r, Long reportType, Long contracId) {
        final Long paramId = ClientUtils.getRandomLong();
        findCriteria.setAttribute("param_id", paramId);

        // Insert Report Params(Subscriber IDs)
        final DataSource reportDS = DataSource.get("ReportDS");
        DSRequest dsRequest = new DSRequest();
        dsRequest.setOperationId("insertReportParams");

        r.setAttribute("param_id", paramId);
        r.setAttribute("contract_id", contracId);
        r.setAttribute("report_type", reportType);
        r.setAttribute("format", r.getAttribute("format"));

        DSCallback dsCallback = new DSCallback() {
            @Override
            public void execute(DSResponse response, Object rawData, DSRequest request) {
                // Get Jasper Params Count
                DSRequest dsRequest = new DSRequest();
                dsRequest.setOperationId("getJasperParamsCount");
                Criteria criteria = new Criteria();
                criteria.setAttribute("param_id", paramId);
                reportDS.fetchData(criteria, new DSCallback() {
                    @Override
                    public void execute(DSResponse response, Object rawData, DSRequest request) {
                        try {
                            r.setAttribute("items_count", response.getData()[0].getAttributeAsLong("id"));
                            // Insert into REPORTS_GENERATOR
                            DSRequest dsRequest = new DSRequest();
                            dsRequest.setOperationId("createReportsGenerator");
                            reportDS.addData(r, new DSCallback() {
                                @Override
                                public void execute(DSResponse response, Object rawData, DSRequest request) {
                                    SC.say(TicketMaster.constants.order_received());
                                }
                            }, dsRequest);
                        } catch (Exception e) {
                            e.printStackTrace();
                            SC.warn(e.toString());
                        }
                    }
                }, dsRequest);
            }
        };
        reportDS.updateData(new Record(findCriteria.getValues()), dsCallback, dsRequest);
    }

    private void openGeneratedReports() {
        try {
            try {
                DlgGeneratedReports dlgGeneratedReports = new DlgGeneratedReports();
                MgtDialog.showDialog(dlgGeneratedReports, null, true);
            } catch (Exception e) {
                e.printStackTrace();
                SC.warn(e.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            SC.say(e.toString());
        }
    }

    private void bundleManagement() {
        try {
            Record contractRecord = contractsGrid.getSelectedRecord();
            if (contractRecord == null || contractRecord.getAttributeAsLong("contract_id") == null) {
                SC.say(TicketMaster.constants.please_select_contract_record());
                return;
            }
            MgtDialog.showDialog(new DlgBundleManagement(contractRecord), null, true);
        } catch (Exception e) {
            e.printStackTrace();
            SC.say(e.toString());
        }
    }

    private void setLimitToPhones() {
        try {
            Record contractRecord = contractsGrid.getSelectedRecord();
            if (contractRecord == null || contractRecord.getAttributeAsLong("contract_id") == null) {
                SC.say(TicketMaster.constants.please_select_contract_record());
                return;
            }
            MgtDialog.showDialog(new DlgLimitManagement(contractRecord), null, true);
        } catch (Exception e) {
            e.printStackTrace();
            SC.say(e.toString());
        }
    }

    private void getContractBill() {
        try {
            Record contractRecord = contractsGrid.getSelectedRecord();
            if (contractRecord == null || contractRecord.getAttributeAsLong("contract_id") == null) {
                SC.say(TicketMaster.constants.please_select_contract_record());
                return;
            }
            final Long contract_id = contractRecord.getAttributeAsLong("contract_id");

            OCDlgReportCorpBill ocReportBill = new OCDlgReportCorpBill();
            OkCancelDialog.showDialog(ocReportBill, false);
            ocReportBill.addOnOkClickHandler(new OkCancelDialogOkClickHandler() {
                @Override
                public void addOnOkHandler(OkCancelDialogClickEvent event) {
                    Record r = event.getRecord();
                    r.setAttribute("contract_id", contract_id);
                    JasperReports jasperReport = new JasperReports(r.getAttribute("ReportName"));
                    jasperReport.openReport(r);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            SC.say(e.toString());
        }
    }

    private void findSubscribers(boolean isExport) {
        try {
            Record contractRecord = contractsGrid.getSelectedRecord();
            if (contractRecord == null || contractRecord.getAttributeAsLong("contract_id") == null) {
                SC.say(TicketMaster.constants.please_select_contract_record());
                return;
            }
            Long contract_id = contractRecord.getAttributeAsLong("contract_id");
            List<String> phones = userIdent.getListPhones();
            String thresholdValue = thresholdType.getValueAsString();
            String strContractItemId = contractItem.getValueAsString();

            findCriteria = new Criteria();
            findCriteria.setAttribute("contract_id", contract_id);
            if (phones != null && !phones.isEmpty()) {
                if (phones.size() > 250) {
                    SC.say(TicketMaster.constants.max_list_is_250());
                    return;
                }
                findCriteria.setAttribute("user_identifier", userIdent.getValueAsString());
            }
            if (strContractItemId != null && !strContractItemId.trim().equals("")) {
                findCriteria.setAttribute("contract_item_id", Long.parseLong(strContractItemId));
            }
            if (thresholdValue != null && !thresholdValue.trim().equals("")) {
                findCriteria.setAttribute("threshold_type", Long.parseLong(thresholdValue));
            }

            DSRequest dsRequest = new DSRequest();
            dsRequest.setOperationId("searchContractSubscribers");

            if (!isExport) {
                phonesGrid.invalidateCache();
                phonesGrid.fetchData(findCriteria, new DSCallback() {
                    @Override
                    public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
                    }
                }, dsRequest);
            } else {
                dsRequest.setExportAs(EnumUtil.getEnum(ExportFormat.values(), "xls"));
                dsRequest.setExportDisplay(ExportDisplay.DOWNLOAD);
                dsRequest.setExportFields("user_identifier", "private_balance", "corporate_balance",
                        "corporate_threshold_txt", "additional_balance");
                phonesGrid.exportData(dsRequest);
            }

        } catch (Exception e) {
            e.printStackTrace();
            SC.say(e.toString());
        }
    }

    private void openContract(Record record) {
        try {
            Long contract_id = record.getAttributeAsLong("contract_id");
            if (contract_id == null) {
                SC.say(TicketMaster.constants.please_select_record());
                return;
            }
            contractItem.clearValue();
            Map<String, Object> criteriaMap = new TreeMap<String, Object>();
            criteriaMap.put("contract_id", contract_id);
            ClientUtils.fillCombo(contractItem, "SubscriberDS", "getContractItems", "id", "description", criteriaMap);
            findSubscribers(false);
        } catch (Exception e) {
            e.printStackTrace();
            SC.say(e.toString());
        }
    }

    private void fillPartyData() {
        try {
            Criteria criteria = new Criteria();
            DSRequest dsRequest = new DSRequest();
            dsRequest.setOperationId("searchParties");

            contractsGrid.fetchData(criteria, new DSCallback() {
                @Override
                public void execute(DSResponse response, Object rawData, DSRequest request) {
                }
            }, dsRequest);
        } catch (Exception e) {
            e.printStackTrace();
            SC.say(e.toString());
        }
    }

    private void exportWithAccounts() {
        Record contractRecord = contractsGrid.getSelectedRecord();
        if (contractRecord == null) {
            SC.warn(TicketMaster.constants.please_select_contract_record());
            return;
        }
        String fields = "user_identifier;account_rel_type_descr;balance_txt;account_bal_type_descr;balance_expiration_date_txt;";
        HashMap<String, String> mpCaptions = new HashMap<String, String>();
        mpCaptions.put("account_rel_type_descr", TicketMaster.constants.account_rel_type_descr());
        mpCaptions.put("balance_txt", TicketMaster.constants.balance());
        mpCaptions.put("account_bal_type_descr", TicketMaster.constants.account_bal_type_descr());
        mpCaptions.put("balance_expiration_date_txt", TicketMaster.constants.exp_date());

        HashMap<String, String> mpHeaders = new HashMap<String, String>();
        mpHeaders.put("0", TicketMaster.constants.subscribers_list_with_accounts());
        mpHeaders.put(
                "1",
                TicketMaster.constants.date() + ": "
                        + (DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss").format(new Date())));
        mpHeaders.put("2", TicketMaster.constants.party() + ": " + contractRecord.getAttribute("party_name")
                + "(" + contractRecord.getAttribute("org_ident_no") + ")");
        mpHeaders.put("3",
                TicketMaster.constants.contract() + ": " + contractRecord.getAttribute("contract_description") + "("
                        + contractRecord.getAttribute("contract_id") + ")");
        try {
            ClientUtils.generateExcellReport(new ReportParams("SubscriberDS", "getSubscriberAccounts", fields,
                    findCriteria, mpCaptions, mpHeaders));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void roamingExport() {
        Record contractRecord = contractsGrid.getSelectedRecord();
        if (contractRecord == null) {
            SC.warn(TicketMaster.constants.please_select_contract_record());
            return;
        }
        String fields = "user_identifier;roaming_current_balance;roaming_default_balance;roaming_limit;" +
                "voice_roaming_start_date;voice_roaming_end_date;gprs_roaming_start_date;gprs_roaming_end_date;" +
                "traveller_start_date;from_corp_acc;";
        HashMap<String, String> mpCaptions = new HashMap<String, String>();
        mpCaptions.put("user_identifier", TicketMaster.constants.phone());
        mpCaptions.put("roaming_current_balance", TicketMaster.constants.roaming_current_balance());
        mpCaptions.put("roaming_default_balance", TicketMaster.constants.roaming_default_balance());
        mpCaptions.put("roaming_limit", TicketMaster.constants.roaming_limit());
        mpCaptions.put("voice_roaming_start_date", TicketMaster.constants.voice_roaming_start_date());
        mpCaptions.put("voice_roaming_end_date", TicketMaster.constants.voice_roaming_end_date());
        mpCaptions.put("gprs_roaming_start_date", TicketMaster.constants.gprs_roaming_start_date());
        mpCaptions.put("gprs_roaming_end_date", TicketMaster.constants.gprs_roaming_end_date());
        mpCaptions.put("traveller_start_date", TicketMaster.constants.traveller_start_date());
        mpCaptions.put("from_corp_acc", TicketMaster.constants.use_corp_account());

        HashMap<String, String> mpHeaders = new HashMap<String, String>();
        mpHeaders.put("0", TicketMaster.constants.roaming_services());
        mpHeaders.put(
                "1",
                TicketMaster.constants.date() + ": "
                        + (DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss").format(new Date())));
        mpHeaders.put("2", TicketMaster.constants.party() + ": " + contractRecord.getAttribute("party_name")
                + "(" + contractRecord.getAttribute("org_ident_no") + ")");
        mpHeaders.put("3",
                TicketMaster.constants.contract() + ": " + contractRecord.getAttribute("contract_description") + "("
                        + contractRecord.getAttribute("contract_id") + ")");
        try {
            ClientUtils.generateExcellReport(new ReportParams("SubscriberDS", "getSubscriberRoamings", fields,
                    findCriteria, mpCaptions, mpHeaders));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
