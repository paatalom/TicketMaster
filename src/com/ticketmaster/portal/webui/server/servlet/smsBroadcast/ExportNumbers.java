package com.ticketmaster.portal.webui.server.servlet.smsBroadcast;

import com.isomorphic.jpa.EMF;
import com.magti.billing.ejb.common.beans.UserContext;
import com.ticketmaster.portal.webui.server.session.ServerSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by vano on 6/10/15.
 */
public class ExportNumbers extends HttpServlet {

	private EntityManager em = EMF.getEntityManager();

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServerSession serverSession = (ServerSession) request.getSession().getAttribute(
				ServerSession.SESSION_ATTRIBUTE_NAME);
		if (serverSession != null) {
			UserContext userContext = serverSession.getUserContext();
			Long userId = userContext.getUser().getUserId();
			String smsInfoId = request.getParameter("sms_info_id").toString();

			if(!isValidUser(smsInfoId, userId)) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.getWriter().append("403 Forbidden");
				return ;
			}

			response.setContentType("text/csv");
			response.setHeader("Content-disposition", "attachment; filename=numbers_"+smsInfoId+".csv");
			Query query = em.createNativeQuery("select \n" +
					"  q.id, \n" +
					"  q.PHONE_NUMBER,\n" +
					"  s.NAME state,\n" +
					"  q.send_date,\n" +
					"  q.delivery_date\n" +
					"from sms_queue q, SMS_INFO i, SMS_STATES s\n" +
					"where q.sms_info_id = :sms_info_id \n" +
					"and q.state_id = s.id\n" +
					"and i.ID = q.SMS_INFO_ID\n" +
					"and i.type_id = 1\n" +
					"order by q.id");
			query.setParameter("sms_info_id", smsInfoId);
			List<Object[]> res = query.getResultList();
			PrintWriter pw = response.getWriter();

			pw.append("ID,Number,State,Send Date,Delivery Date\n");
			for(Object[] rec : res){
				pw.append(rec[0] + "," + rec[1]+ "," + rec[2]+ ",");
				pw.append((rec[3] == null ? "" : rec[3]) + ",");
				pw.append((rec[4] == null ? "" : rec[4]) + "\n");
			}
		} else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().append("401 unauthorized");
		}
	}

	private boolean isValidUser(String smsInfoId, Long userId){
		Query query = em.createNativeQuery("select 1 from USER_MANAGER.USER_ASSIGNED_PARTIES p\n" +
				"where p.CONTRACT_ID = (select s.CONTRACT_ID \n" +
				"  from sms_info i, sms_senders s \n" +
				"  where i.SMS_SENDER_ID = s.ID and i.id = :sms_info_id)\n" +
				"and p.user_id = :user_id");
		query.setParameter("sms_info_id", smsInfoId);
		query.setParameter("user_id", userId);
		return query.getResultList().size() > 0;
	}
}
