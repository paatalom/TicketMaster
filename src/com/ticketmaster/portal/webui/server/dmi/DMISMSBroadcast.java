package com.ticketmaster.portal.webui.server.dmi;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;
import com.isomorphic.jpa.EMF;
import com.magti.billing.ejb.common.beans.UserContext;
import com.ticketmaster.portal.webui.server.session.ServerSession;
import com.ticketmaster.portal.webui.shared.smsBroadcast.Util;

import org.hibernate.SQLQuery;
import org.hibernate.internal.SessionImpl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by vano on 2/24/15.
 */

public class DMISMSBroadcast {

	private EntityManager em = EMF.getEntityManager();

	private class SmsInfo {
		private BigDecimal id;
		private String sms;
		private String name;
		private String sender;
		private Long contractId;
		private Long userId;
		private Long category;
		private InputStream fileInputStream;

		public BigDecimal getId() {
			return id;
		}

		public void setId(BigDecimal id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSms() {
			return sms;
		}

		public void setSms(String sms) {
			this.sms = sms;
		}

		public String getSender() {
			return sender;
		}

		public void setSender(String sender) {
			this.sender = sender;
		}

		public Long getUserId() {
			return userId;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}

		public InputStream getFileInputStream() {
			return fileInputStream;
		}

		public void setFileInputStream(InputStream fileInputStream) {
			this.fileInputStream = fileInputStream;
		}

		public Long getCategory() {
			return category;
		}

		public void setCategory(Long category) {
			this.category = category;
		}

		public Long getContractId() {
			return contractId;
		}

		public void setContractId(Long contractId) {
			this.contractId = contractId;
		}
	}

	public DSResponse sendFilteredSms(DSRequest request, HttpSession httpSession){
		String err = "";
		DSResponse response = new DSResponse();
		ServerSession serverSession = (ServerSession) httpSession.getAttribute(
				ServerSession.SESSION_ATTRIBUTE_NAME);
		if (serverSession == null) {
			err = "Invalid Session";
		} else {
			UserContext userContext = serverSession.getUserContext();

			Map<?, ?> c = request.getCriteria();

			Long contractId = (Long) c.get("contract_id");
			String senderS = (String) c.get("sender");
			Long regionL = (Long) c.get("region");
			String sms = (String) c.get("sms");
			String name = (String) c.get("name");
			String smsOffText = (String) c.get("sms_off_text");
			Boolean includeSmsOff = (Boolean) c.get("sms_off_check_box");
			if(includeSmsOff) {
				sms += smsOffText;
			}
//			System.out.println(">>>>>>>>> " + sms);
			Long typeL = (Long) c.get("type");
			Long districtL = (Long) c.get("district");
			Long phoneTypeL = (Long) c.get("phone_type");
			Long chargeL = (Long) c.get("charge");
			Long genderL = (Long) c.get("gender");
			String limitS = (String) c.get("limit");
			Integer limit = Integer.parseInt(limitS);

			Integer sender = senderS == null ? null : Integer.parseInt(senderS);
			Integer region = regionL == null ? 0 : regionL.intValue();
			Integer type = typeL == null ? 0 : typeL.intValue();
			Integer district = districtL == null ? 0 : districtL.intValue();
			Integer phoneType = phoneTypeL == null ? 0 : phoneTypeL.intValue();
			Integer charge = chargeL == null ? 0 : chargeL.intValue();
			Integer gender = genderL == null ? 0 : genderL.intValue();

			if (!Util.isValidFilter(region, district, type, phoneType, charge, gender)) {
			 	initResponse("Invalid Filter", response);
				return response;
			}

			em.getTransaction().begin();
			try {
				Query query;

				// GET ID
				query = em.createNativeQuery("select ccare_portal.seq_sms_info.nextval from dual");
				query.executeUpdate();
				BigDecimal id = (BigDecimal) query.getSingleResult();

				// GENERATE SELECT QUERY
				String selectQuery = generateSelectQuery(id, region, district, type, phoneType, charge, gender, limit);
				System.out.println(selectQuery);

				// GENERATE TEST QUERY

//				String selectTestQuery = generateTestQuery(id, region, district, type, phoneType, charge, gender, limit);
//				System.out.println(selectTestQuery);

				// CHECK QUERY
//				query = em.createNativeQuery("select count(*) cou from ( " + selectQuery + " ) ");
//				BigDecimal cou = (BigDecimal) query.getSingleResult();
//				System.out.println("Count: " + cou);
				//selectQuery += "\n and s.phone_number > ?\n order by s.phone_number";

				// GET SMS COUNT
				int smsCount = Util.countSMS(sms);
				if(smsCount > 2) {
					throw new Exception("Sms count limit 2");
				}
				int isGeo = Util.isASCII(sms) ? 0 : 1;

				// ADD SMS INFO
				query = em.createNativeQuery("insert into ccare_portal.SMS_INFO (id, name, sms_text, sms_sender_id, contract_id, user_id, type_id, SMS_COUNT, IS_GEO)"
						+ " values(:id, :name, :sms_text, :sms_sender_id, :contract_id, :user_id, :type_id, :sms_count, :is_geo) ");
				query.setParameter("id", id);
				query.setParameter("sms_text", sms);
				query.setParameter("sms_sender_id", sender);
				query.setParameter("contract_id", contractId);
				query.setParameter("user_id", userContext.getUser().getUserId());
				query.setParameter("type_id", 2);
				query.setParameter("sms_count", smsCount);
				query.setParameter("is_geo", isGeo);
				query.setParameter("name", name);
//				query.setParameter("priority", cou);
				query.executeUpdate();

				// ADD PARAMETERS
				String insertQuery = generateInsertQuery(selectQuery, limit);
				query = em.createNativeQuery("insert into ccare_portal.SMS_FILTER_QUERY(SMS_INFO_ID, QUERY, REGION, DISTRICT, party_type, PHONE_TYPE, CHARGE, GENDER, STATUS, DAILY_LIMIT) " +
						"values(:sms_info_id, :query, :region, :district, :party_type, :phone_type, :charge, :gender, 0, :limit)");
				query.setParameter("sms_info_id", id);
				query.setParameter("query", insertQuery);
				query.setParameter("region", region);
				query.setParameter("district", district);
				query.setParameter("party_type", type);
				query.setParameter("phone_type", phoneType);
				query.setParameter("charge", charge);
				query.setParameter("gender", gender);
				query.setParameter("limit", limit);
				query.executeUpdate();

				em.getTransaction().commit();
			} catch (Exception e) {
				err = e.getMessage();
				em.getTransaction().rollback();
				e.printStackTrace();
			}

		}
		initResponse(err, response);
		return response;
	}

