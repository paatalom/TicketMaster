package com.ticketmaster.portal.webui.server.dmi;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;
import com.isomorphic.rpc.RPCManager;
import com.isomorphic.rpc.RPCRequest;
import com.isomorphic.rpc.RPCResponse;
import com.isomorphic.servlet.IDACall;
import com.isomorphic.servlet.RequestContext;

public class MyIDACall extends IDACall {
	private static final long serialVersionUID = -6219869125108538059L;

	// private String USER_NAME = "USER_NAME";
	
	@Override
	public void processRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		super.processRequest(arg0, arg1);
		System.out.println(" = processRequest = ");
	}
	
	@Override
	public RPCResponse handleRPCRequest(RPCRequest arg0, RPCManager arg1, RequestContext arg2) throws Exception {
		RPCResponse resp = super.handleRPCRequest(arg0, arg1, arg2);
		System.out.println(" = handleRPCRequest = ");
		return resp;
	}

	@Override
	public DSResponse handleDSRequest(DSRequest arg0, RPCManager arg1, RequestContext arg2) throws Exception {
		DSResponse resp = super.handleDSRequest(arg0, arg1, arg2);
		System.out.println(" = handleDSRequest = ");
		return resp;
	}

	@SuppressWarnings("unused")
	@Override
	public void processRPCTransaction(RPCManager rpc, RequestContext context) throws Exception {
		super.processRPCTransaction(rpc, context);

		String userName = "";
		// Map criteria = dsRequest.getCriteria();
		// if (criteria != null && !criteria.isEmpty() &&
		// criteria.containsKey(USER_NAME)) {
		// userName = criteria.get(USER_NAME).toString();
		// }
		// if (userName == null || userName.trim().equals("")) {
		// Map values = dsRequest.getValues();
		// if (values != null && !values.isEmpty() &&
		// values.containsKey(USER_NAME)) {
		// userName = values.get("").toString();
		// }
		// }
		// if (userName == null || userName.trim().equals("")) {
		// Map oldValues = dsRequest.getOldValues();
		// if (oldValues != null && !oldValues.isEmpty() &&
		// oldValues.containsKey(USER_NAME)) {
		// userName = oldValues.get(USER_NAME).toString();
		// }
		// }

//		System.out.println("1.userName = " + userName + " rpc.getUserId()=" + rpc.getUserId());
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		if (auth != null) {
//			new SecurityContextLogoutHandler().logout(context.request, context.response, auth);
//		}
//		SecurityContextHolder.getContext().setAuthentication(null);
//		throw new RuntimeException(" = processRPCTransaction = ");
	}
}
