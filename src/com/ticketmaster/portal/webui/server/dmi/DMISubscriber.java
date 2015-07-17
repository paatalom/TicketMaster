package com.ticketmaster.portal.webui.server.dmi;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;
import com.isomorphic.rpc.RPCManager;
import com.magti.billing.ejb.beans.entity.common.Office;
import com.magti.billing.ejb.beans.entity.subscomm.SubscriberCommentHist;
import com.magti.billing.ejb.beans.entity.subscriber.SubscriberSchedules;
import com.magti.billing.ejb.common.beans.GenericTranObject;
import com.magti.billing.ejb.common.beans.UserContext;
import com.magti.billing.ejb.exception.CCareEJBException;
import com.ticketmaster.portal.webui.server.common.DMIUtils;
import com.ticketmaster.portal.webui.server.jndi.JNDIManager;
import com.ticketmaster.portal.webui.server.session.ServerSession;
import com.ticketmaster.portal.webui.server.utils.ServerUtils;
import com.ticketmaster.portal.webui.shared.exception.PortalException;

public class DMISubscriber {
	@SuppressWarnings("rawtypes")
	public DSResponse execBatchOperation(DSRequest dsRequest, HttpServletRequest httpRequest, RPCManager rpcManager)
			throws PortalException {
		try {
			ServerSession serverSession = (ServerSession) httpRequest.getSession().getAttribute(
					ServerSession.SESSION_ATTRIBUTE_NAME);
			if (serverSession == null) {
				throw new PortalException("Invalid Session.");
			}

			UserContext userContext = serverSession.getUserContext();
			Map<?, ?> values = dsRequest.getValues();

			String userName = userContext.getUser().getUserName();
			Long userId = userContext.getUser().getUserId();
			Long subscriberId = Long.valueOf(values.get("subscriber_id").toString());

			Map resultCheck = DMIUtils.findRecordById(rpcManager, "FreeListsDS", "checkSubscribersContract",
					subscriberId, "subscriber_id");

			if (resultCheck == null || resultCheck.isEmpty()) {
				throw new PortalException("Invalid Subscriber.");
			}

			Long serviceId = Long.valueOf(values.get("service_id").toString());
			Double limit = Double.valueOf(values.get("limit").toString());
			Date change_date = DMIUtils.getRowValueDate(values.get("change_date"));
			Object commentText = values.get("comment");
			Long schedule_id = values.get("schedule_id") == null ? null : Long.parseLong(values.get("schedule_id")
					.toString());

			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

			Integer changeDateInt = new Integer(format.format(change_date));
			Timestamp currDate = new Timestamp(System.currentTimeMillis());
			Integer currDateInt = new Integer(format.format(currDate));

			boolean executeImmediate = changeDateInt.intValue() == currDateInt.intValue();

			if (executeImmediate) {

				GenericTranObject genTranObj = new GenericTranObject();
				genTranObj.setTransactional(true);
				genTranObj.setGenTranTypeId(serviceId);
				genTranObj.setSubscrId(subscriberId);
				genTranObj.setUser(userContext.getUser());

				TreeMap<Long, Double> limitT = new TreeMap<Long, Double>();
				limitT.put(5L, limit);
				genTranObj.setThresholdValues(limitT);

				if (commentText != null && !commentText.toString().equals("")) {

					SubscriberCommentHist subscriberCommentHist = new SubscriberCommentHist();
					subscriberCommentHist.setCommentText(commentText.toString());
					subscriberCommentHist.setCommentTypeId(2L);
					subscriberCommentHist.setRecDate(new Timestamp((new Date()).getTime()));
					subscriberCommentHist.setSubscriberId(subscriberId);
					subscriberCommentHist.setUserId(userId);
					subscriberCommentHist.setUserName(userName);
					genTranObj.setSubscriberCommentHist(subscriberCommentHist);
				}

				JNDIManager.getInstance().getTransactionFascadeNew().execGenericTran(genTranObj, userContext);
			} else {

				SubscriberSchedules subscriberSchedule = new SubscriberSchedules();

				subscriberSchedule.setScheduleDate(ServerUtils.getInstance().truncDate(
						new Timestamp(change_date.getTime())));
				subscriberSchedule
						.setEndDate(ServerUtils.getInstance().truncDate(new Timestamp(change_date.getTime())));
				subscriberSchedule.setThresholdValue(limit);
				subscriberSchedule.setScheduleTypeId(schedule_id);
				subscriberSchedule.setServEndDate(new Timestamp(change_date.getTime()));
				subscriberSchedule.setStartDate(currDate);

				if (commentText != null && !commentText.toString().equals("")) {
					subscriberSchedule.setSubsComment(commentText.toString());
				}

				subscriberSchedule.setSubscriberId(subscriberId);
				subscriberSchedule.setUserId(userId);
				subscriberSchedule.setUserName(userName);

				JNDIManager.getInstance().getTransactionFasade().addSubscriberSchedule(subscriberSchedule, userContext);
			}

			DSResponse dsResponse = new DSResponse();
			Map<String, Integer> result = new HashMap<String, Integer>();
			result.put("id", 1);
			dsResponse.setData(result);
			return dsResponse;
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof CCareEJBException) {
				CCareEJBException ejbEx = (CCareEJBException) e;
				throw new PortalException(ejbEx.getErrorMessage(), ejbEx.getThrowable());
			}
			if (e instanceof PortalException) {
				throw (PortalException) e;
			}
			throw new PortalException(e.getMessage());
		}
	}

	@SuppressWarnings("rawtypes")
	public DSResponse roamingServiceOperation(DSRequest dsRequest, HttpServletRequest httpRequest, RPCManager rpcManager)
			throws PortalException {
		try {
			Map<?, ?> values = dsRequest.getValues();
			ServerSession serverSession = (ServerSession) httpRequest.getSession().getAttribute(
					ServerSession.SESSION_ATTRIBUTE_NAME);
			if (serverSession == null) {
				throw new PortalException("Invalid Session.");
			}

			UserContext userContext = serverSession.getUserContext();

			Long subscriberId = DMIUtils.getRowValueLong(values.get("subscriber_id"));
			Long roamgttid = DMIUtils.getRowValueLong(values.get("roamgttid"));
			Double limit = DMIUtils.getRowValueDouble(values.get("limit"));
			Long ignore_params = DMIUtils.getRowValueLong(values.get("ignore_params"));

			Date start_date = DMIUtils.getRowValueDate(values.get("start_date"));
			Date end_date = DMIUtils.getRowValueDate(values.get("end_date"));

			Map resultCheck = DMIUtils.findRecordById(rpcManager, "FreeListsDS", "checkSubscribersContract",
					subscriberId, "subscriber_id");

			if (resultCheck == null || resultCheck.isEmpty()) {
				throw new PortalException("Invalid Subscriber.");
			}

			Object oComm = values.get("comment");
			String commentText = oComm == null ? null : oComm.toString();
			Office office = new Office();
			office.setId(userContext.getUser().getOfficeId());
			String userName = userContext.getUserName();

			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			Timestamp currDate = new Timestamp(System.currentTimeMillis());
			Integer currDateInt = new Integer(format.format(currDate));
			boolean executeImmediate = true;
			Timestamp truncEndDate = null;

			if (start_date != null && end_date != null) {
				Integer startDateInt = new Integer(format.format(start_date));
				executeImmediate = startDateInt.intValue() == currDateInt.intValue();
				truncEndDate = ServerUtils.getInstance().truncDate(end_date);
			}

			Long schedule_id = values.get("schedule_id") == null ? null : Long.parseLong(values.get("schedule_id")
					.toString());
			Long userId = userContext.getUser().getUserId();

			if (executeImmediate) {

				GenericTranObject genTranObj = new GenericTranObject();
				genTranObj.setTransactional(true);
				genTranObj.setGenTranTypeId(roamgttid);
				genTranObj.setSubscrId(subscriberId);
				genTranObj.setOffice(office);
				genTranObj.setUser(userContext.getUser());

				if (limit != null && ignore_params.equals(0L)) {
					TreeMap<Long, Double> limitT = new TreeMap<Long, Double>();
					limitT.put(37L, limit);
					genTranObj.setThresholdValues(limitT);
				}

				if (truncEndDate != null && ignore_params.equals(0L)) {
					TreeMap<Long, Timestamp> endDateT = new TreeMap<Long, Timestamp>();
					endDateT.put(161L, truncEndDate);
					genTranObj.setAccountEndDates(endDateT);

					TreeMap<Long, Timestamp> servEndDates = new TreeMap<Long, Timestamp>();
					servEndDates.put(7L, truncEndDate);
					servEndDates.put(188L, truncEndDate);
					genTranObj.setServiceEndDates(servEndDates);
				}

				if (commentText != null && !commentText.toString().equals("")) {
					SubscriberCommentHist subscriberCommentHist = new SubscriberCommentHist();
					subscriberCommentHist.setCommentText(commentText.toString());
					subscriberCommentHist.setCommentTypeId(1L);
					subscriberCommentHist.setRecDate(new Timestamp((new Date()).getTime()));
					subscriberCommentHist.setSubscriberId(subscriberId);
					subscriberCommentHist.setUserId(userId);
					subscriberCommentHist.setUserName(userName);
					genTranObj.setSubscriberCommentHist(subscriberCommentHist);
				}

				JNDIManager.getInstance().getTransactionFascadeNew().execGenericTran(genTranObj, userContext);
			} else {
				if (schedule_id == null) {
					throw new PortalException("ამ სერვისისათვის გრაფიკი ვერ მოიძებნა !");
				}

				SubscriberSchedules subscriberSchedule = new SubscriberSchedules();

				subscriberSchedule.setScheduleDate(ServerUtils.getInstance().truncDate(
						new Timestamp(start_date.getTime())));
				subscriberSchedule.setEndDate(ServerUtils.getInstance().truncDate(new Timestamp(start_date.getTime())));
				subscriberSchedule.setThresholdValue(limit);

				subscriberSchedule.setScheduleTypeId(schedule_id);
				subscriberSchedule.setServEndDate(truncEndDate);
				subscriberSchedule.setStartDate(currDate);
				subscriberSchedule.setSubsComment(commentText);
				subscriberSchedule.setSubscriberId(subscriberId);
				subscriberSchedule.setUserId(userId);
				subscriberSchedule.setUserName(userName);

				JNDIManager.getInstance().getTransactionFasade().addSubscriberSchedule(subscriberSchedule, userContext);
			}

			DSResponse dsResponse = new DSResponse();
			Map<String, Integer> result = new HashMap<String, Integer>();
			result.put("id", 1);
			dsResponse.setData(result);
			return dsResponse;
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof CCareEJBException) {
				CCareEJBException ejbEx = (CCareEJBException) e;
				throw new PortalException(ejbEx.getErrorMessage(), ejbEx.getThrowable());
			}
			throw new PortalException(e.getMessage());
		}
	}

	@SuppressWarnings("rawtypes")
	public DSResponse remRoamingSchedules(DSRequest dsRequest, HttpServletRequest httpRequest, RPCManager rpcManager)
			throws PortalException {
		try {
			ServerSession serverSession = (ServerSession) httpRequest.getSession().getAttribute(
					ServerSession.SESSION_ATTRIBUTE_NAME);
			if (serverSession == null) {
				throw new PortalException("Invalid Session.");
			}

			UserContext userContext = serverSession.getUserContext();
			Map<?, ?> values = dsRequest.getValues();

			Long subscriberId = Long.valueOf(values.get("subscriber_id").toString());

			Map resultCheck = DMIUtils.findRecordById(rpcManager, "FreeListsDS", "checkSubscribersContract",
					subscriberId, "subscriber_id");
			if (resultCheck == null || resultCheck.isEmpty()) {
				throw new PortalException("Invalid Subscriber.");
			}

			Office office = new Office();
			office.setId(userContext.getUser().getOfficeId());
			String schedule_id_list = values.get("schedule_id_list").toString();
			if (!schedule_id_list.equals("NULL")) {
				String array[] = schedule_id_list.split(",");
				ArrayList<Long> idList = new ArrayList<Long>();
				for (String string : array) {
					idList.add(Long.parseLong(string));
				}
				JNDIManager.getInstance().getTransactionFasade().deleteSubscriberSchedules(idList, userContext);
			}

			DSResponse dsResponse = new DSResponse();
			Map<String, Integer> result = new HashMap<String, Integer>();
			result.put("id", 1);
			dsResponse.setData(result);
			return dsResponse;
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof CCareEJBException) {
				CCareEJBException ejbEx = (CCareEJBException) e;
				throw new PortalException(ejbEx.getErrorMessage(), ejbEx.getThrowable());
			}
			throw new PortalException(e.getMessage());
		}
	}

	@SuppressWarnings("rawtypes")
	public DSResponse roamTermAndLimitExtension(DSRequest dsRequest, HttpServletRequest httpRequest,
			RPCManager rpcManager) throws PortalException {
		try {
			ServerSession serverSession = (ServerSession) httpRequest.getSession().getAttribute(
					ServerSession.SESSION_ATTRIBUTE_NAME);
			if (serverSession == null) {
				throw new PortalException("Invalid Session.");
			}

			UserContext userContext = serverSession.getUserContext();
			Map<?, ?> values = dsRequest.getValues();

			String userName = userContext.getUser().getUserName();
			Long userId = userContext.getUser().getUserId();
			Long subscriberId = Long.valueOf(values.get("subscriber_id").toString());

			Map resultCheck = DMIUtils.findRecordById(rpcManager, "FreeListsDS", "checkSubscribersContract",
					subscriberId, "subscriber_id");

			if (resultCheck == null || resultCheck.isEmpty()) {
				throw new PortalException("Invalid Subscriber.");
			}

			Double limit = values.get("limit") == null ? null : Double.valueOf(values.get("limit").toString());
			Date end_date = (Date) values.get("end_date");
			Object oComm = values.get("comment");
			String commentText = oComm == null ? null : oComm.toString();
			Office office = new Office();
			office.setId(userContext.getUser().getOfficeId());

			GenericTranObject genTranObj = new GenericTranObject();
			genTranObj.setTransactional(true);
			genTranObj.setGenTranTypeId(com.magti.billing.ejb.common.Constants.GTT_ROAM_TERM_AND_LIMIT_EXT);
			genTranObj.setSubscrId(subscriberId);
			genTranObj.setOffice(office);
			genTranObj.setUser(userContext.getUser());

			if (end_date != null) {
				Timestamp endDateTime = ServerUtils.getInstance().truncDate(new Timestamp(end_date.getTime()));

				TreeMap<Long, Timestamp> endDateT = new TreeMap<Long, Timestamp>();
				endDateT.put(161L, endDateTime);
				genTranObj.setAccountEndDates(endDateT);

				TreeMap<Long, Timestamp> servEndDates = new TreeMap<Long, Timestamp>();
				servEndDates.put(7L, endDateTime);
				servEndDates.put(188L, endDateTime);
				genTranObj.setServiceEndDates(servEndDates);

			}
			TreeMap<Long, Double> limitT = new TreeMap<Long, Double>();
			if (limit != null) {
				limitT.put(37L, limit);
			} else {
				limitT.put(-99L, 0D);
			}
			genTranObj.setThresholdValues(limitT);

			if (commentText != null && !commentText.toString().equals("")) {
				SubscriberCommentHist subscriberCommentHist = new SubscriberCommentHist();
				subscriberCommentHist.setCommentText(commentText.toString());
				subscriberCommentHist.setCommentTypeId(1L);
				subscriberCommentHist.setRecDate(new Timestamp(System.currentTimeMillis()));
				subscriberCommentHist.setSubscriberId(subscriberId);
				subscriberCommentHist.setUserId(userId);
				subscriberCommentHist.setUserName(userName);
				genTranObj.setSubscriberCommentHist(subscriberCommentHist);
			}

			JNDIManager.getInstance().getTransactionFascadeNew().execGenericTran(genTranObj, userContext);
			DSResponse dsResponse = new DSResponse();
			Map<String, Integer> result = new HashMap<String, Integer>();
			result.put("id", 1);
			dsResponse.setData(result);
			return dsResponse;
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof CCareEJBException) {
				CCareEJBException ejbEx = (CCareEJBException) e;
				throw new PortalException(ejbEx.getErrorMessage(), ejbEx.getThrowable());
			}
			throw new PortalException(e.getMessage());
		}
	}

	public DSResponse sendCorpNotificationMail(DSRequest dsRequest, HttpServletRequest httpRequest,
			RPCManager rpcManager) throws PortalException {
		try {
			ServerSession serverSession = (ServerSession) httpRequest.getSession().getAttribute(
					ServerSession.SESSION_ATTRIBUTE_NAME);
			if (serverSession == null) {
				throw new PortalException("Invalid Session.");
			}
			UserContext userContext = serverSession.getUserContext();

			Map<?, ?> values = dsRequest.getValues();

			String email = values.get("email").toString();
			String subject = values.get("subject").toString();
			String text = values.get("text").toString();

			JNDIManager.getInstance().getUserManagerBean().sendCorpNotificationMail(email, subject, text);

			String clientMail = userContext.getUser().getUserName();
			JNDIManager.getInstance().getUserManagerBean().sendCorpNotificationMail(clientMail, subject, text);

			DSResponse dsResponse = new DSResponse();
			Map<String, Integer> result = new HashMap<String, Integer>();
			result.put("id", 1);
			dsResponse.setData(result);
			return dsResponse;
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof CCareEJBException) {
				CCareEJBException ejbEx = (CCareEJBException) e;
				throw new PortalException(ejbEx.getErrorMessage(), ejbEx.getThrowable());
			}
			throw new PortalException(e.getMessage());
		}
	}
}