	private void initResponse(String err, DSResponse response){
		Map<String, Object> res = new HashMap<>();
		res.put("status", err);
		response.setData(res);
	}

	private String generateTestQuery(BigDecimal id, Integer region, Integer district, Integer type, Integer phoneType, Integer charge, Integer gender, Integer limit){
		StringBuilder sb = new StringBuilder();
		String ddd;
		if(limit != null && limit > 0){
			ddd = " sysdate + floor(rownum / " + limit + ") ";
		} else {
			ddd = " 1 ";
		}
		sb.append("select id, phone, " + ddd + " from (");
		sb.append("SELECT " + id + " id, 595212288 phone FROM DUAL ");
		sb.append("UNION ALL ");
		sb.append("SELECT " + id + ", 595151313 FROM DUAL ");
		sb.append("UNION ALL ");
		sb.append("SELECT " + id + ", 598380076 FROM DUAL ");
		sb.append("UNION ALL ");
		sb.append("SELECT " + id + ", 595919922 FROM DUAL ");
		sb.append("UNION ALL ");
		sb.append("SELECT " + id + ", 599443000 FROM DUAL ");
		sb.append(")");
		return sb.toString();
	}

	private String generateSelectQuery(BigDecimal id, Integer region, Integer district, Integer type, Integer phoneType, Integer charge, Integer gender, Integer limit){
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT " + id + " sms_info_id, s.PHONE_NUMBER");
		if(limit != null && limit > 0){
			sb.append(", sysdate + floor(rownum / " + limit + ") send_date ");
		} else {
			sb.append(", 1 state_id ");
		}
		sb.append(" \nFROM ccare_portal.SMS_BROADCAST_SUBSCRIBERS s\n" +
				" WHERE 1 = 1 \n");

		if(gender != null && gender > 0){
			sb.append("AND s.GENDER = " + gender + " \n");
		}
		if(charge != null && charge > 0){
			sb.append("AND s.CHARGE_SEGMENT = " + charge + " \n");
		}
		if(type != null && type > 0){
			sb.append("AND s.PARTY_TYPE = " + type + " \n");
		}
		if(region != null && region != 0 ||
				district != null && district != 0 ||
				phoneType != null && phoneType != 0){
			sb.append("AND s.PHONE_NUMBER IN (SELECT p.PHONE_NUMBER FROM ccare_portal.SMS_BROADCAST_PHONES p\n" +
					" WHERE 1 = 1 \n");
			if(region != null && region != 0){
				sb.append("AND p.REGION_ID = " + region + " \n");
			}
			if(district != null && district != 0){
				sb.append("AND p.DISTRICT_ID = " + district + " \n");
			}
			if(phoneType != null && phoneType != 0){
				sb.append("AND p.IS_SMARTPHONE = " + phoneType + " \n");
			}
			sb.append(")");
		}
		return sb.toString();
	}

