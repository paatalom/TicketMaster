package com.ticketmaster.portal.webui.server.servlet.smsBroadcast;

import com.isomorphic.jpa.EMF;
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
 * Created by vano on 6/8/15.
 */
public class ExportBlackList extends HttpServlet {

	private EntityManager em = EMF.getEntityManager();

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServerSession serverSession = (ServerSession) request.getSession().getAttribute(
				ServerSession.SESSION_ATTRIBUTE_NAME);
		if (serverSession != null) {
			response.setContentType("text/csv");
			response.setHeader("Content-disposition", "attachment; filename=black_list.csv");
//			UserContext userContext = serverSession.getUserContext();
//			Long userId = userContext.getUser().getUserId();

			Query query = em.createNativeQuery("select phone, sender\n" +
					"from SMS_BLACKLIST b, SMS_SENDERS s\n" +
					"where b.SENDER_ID = s.ID\n" +
					"and b.sender_id = :sender_id \n" +
					"union all\n" +
					"select phone, sender \n" +
					"from B1.SMS_SEND_BLOCK t, SMS_SENDERS s\n" +
					"where t.LAYER = s.ID\n" +
					"and t.layer = :sender_id");
			query.setParameter("sender_id", request.getParameter("sender").toString());
			List<Object[]> res = query.getResultList();
			PrintWriter pw = response.getWriter();

				pw.append("Number,Sender\n");
			for(Object[] rec : res){
				pw.append(rec[0] + "," + rec[1] + "\n");
			}
		} else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}
}
