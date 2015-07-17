package com.ticketmaster.portal.webui.client.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.ticketmaster.portal.webui.shared.utils.Constants;
import com.smartgwt.client.util.SC;

public class ClientMapUtil {
	private static ClientMapUtil instance;

	public static ClientMapUtil getInstance() {
		if (instance == null) {
			instance = new ClientMapUtil();
		}
		return instance;
	}

	private LinkedHashMap<String, String> languages;
	private LinkedHashMap<String, String> serviceParams;
	private LinkedHashMap<String, String> identifiers;
	private LinkedHashMap<String, String> identifiersContract;
	private LinkedHashMap<String, String> identifiersParty;
	private LinkedHashMap<String, String> documentTypes;
	private LinkedHashMap<String, String> orgTypes;
	private LinkedHashMap<String, String> searchTypes;
	private LinkedHashMap<String, String> priceTypes;
	private LinkedHashMap<String, String> prevOperators;
	private LinkedHashMap<String, String> genders;
	private LinkedHashMap<String, String> accTransfTypes;
	private LinkedHashMap<String, String> servTransfTypes;
	private LinkedHashMap<String, String> bundleTypes;
	private LinkedHashMap<String, String> bundleTypesBB;
	private LinkedHashMap<String, String> subscriberOwners;
	private LinkedHashMap<String, String> limExcAmmTransfTypes;
	private LinkedHashMap<Integer, String> monthsGE;
	private LinkedHashMap<Integer, String> monthsEN;
	private LinkedHashMap<String, String> actionTypes;
	private LinkedHashMap<Long, Long> simInfoList;
	private LinkedHashMap<String, String> invoiceGroups;
	private LinkedHashMap<String, String> thrTypes;
	private LinkedHashMap<String, String> corpPartyBillCftTypes;
	private LinkedHashMap<String, String> corpPartyBillCftTypes1;
	private LinkedHashMap<String, String> bundlePriceChargeType;