	private String generateInsertQuery(String selectQuery, Integer limit){
		String query;
		if(limit != null && limit > 0){
			query = "insert into ccare_portal.SMS_DELAYED_QUEUE(SMS_INFO_ID, PHONE_NUMBER, send_date) \n" + selectQuery;
		} else {
			query = "insert into ccare_portal.SMS_QUEUE(SMS_INFO_ID, PHONE_NUMBER, STATE_ID) \n" + selectQuery;
		}

		return query;
	}

	public DSResponse uploadNumbers(DSRequest dsRequest, HttpServletRequest request){
		DSResponse response = new DSResponse();
		String err = "";

		ServerSession serverSession = (ServerSession) request.getSession().getAttribute(
				ServerSession.SESSION_ATTRIBUTE_NAME);
		if (serverSession == null) {
			err = "Invalid Session";
		} else {
			UserContext userContext = serverSession.getUserContext();
			Map values = dsRequest.getValues();
			System.out.println(values);

			SmsInfo smsInfo = new SmsInfo();
			smsInfo.setUserId(userContext.getUser().getUserId());
			smsInfo.setName((String) values.get("name"));
			smsInfo.setSender((String) values.get("sender"));
			smsInfo.setContractId((Long) values.get("contract_id"));
			smsInfo.setCategory((Long) values.get("category"));
			String sms = (String) values.get("sms");
			String smsOffText = (String) values.get("sms_off_text");
			Boolean includeSmsOff = (Boolean) values.get("sms_off_check_box");
			if(includeSmsOff) {
				sms += smsOffText;
			}
			smsInfo.setSms(sms);
			smsInfo.setFileInputStream((ByteArrayInputStream) values.get("file"));

			em.getTransaction().begin();
			try {
				// GET ID
				Query query = em.createNativeQuery("select CCARE_PORTAL.seq_sms_info.nextval from dual");
				query.executeUpdate();
				BigDecimal id = (BigDecimal) query.getSingleResult();

				// GET SMS COUNT
				int smsCount = Util.countSMS(smsInfo.getSms());
				int isGeo = Util.isASCII(smsInfo.getSms()) ? 0 : 1;

				// get attributes
				query = em.createNativeQuery("SELECT\n" +
						"bi.add_condit_checked  no_delivery,\n" +
						"bi1.add_condit_checked show_in_report\n" +
						"FROM ccare.bundle t\n" +
						"INNER JOIN ccare.bundle_to_bundle_items tt ON tt.bundle_id = t.id\n" +
						"INNER JOIN ccare.bundle_items bi ON bi.id = tt.bundle_item_id AND bi.bundle_discount_id = 9001\n" +
						"INNER JOIN ccare.bundle_discounts bd ON bd.id = bi.bundle_discount_id\n" +
						"INNER JOIN ccare.bundle_to_bundle_items tt1 ON tt1.bundle_id = t.id\n" +
						"INNER JOIN ccare.bundle_items bi1 ON bi1.id = tt1.bundle_item_id AND bi1.bundle_discount_id = 9000\n" +
						"INNER JOIN ccare.bundle_discounts bd1 ON bd1.id = bi1.bundle_discount_id\n" +
//					"INNER JOIN USER_MANAGER.USER_ASSIGNED_PARTIES up ON t.Contract_id = up.CONTRACT_ID\n" +
						"WHERE t.contract_id = :contract_id \n" +
						"AND t.subscriber_type_group_id = 7");

				query.setParameter("contract_id", smsInfo.getContractId());
				List<Object[]> data = query.getResultList();
				Integer noDelivery;
				Integer showInReport;
				if (data.size() == 0) {
					noDelivery = 0;
					showInReport = 0;
				} else {
					noDelivery = Integer.parseInt(data.get(0)[0].toString());
					showInReport = Integer.parseInt(data.get(0)[1].toString());
				}


				// ADD SMS INFO
				query = em.createNativeQuery("insert into CCARE_PORTAL.SMS_INFO (id, name, sms_text, sms_sender_id, contract_id, user_id, type_id, SMS_COUNT, IS_GEO, NO_DELIVERY)"
						+ " values(:id, :name, :sms_text, :sms_sender_id, :contract_id, :user_id, :type_id, :sms_count, :is_geo, :no_delivery) ");
				query.setParameter("id", id);
				query.setParameter("sms_text", smsInfo.getSms());
				query.setParameter("sms_sender_id", smsInfo.getSender());
				query.setParameter("contract_id", smsInfo.getContractId());
				query.setParameter("user_id", smsInfo.getUserId());
				query.setParameter("type_id", 1);
				query.setParameter("sms_count", smsCount);
				query.setParameter("is_geo", isGeo);
				query.setParameter("name", smsInfo.getName());
				query.setParameter("no_delivery", noDelivery);
				query.executeUpdate();
				smsInfo.setId(id);
				em.getTransaction().commit();

				NumberInserter numberInserter = new NumberInserter(smsInfo, noDelivery, showInReport);
				Thread th = new Thread(numberInserter);
				th.start();

			} catch (Exception e){
				err = e.getMessage();
				em.getTransaction().rollback();
				e.printStackTrace();
			}

		}
		err = err.replaceAll("[\n\r\t]+", " ");
		Map<String, Object> res = new HashMap<>();
		res.put("status", err);
		response.setData(res);
		return response;
	}

