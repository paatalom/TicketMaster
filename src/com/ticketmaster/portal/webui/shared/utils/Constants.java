package com.ticketmaster.portal.webui.shared.utils;

public class Constants {
	
	public static final String CREDENTIALS_URL_AUTH   = "/TicketMaster/j_spring_security_check";
	public static final String CREDENTIALS_URL_LOGOUT = "/TicketMaster/j_spring_security_logout";
	public static final String TIMESTEN_DB_URL_LIVE = "http://192.9.200.232:8063/1/get_camel_balance.jsp?Subs=%URL%";
	public static final String TIMESTEN_DB_URL_DEV = "http://192.168.9.151:9797/1/get_camel_balance.jsp?Subs=%URL%";
	public static final String DB_HOSTNAME_PREFIX_LIVE = "STR";

	public static final Integer PROGRAM_BASIC_WIDTH = 1245;

	// GTT Attribute Types
	public static final Integer ATTR_TYPE_STRING = new Integer(1);
	public static final Integer ATTR_TYPE_DESCRIPTION = new Integer(2);
	public static final Integer ATTR_TYPE_SERVICE_PARAMS = new Integer(4);

	public static final String USER_CONTEXT_PARAM_NAME = "user_context";

	// Bundle Item Types
	public static final Integer BUNDLE_ITEM_TYPE_BONUS = new Integer(1);
	public static final Integer BUNDLE_ITEM_TYPE_DISCOUNT = new Integer(2);
	public static final Integer BUNDLE_ITEM_TYPE_MEM_GROUP = new Integer(3);
	public static final Integer BUNDLE_ITEM_TYPE_GROUP_DISC = new Integer(4);
	public static final Integer BUNDLE_ITEM_TYPE_SRV_CHRG_SUBS_FEE = new Integer(5);
	public static final Integer BUNDLE_ITEM_TYPE_USSD_BUNDLE = new Integer(6);
	public static final Integer BUNDLE_ITEM_TYPE_EXCEPTIONS = new Integer(7);

	public static final Integer SUBSCRIBER_TYPE_ID_CORP_FIXED = new Integer(40);
	public static final Integer SUBSCRIBER_TYPE_ID_E1 = new Integer(42);

	public static final Integer LIST_BATCH_SIZE = new Integer(300);

	public static final Integer MULTIPLICITY_CODE_CORP_FIXED = new Integer(170);

	public static final Integer GTT_VALUE_TYPE_FIXED = new Integer(1);

	public static final Integer GTT_VALUE_TYPE_SETBY_CLIENT = new Integer(2);

	// Contract Type IDs
	public static final int CONTRACT_TYPE_MAGTI_MONO_PLUS_ID = 1;
	public static final int CONTRACT_TYPE_BALI_ID = 2;
	public static final int CONTRACT_TYPE_CDMA_EVDO_ID = 12;
	public static final int CONTRACT_GGN_ID = 5;
	public static final int CONTRACT_CDMA_EVDO_ID = 12;
	public static final int CONTRACT_TYPE_MAGTI_FIX_ID = 6;
	public static final int CONTRACT_TYPE_MAGTI_CDMA_ID = 7;
	public static final int CONTRACT_TYPE_POST_PAY_MAGTI_ID = 15;
	public static final int CONTRACT_TYPE_MAGTI_FIX_POST_PAID_ID = 32;
	public static final int CONTRACT_TYPE_CORPORATE_CONTRACT_ID = 8;
	public static final int CONTRACT_TYPE_BANI_ID = 14;
	public static final int CONTRACT_TYPE_POST_PAID_ID = 30;
	public static final int CONTRACT_TYPE_MAGTISAT_ID = 31;

	public static final String REPORT_FIELDS = "___fields";
	public static final String REPORT_CAPTIONS = "___captions";
	public static final String REPORT_HEADERS = "___headers";
	public static final String REPORT_DS = "___ds_name";
	public static final String REPORT_OPERATION = "___ds_operation";

	public static final Long SUBSCRIBER_TYPE_MOBILE = 41L;

	public static final Long END_DATE_2050 = 2524593600000L;

	public static Long SERVICE_CODE_MOBILE = 1001L;
	public static Long SERVICE_CODE_BANI_MOBILE = 6001L;
	public static Long SERVICE_CODE_CDMA_MOBILE = 2001L;
	public static Long SERVICE_CODE_STB_PROV = 8101L;
	public static Long SERVICE_CODE_EVDO_SIM = 4000L;

	public static final int PART_INFO_ADV_ITEM_PHONE_QUANT_MGT = 19;
	public static final int PART_INFO_ADV_ITEM_PHONE_QUANT_BALI = 21;
	public static final int PART_INFO_ADV_ITEM_PHONE_QUANT_BANI = 252;
	public static final int PART_INFO_ADV_ITEM_PHONE_QUANT_GEOCELL = 23;
	public static final int PART_INFO_ADV_ITEM_PHONE_QUANT_LAILAI = 25;
	public static final int PART_INFO_ADV_ITEM_PHONE_QUANT_BEELINE = 27;
	public static final int PART_INFO_ADV_ITEM_PHONE_QUANT_SILKNET = 250;

	public static final int PART_INFO_ADV_ITEM_GENDER = 173;
	public static final int PART_INFO_ADV_ITEM_BIRTH_DATE = 174;
	public static final int PART_INFO_ADV_ITEM_EMPLOYMENT = 175;
	public static final int PART_INFO_ADV_ITEM_ACTIVITY = 185;
	public static final int PART_INFO_ADV_ITEM_POSITION = 202;
	public static final int PART_INFO_ADV_ITEM_OTH_SIM_CARD = 208;
	public static final int PART_INFO_ADV_ITEM_PRIOR_SRV_TYPE = 216;
	public static final int PART_INFO_ADV_ITEM_OUR_COMP_OTH_SRV = 221;
	public static final int PART_INFO_ADV_ITEM_OTHER_FIXED_PHONE_COMP = 228;
	public static final int PART_INFO_ADV_ITEM_OTHER_TV_PROVIDES = 234;
	public static final int PART_INFO_ADV_ITEM_TV_CHANELS = 241;

	public static final Long GTT_ROAM_ADD_GPRS = 1646L;

	public static final Long GTT_ROAM_DEL_GPRS = 1550L;
	public static final Long GTT_ROAM_DEL_VOICE_GPRS = 1548L;

}