	protected ClientMapUtil() {
		languages = new LinkedHashMap<String, String>();
		priceTypes = new LinkedHashMap<String, String>();
		prevOperators = new LinkedHashMap<String, String>();
		genders = new LinkedHashMap<String, String>();
		accTransfTypes = new LinkedHashMap<String, String>();
		servTransfTypes = new LinkedHashMap<String, String>();
		bundleTypes = new LinkedHashMap<String, String>();
		bundleTypesBB = new LinkedHashMap<String, String>();
		subscriberOwners = new LinkedHashMap<String, String>();
		limExcAmmTransfTypes = new LinkedHashMap<String, String>();
		monthsGE = new LinkedHashMap<Integer, String>();
		monthsEN = new LinkedHashMap<Integer, String>();
		actionTypes = new LinkedHashMap<String, String>();
		simInfoList = new LinkedHashMap<Long, Long>();
		invoiceGroups = new LinkedHashMap<String, String>();
		thrTypes = new LinkedHashMap<String, String>();
		corpPartyBillCftTypes = new LinkedHashMap<String, String>();
		corpPartyBillCftTypes1 = new LinkedHashMap<String, String>();
		bundlePriceChargeType = new LinkedHashMap<String, String>();

		languages.put("1", "ქართული");
		languages.put("2", "English");

		serviceParams = new LinkedHashMap<String, String>();
		serviceParams.put("1", "Phone");
		serviceParams.put("2", "SIM");

		identifiers = new LinkedHashMap<String, String>();
		identifiers.put("1", "Subscriber ID");
		identifiers.put("2", "Account ID");
		identifiers.put("3", "Contract ID");
		identifiers.put("4", "Party ID");

		identifiersContract = new LinkedHashMap<String, String>();
		identifiersContract.put("3", "Contract ID");
		identifiersContract.put("4", "Party ID");

		identifiersParty = new LinkedHashMap<String, String>();
		identifiersParty.put("4", "Party ID");

		searchTypes = new LinkedHashMap<String, String>();
		searchTypes.put("1", TicketMaster.constants.subscriber());
		searchTypes.put("2", TicketMaster.constants.contract());
		searchTypes.put("3", TicketMaster.constants.party());

		priceTypes.put("1", TicketMaster.constants.percentage());
		priceTypes.put("2", TicketMaster.constants.fixed());
		priceTypes.put("3", TicketMaster.constants.first_minute());

		prevOperators.put("none", TicketMaster.constants.none());
		prevOperators.put("bali", TicketMaster.constants.bali());
		prevOperators.put("bani", TicketMaster.constants.bani());
		prevOperators.put("magti", TicketMaster.constants.magti());
		prevOperators.put("geocell", TicketMaster.constants.geocell());
		prevOperators.put("lailai", TicketMaster.constants.lailai());
		prevOperators.put("beeline", TicketMaster.constants.beeline());
		prevOperators.put("iberaitel", TicketMaster.constants.iberiatel());
		prevOperators.put("other", TicketMaster.constants.other_one());

		genders.put("0", TicketMaster.constants.female());
		genders.put("1", TicketMaster.constants.male());

		accTransfTypes.put("1", TicketMaster.constants.transfer());
		accTransfTypes.put("2", TicketMaster.constants.left());
		accTransfTypes.put("100", TicketMaster.constants.transfer_negative());
		accTransfTypes.put("200", TicketMaster.constants.transfer_positive());

		servTransfTypes.put("1", TicketMaster.constants.transfer());
		servTransfTypes.put("2", TicketMaster.constants.turn_off());

		bundleTypes.put("1", TicketMaster.constants.once());
		bundleTypes.put("2", TicketMaster.constants.nowAndEveryMonthFirstDay());
		bundleTypes.put("3", TicketMaster.constants.everyMonthFirstDay());

		bundleTypesBB.put("2", TicketMaster.constants.nowAndEveryMonthFirstDay());

		subscriberOwners.put("0", TicketMaster.constants.organization());
		subscriberOwners.put("1", TicketMaster.constants.private_person());

		limExcAmmTransfTypes.put("1", TicketMaster.constants.move_to_private_account());
		limExcAmmTransfTypes.put("2", TicketMaster.constants.move_to_total());
		limExcAmmTransfTypes.put("3", TicketMaster.constants.left_on_corp_account());
		limExcAmmTransfTypes.put("4", TicketMaster.constants.write_off());

		monthsGE.put(1, "იანვარი");
		monthsGE.put(2, "თებერვალი");
		monthsGE.put(3, "მარტი");
		monthsGE.put(4, "აპრილი");
		monthsGE.put(5, "მაისი");
		monthsGE.put(6, "ივნისი");
		monthsGE.put(7, "ივლისი");
		monthsGE.put(8, "აგვისტო");
		monthsGE.put(9, "სექტემბერი");
		monthsGE.put(10, "ოქტომბერი");
		monthsGE.put(11, "ნოემბერი");
		monthsGE.put(12, "დეკემბერი");

		monthsEN.put(1, "January");
		monthsEN.put(2, "February");
		monthsEN.put(3, "March");
		monthsEN.put(4, "April");
		monthsEN.put(5, "May");
		monthsEN.put(6, "June");
		monthsEN.put(7, "July");
		monthsEN.put(8, "August");
		monthsEN.put(9, "September");
		monthsEN.put(10, "October");
		monthsEN.put(11, "November");
		monthsEN.put(12, "December");

		actionTypes.put("1", TicketMaster.constants.now());
		actionTypes.put("2", TicketMaster.constants.shedule());

		simInfoList.put(Constants.SERVICE_CODE_MOBILE, Constants.SERVICE_CODE_MOBILE);
		simInfoList.put(Constants.SERVICE_CODE_BANI_MOBILE, Constants.SERVICE_CODE_BANI_MOBILE);
		simInfoList.put(Constants.SERVICE_CODE_CDMA_MOBILE, Constants.SERVICE_CODE_CDMA_MOBILE);
		simInfoList.put(Constants.SERVICE_CODE_STB_PROV, Constants.SERVICE_CODE_STB_PROV);
		simInfoList.put(Constants.SERVICE_CODE_EVDO_SIM, Constants.SERVICE_CODE_EVDO_SIM);

		invoiceGroups.put("0", " ");
		invoiceGroups.put("1", "1");
		invoiceGroups.put("2", "2");
		invoiceGroups.put("3", "3");
		invoiceGroups.put("4", "4");
		invoiceGroups.put("5", "5");
		invoiceGroups.put("6", "6");
		invoiceGroups.put("7", "7");
		invoiceGroups.put("8", "8");
		invoiceGroups.put("9", "9");
		invoiceGroups.put("10", "10");

		thrTypes.put("1", "Mono");
		thrTypes.put("2", "Mix");
		thrTypes.put("3", "Limited");
		thrTypes.put("4", "VIP");

		corpPartyBillCftTypes.put("1", TicketMaster.constants.complete());

		corpPartyBillCftTypes1.put("0", "");
		corpPartyBillCftTypes1.put("1", TicketMaster.constants.both1());
		corpPartyBillCftTypes1.put("2", TicketMaster.constants.private_());
		corpPartyBillCftTypes1.put("3", TicketMaster.constants.corporate_short());

		bundlePriceChargeType.put("1", TicketMaster.constants.whole());
		bundlePriceChargeType.put("2", TicketMaster.constants.by_days());
	}

	public LinkedHashMap<String, String> getLanguages() {
		return languages;
	}

	public LinkedHashMap<String, String> getServiceParams() {
		return serviceParams;
	}

	public void setServiceParams(LinkedHashMap<String, String> serviceParams) {
		this.serviceParams = serviceParams;
	}