	public DSResponse uploadBlackList(DSRequest dsRequest, HttpServletRequest request){
		DSResponse response = new DSResponse();
		String err;

		ServerSession serverSession = (ServerSession) request.getSession().getAttribute(
				ServerSession.SESSION_ATTRIBUTE_NAME);
		if (serverSession == null) {
			err = "Invalid Session";
		} else {
			UserContext userContext = serverSession.getUserContext();
			Map values = dsRequest.getValues();
			System.out.println(values);

			err = saveBlackList(userContext.getUser().getUserId(),
					(Long) values.get("sender"),
					(ByteArrayInputStream) values.get("file"));
		}
		Map<String, Object> res = new HashMap<>();
		res.put("status", err);
		response.setData(res);
		return response;
	}

	private String saveBlackList(Long userId, Long senderId, InputStream is){
		String err = "";

		em.getTransaction().begin();
		try {
			Query query;

			// DELETE OLD NUMBERS
			query = em.createNativeQuery("delete from CCARE_PORTAL.SMS_BLACKLIST where SENDER_ID = :sender_id");
			query.setParameter("sender_id", senderId);
			query.executeUpdate();

			// INSERT NEW NUMBERS
			Scanner scanner = new Scanner(is);
			int i = 0;
			while(scanner.hasNext()) {
				i++;
				String phone = scanner.next();
				query = em.createNativeQuery("insert into CCARE_PORTAL.SMS_BLACKLIST (phone, sender_id, user_id)"
						+ " values(:phone, :sender_id, :user_id) ");
				query.setParameter("phone", phone);
				query.setParameter("sender_id", senderId);
				query.setParameter("user_id", userId);
				if(!Util.isValidNumber(phone)){
					throw new Exception("არასწორი ფორმატი. ნომერი: " + phone + ", ხაზი: " + i);
				}
				query.executeUpdate();
			}

			em.getTransaction().commit();
		} catch(Exception e){
			err = e.getMessage();
			em.getTransaction().rollback();
			e.printStackTrace();
		}
		return err;
	}

