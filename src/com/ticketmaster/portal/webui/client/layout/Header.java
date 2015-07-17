package com.ticketmaster.portal.webui.client.layout;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.Window;
import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.client.component.okcancel.OkCancelDialog;
import com.ticketmaster.portal.webui.client.component.okcancel.OkCancelDialogClickEvent;
import com.ticketmaster.portal.webui.client.component.okcancel.OkCancelDialogOkClickHandler;
import com.ticketmaster.portal.webui.client.dialogs.DlgLogin;
import com.ticketmaster.portal.webui.client.dialogs.contract.OCDlgChangePassword;
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
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HTMLPane;

public class Header extends HTMLPane {
    public Header() {
        setWidth100();
        setHeight(48);
        removeLogOut();
        setOverflow(Overflow.HIDDEN);
        registerLogOut(this);
    }

    public static native void registerLogOut(Header h) /*-{
        $wnd.logoutFnc = function () {
            return h.@com.ticketmaster.portal.webui.client.layout.Header::logout()();
        };

        $wnd.changePasswordFnc = function () {
            return h.@com.ticketmaster.portal.webui.client.layout.Header::changePassword()();
        };

        $wnd.instructionsFnc = function () {
            return h.@com.ticketmaster.portal.webui.client.layout.Header::instructions()();
        };
    }-*/;

    public void addLogOut() {
        setContents("<div class=\"menu_box_wrapper\">"
                + "<!-- Main Menu -->"
                + "<ul class=\"main_menu\" id=\"mainmenu_9\"><li><a class=\"main_logo\">"
                + "<img src=\"images/logos/main_logo.gif\" width=\"112px\" height=\"31px\"></a></li>"
                + "<li>"
                + "<a class=\"main_logo\">" + TicketMaster.constants.main_header() + "</a>"
                + "</li>"
                + "<li>"
                + "<input type=\"submit\" onclick=\"logoutFnc();\" class=\"btn red_btn round_border medium\" value=\""
                + TicketMaster.constants.log_out() + "\"> "
                + "</li>"
                + "<li>"
                + "<input type=\"submit\" onclick=\"changePasswordFnc();\" class=\"btn red_btn round_border medium\" value=\""
                + TicketMaster.constants.change_password() + "\"> "
                + "</li>"
                + "<li>"
                + "<input type=\"submit\" onclick=\"instructionsFnc();\" class=\"btn red_btn round_border medium\" value=\""
//                + "<a href=\"images/pdf/instructions.pdf\" target=\"_blank\" class=\"btn red_btn round_border medium\">"
                + TicketMaster.constants.instructions() + "\"> "// + "</a>"
                + "</li>"
                + "</ul></div>");
    }

    public void removeLogOut() {
        setContents("<div class=\"menu_box_wrapper\">" + "<!-- Main Menu -->"
                + "<ul class=\"main_menu\" id=\"mainmenu_9\"><li><a class=\"main_logo\">"
                + "<img src=\"images/logos/main_logo.gif\" width=\"112px\" height=\"31px\"></a></li>"
                + "<li><a class=\"main_logo\">" + TicketMaster.constants.main_header() + "</a></li>" + "<li>"
                + "</li>" + "</ul></div>");
    }

    public void logout() {
        try {
            RPCRequest request = new RPCRequest();
            request.setContainsCredentials(true);
            request.setActionURL(Constants.CREDENTIALS_URL_LOGOUT);
            request.setUseSimpleHttp(true);
            request.setShowPrompt(false);
            Map<String, String> params = new HashMap<String, String>();
            request.setParams(params);
            RPCManager.sendRequest(request, new RPCCallback() {
                @Override
                public void execute(RPCResponse response, Object rawData, RPCRequest request) {
                    if (response.getStatus() == RPCResponse.STATUS_SUCCESS) {
                        TicketMaster.getMainLayout().getBody().drawEmptyPanel();
                        DlgLogin dlgLogin = new DlgLogin();
                        dlgLogin.draw();
                    } else if (response.getStatus() == RPCResponse.STATUS_LOGIN_INCORRECT) {
                        SC.warn("Error While Logging Out 1.");
                        return;
                    } else if (response.getStatus() == RPCResponse.STATUS_MAX_LOGIN_ATTEMPTS_EXCEEDED) {
                        SC.warn("Error While Logging Out 2.");
                        return;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            SC.warn(e.toString());
        }
    }

    public void changePassword() {
        try {
            OCDlgChangePassword changePassword = new OCDlgChangePassword("");
            OkCancelDialog.showDialog(changePassword, true);
            changePassword.addOnOkClickHandler(new OkCancelDialogOkClickHandler() {
                @Override
                public void addOnOkHandler(OkCancelDialogClickEvent event) {
                    try {
                        DataSource dataSource = DataSource.get("UserManagerDS");
                        DSRequest dsRequest = new DSRequest();
                        dsRequest.setOperationId("changeUserPassword");

                        Criteria criteria = new Criteria();
                        criteria.setAttribute("current_password", event.getRecord().getAttribute("current_password"));
                        criteria.setAttribute("new_password", event.getRecord().getAttribute("new_password"));

                        dataSource.fetchData(criteria, new DSCallback() {
                            @Override
                            public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
                                SC.say(TicketMaster.constants.password_changed_saccessfully());
                            }
                        }, dsRequest);
                    } catch (Exception e) {
                        e.printStackTrace();
                        SC.warn(e.toString());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            SC.warn(e.toString());
        }
    }

    public void instructions() {
        try {
            Window.open("images/pdf/instructions.pdf", "_blank", "");
        } catch (Exception e) {
            e.printStackTrace();
            SC.warn(e.toString());
        }
    }
}