	public LinkedHashMap<String, String> getIdentifiers() {
		return identifiers;
	}

	public void setIdentifiers(LinkedHashMap<String, String> identifiers) {
		this.identifiers = identifiers;
	}

	public LinkedHashMap<String, String> getIdentifiersContract() {
		return identifiersContract;
	}

	public void setIdentifiersContract(LinkedHashMap<String, String> identifiersContract) {
		this.identifiersContract = identifiersContract;
	}

	public LinkedHashMap<String, String> getIdentifiersParty() {
		return identifiersParty;
	}

	public void setIdentifiersParty(LinkedHashMap<String, String> identifiersParty) {
		this.identifiersParty = identifiersParty;
	}

	public LinkedHashMap<String, String> getDocumentTypes() {
		return documentTypes;
	}

	public void setDocumentTypes(LinkedHashMap<String, String> documentTypes) {
		this.documentTypes = documentTypes;
	}

	public LinkedHashMap<String, String> getOrgTypes() {
		return orgTypes;
	}

	public void setOrgTypes(LinkedHashMap<String, String> orgTypes) {
		this.orgTypes = orgTypes;
	}

	public LinkedHashMap<String, String> getSearchTypes() {
		return searchTypes;
	}

	public void setSearchTypes(LinkedHashMap<String, String> searchTypes) {
		this.searchTypes = searchTypes;
	}

	public void setLanguages(LinkedHashMap<String, String> languages) {
		this.languages = languages;
	}

	public LinkedHashMap<String, String> getPriceTypes() {
		return priceTypes;
	}

	public LinkedHashMap<String, String> getPrevOperators() {
		return prevOperators;
	}

	public LinkedHashMap<String, String> getGenders() {
		return genders;
	}

	public LinkedHashMap<String, String> getAccTransfTypes() {
		return accTransfTypes;
	}

	public LinkedHashMap<String, String> getBundleTypes() {
		return bundleTypes;
	}

	public LinkedHashMap<String, String> getSubscriberOwners() {
		return subscriberOwners;
	}

	public LinkedHashMap<String, String> getLimExcAmmTransfTypes() {
		return limExcAmmTransfTypes;
	}

	public LinkedHashMap<String, String> getActionTypes() {
		return actionTypes;
	}

	public LinkedHashMap<String, String> getInvoiceGroups() {
		return invoiceGroups;
	}

	public LinkedHashMap<String, String> getCorpPartyBillCftTypes() {
		return corpPartyBillCftTypes;
	}

	public LinkedHashMap<String, String> getCorpPartyBillCftTypes1() {
		return corpPartyBillCftTypes1;
	}

	public LinkedHashMap<String, String> getServTransfTypes() {
		return servTransfTypes;
	}

	public LinkedHashMap<String, String> getBundleTypesBB() {
		return bundleTypesBB;
	}

	public LinkedHashMap<Integer, String> getMonths(Long languageId) {
		if (languageId.equals(1L)) {
			return monthsEN;
		} else {
			return monthsGE;
		}
	}

	public LinkedHashMap<String, String> getThrTypes() {
		return thrTypes;
	}

	public LinkedHashMap<String, String> getBundlePriceChargeType() {
		return bundlePriceChargeType;
	}

	public boolean isSimInfoSubs(Long serviceId) {
		try {
			return simInfoList.containsKey(serviceId);
		} catch (Exception e) {
			e.printStackTrace();
			SC.say(e.toString());
			return false;
		}
	}

	@SuppressWarnings("rawtypes")
	public static Boolean convertBoolean(Map mp, String name) {
		Object obj = mp.get(name);
		if (obj == null)
			return null;
		if (obj instanceof Boolean)
			return (Boolean) obj;
		if (obj instanceof Number)
			return ((Number) obj).longValue() == 1;

		return obj.toString().trim().equals("1") || obj.toString().trim().toLowerCase().equals("true");

	}

	@SuppressWarnings("rawtypes")
	public static Number convertNumber(Map mp, String name) {
		Object obj = mp.get(name);
		return getNumberValue(obj);
	}

	@SuppressWarnings("rawtypes")
	public static String convertString(Map mp, String name) {
		Object ret = mp.get(name);
		if (ret != null)
			return ret.toString();
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static Long convertLong(Map mp, String name) {
		Number ret = convertNumber(mp, name);
		if (ret != null)
			return ret.longValue();
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static Double convertDouble(Map mp, String name) {
		Number ret = convertNumber(mp, name);
		if (ret != null)
			return ret.doubleValue();
		return null;
	}

	public static Number getNumberValue(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof Number)
			return (Number) obj;
		try {
			return new Double(obj.toString().trim());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;

	}

	public static Long getLongValue(Object obj) {
		Number ret = getNumberValue(obj);
		if (ret != null)
			return ret.longValue();
		return null;
	}
}