	public DSResponse checkDelivery(DSRequest request, HttpSession session) throws Exception{
		DSResponse response = new DSResponse();

		ServerSession serverSession = (ServerSession) session.getAttribute(
				ServerSession.SESSION_ATTRIBUTE_NAME);
		if (serverSession == null) {
			throw new Exception("Session Expired");
		}
		UserContext userContext = serverSession.getUserContext();
		Query query = em.createNativeQuery("SELECT \n" +
				"bi.add_condit_checked  no_delivery \n" +
				"FROM ccare.bundle t \n" +
				"INNER JOIN ccare.bundle_to_bundle_items tt ON tt.bundle_id = t.id \n" +
				"INNER JOIN ccare.bundle_items bi ON bi.id = tt.bundle_item_id AND bi.bundle_discount_id = 9001 \n" +
				"INNER JOIN ccare.bundle_discounts bd ON bd.id = bi.bundle_discount_id \n" +
				"INNER JOIN USER_MANAGER.USER_ASSIGNED_PARTIES up ON t.Contract_id = up.CONTRACT_ID \n" +
				"WHERE up.USER_ID = :user_id \n" +
				"AND t.subscriber_type_group_id = 7");

		query.setParameter("user_id", userContext.getUser().getUserId());
		Object noDelivery = query.getSingleResult();
		Map<String, Object> res = new HashMap<>();
		res.put("no_delivery", "1".equals(noDelivery.toString()));
		response.setData(res);
		return response;
	}

	class NumberInserter implements Runnable{
		private SmsInfo smsInfo;
		private Integer noDelivery;
		private Integer showInReport;
		public NumberInserter(SmsInfo smsInfo, Integer noDelivery, Integer showInReport){
			this.smsInfo = smsInfo;
			this.noDelivery = noDelivery;
			this.showInReport = showInReport;
		}

		public void run(){
			String err = "";
			int i = 0;
			int line = 0;
			try {
				em.getTransaction().begin();
				try {
					Query query;

					// ADD NUMBERS

					Scanner scanner = new Scanner(smsInfo.getFileInputStream(), "UTF-8");
					SQLQuery sqlQuery;
					while (scanner.hasNext()) {
						String phone = scanner.next();
						line++;
						if(phone == null || phone.isEmpty()) {
							continue;
						}
						if(noDelivery > 0) {
							// CHECK BLOCKED NUMBERS
							query = em.createNativeQuery("select 1 From b1.sms_send_block t\n" +
									"where t.PHONE = :phone \n" +
									"and t.LAYER = :layer ");
							query.setParameter("phone", phone);
							query.setParameter("layer", smsInfo.getCategory());
							List block = query.getResultList();
							if (block.size() > 0) {
								continue;
							}
						}
						i++;

						query = em.createNativeQuery("insert into CCARE_PORTAL.sms_queue (sms_info_id, phone_number)"
								+ " values(:sms_info_id, :phone_number)");
						query.setParameter("sms_info_id", smsInfo.getId());
						query.setParameter("phone_number", phone);
						if (!Util.isValidNumber(phone)) {
							throw new Exception("არასწორი ფორმატი. ნომერი: " + phone + ", ხაზი: " + line);
						}
						query.executeUpdate();

						// SHOW IN REPORT
						if(showInReport > 0) {
							sqlQuery = ((SessionImpl) em.getDelegate()).createSQLQuery("call CCARE_PORTAL.INSERT_FOR_REPORT(:phone, :id)");
							sqlQuery.setParameter("phone", phone);
							sqlQuery.setParameter("id", smsInfo.getId());
							sqlQuery.executeUpdate();
						}
					}

					// SET PRIORITY (COUNT)
					query = em.createNativeQuery("update CCARE_PORTAL.SMS_INFO " +
							"SET PRIORITY = :priority, " +
							"STATE_ID = 1 " +
							"where id = :id");
					query.setParameter("priority", i);
					query.setParameter("id", smsInfo.getId());
					query.executeUpdate();


					em.getTransaction().commit();
				} catch (Exception e) {
					err = e.getMessage();
					em.getTransaction().rollback();
					System.out.println("----------------- num count " + i);
					e.printStackTrace();
					em.getTransaction().begin();
					em.createNativeQuery("UPDATE CCARE_PORTAL.SMS_INFO " +
							"SET STATE_ID = 2, " +
							"ERROR = :err " +
							"WHERE ID = :id")
							.setParameter("id", smsInfo.getId())
							.setParameter("err", err)
							.executeUpdate();
					em.getTransaction().commit();
				} finally {
					System.out.println(">>>>>>>>>>>>>>>> finished");
				}
				System.out.println(err);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
