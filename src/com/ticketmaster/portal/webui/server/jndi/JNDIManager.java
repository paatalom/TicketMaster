package com.ticketmaster.portal.webui.server.jndi;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import com.magti.billing.ejb.beans.fascade.baladjustment.BalAdjustFacade;
import com.magti.billing.ejb.beans.fascade.baladjustment.BalAdjustFacadeBean;
import com.magti.billing.ejb.beans.fascade.ccare.customer.CustomerFasade;
import com.magti.billing.ejb.beans.fascade.ccare.customer.CustomerFasadeBean;
import com.magti.billing.ejb.beans.fascade.ccare.gentran.TransactionFasade;
import com.magti.billing.ejb.beans.fascade.ccare.gentran.TransactionFasadeBean;
import com.magti.billing.ejb.beans.fascade.ccare.gentran.TransactionFascadeBeanNew;
import com.magti.billing.ejb.beans.fascade.ccare.gentran.TransactionFascadeNew;
import com.magti.billing.ejb.beans.fascade.corporate.ContractFasade;
import com.magti.billing.ejb.beans.fascade.corporate.ContractFasadeBean;
import com.magti.billing.ejb.beans.fascade.corporate.CorpContractFascade;
import com.magti.billing.ejb.beans.fascade.corporate.CorpContractFascadeBean;
import com.magti.billing.ejb.beans.fascade.numportability.NumberPortabilityFascade;
import com.magti.billing.ejb.beans.fascade.numportability.NumberPortabilityFascadeBean;
import com.magti.billing.ejb.beans.fascade.payment.PaymentFasade;
import com.magti.billing.ejb.beans.fascade.payment.PaymentFasadeBean;
import com.magti.billing.ejb.beans.fascade.service.CommonService;
import com.magti.billing.ejb.beans.fascade.service.CommonServiceBean;
import com.magti.billing.ejb.beans.fascade.service.gentran.GenTranService;
import com.magti.billing.ejb.beans.fascade.service.gentran.GenTranServiceBean;
import com.magti.billing.ejb.beans.fascade.service.manint.JBossManIntClient;
import com.magti.billing.ejb.beans.fascade.service.manint.JBossManIntClientBean;
import com.magti.billing.ejb.beans.fascade.service.srvprov.common.SrvProvCommonService;
import com.magti.billing.ejb.beans.fascade.service.srvprov.common.SrvProvCommonServiceBean;
import com.magti.billing.ejb.beans.fascade.service.srvprov.stb.StbProvisioning;
import com.magti.billing.ejb.beans.fascade.service.srvprov.stb.StbProvisioningBean;
import com.magti.billing.ejb.beans.fascade.service.ws.portal.PortalUserManagement;
import com.magti.billing.ejb.beans.fascade.service.ws.portal.PortalUserManagementRemote;
import com.magti.billing.ejb.beans.fascade.srvprov.CommonFasade;
import com.magti.billing.ejb.beans.fascade.srvprov.CommonFasadeBean;
import com.magti.billing.ejb.beans.fascade.srvprov.STLSubscriberFasade;
import com.magti.billing.ejb.beans.fascade.srvprov.STLSubscriberFasadeBean;
import com.magti.billing.ejb.beans.fascade.srvprov.ServiceProvisioningSTL;
import com.magti.billing.ejb.beans.fascade.srvprov.ServiceProvisioningSTLBean;
import com.magti.billing.ejb.beans.fascade.srvprov.stl.unifiedservs.UnifiedServiceFascade;
import com.magti.billing.ejb.beans.fascade.srvprov.stl.unifiedservs.UnifiedServiceFascadeBean;
import com.magti.billing.ejb.beans.fascade.umanager.UserManager;
import com.magti.billing.ejb.beans.fascade.umanager.UserManagerBean;
import com.magti.billing.ejb.beans.fascade.unlimplus.UnlimitedPlusFacade;
import com.magti.billing.ejb.beans.fascade.unlimplus.UnlimitedPlusFacadeBean;
import com.magti.billing.ejb.exception.CCareEJBException;

public class JNDIManager {

	private final Logger logger = Logger.getLogger(JNDIManager.class);
	private InitialContext ctx;
	private static JNDIManager instance;
	private String moduleName = "CCareEJB";
	private String appName = "";
	private String distinctName = "";

	public static JNDIManager getInstance() throws CCareEJBException {
		if (instance == null) {
			instance = new JNDIManager();
		}
		return instance;
	}

	public JNDIManager() throws CCareEJBException {
		try {
			Hashtable<String, Object> jndiProps = new Hashtable<String, Object>();
			jndiProps.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
			jndiProps.put("jboss.naming.client.ejb.context", true);
			Properties p = new Properties();
			p.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("jboss-ejb-client.properties"));
			p.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
			p.put("jboss.naming.client.ejb.context", true);
			System.out.println(p);
			ctx = new InitialContext(p);
		} catch (Exception e) {
			logger.error("Error while loading jndi configuration file 1 ", e);
			throw new CCareEJBException("Error while loading jndi configuration file", e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getLookUp(Class<T> intrf_class, Class<?> impl_class) throws CCareEJBException {
		try {
			String viewClassName = intrf_class.getName();
			String beanName = impl_class.getSimpleName();
			String name = "ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!"
					+ viewClassName;
			System.out.println("name = " + name);
			T obj = (T) ctx.lookup(name);
			return obj;
		} catch (Exception e) {
			logger.error("Error while looking up Common Fascade ", e);
			throw new CCareEJBException(e);
		}
	}

	public CorpContractFascade getCorpContractFascade() throws CCareEJBException {
		return getLookUp(CorpContractFascade.class, CorpContractFascadeBean.class);
	}

	public ContractFasade getContractFascade() throws CCareEJBException {
		return getLookUp(ContractFasade.class, ContractFasadeBean.class);
	}

	public CommonFasade getCommonFasade() throws CCareEJBException {
		return getLookUp(CommonFasade.class, CommonFasadeBean.class);
	}

	public CustomerFasade getCustomerFasade() throws CCareEJBException {
		return getLookUp(CustomerFasade.class, CustomerFasadeBean.class);
	}

	public BalAdjustFacade getBalAdjustFacade() throws CCareEJBException {
		return getLookUp(BalAdjustFacade.class, BalAdjustFacadeBean.class);
	}

	public PaymentFasade getPaymentFasade() throws CCareEJBException {
		return getLookUp(PaymentFasade.class, PaymentFasadeBean.class);
	}

	public StbProvisioning getStbProvisioning() throws CCareEJBException {
		return getLookUp(StbProvisioning.class, StbProvisioningBean.class);
	}

	public TransactionFasade getTransactionFasade() throws CCareEJBException {
		return getLookUp(TransactionFasade.class, TransactionFasadeBean.class);
	}

	public STLSubscriberFasade getSTLSubscriberFasade() throws CCareEJBException {
		return getLookUp(STLSubscriberFasade.class, STLSubscriberFasadeBean.class);
	}

	public NumberPortabilityFascade getNumberPortabilityFascade() throws CCareEJBException {
		return getLookUp(NumberPortabilityFascade.class, NumberPortabilityFascadeBean.class);
	}

	public ServiceProvisioningSTL getServiceProvisioningSTL() throws CCareEJBException {
		return getLookUp(ServiceProvisioningSTL.class, ServiceProvisioningSTLBean.class);
	}

	public UnifiedServiceFascade getUnifiedServiceFascade() throws CCareEJBException {
		return getLookUp(UnifiedServiceFascade.class, UnifiedServiceFascadeBean.class);
	}

	public JBossManIntClient getJBossManIntClient() throws CCareEJBException {
		return getLookUp(JBossManIntClient.class, JBossManIntClientBean.class);
	}

	public TransactionFascadeNew getTransactionFascadeNew() throws CCareEJBException {
		return getLookUp(TransactionFascadeNew.class, TransactionFascadeBeanNew.class);
	}

	public GenTranService getGenTranService() throws CCareEJBException {
		return getLookUp(GenTranService.class, GenTranServiceBean.class);
	}

	public SrvProvCommonService getSrvProvCommonService() throws CCareEJBException {
		return getLookUp(SrvProvCommonService.class, SrvProvCommonServiceBean.class);
	}

	public UnlimitedPlusFacade getUnlimitedPlusFacade() throws CCareEJBException {
		return getLookUp(UnlimitedPlusFacade.class, UnlimitedPlusFacadeBean.class);
	}

	public PortalUserManagementRemote getPortalUserManagement() throws CCareEJBException {
		return getLookUp(PortalUserManagementRemote.class, PortalUserManagement.class);
	}

	public CommonService getCommonService() throws CCareEJBException {
		return getLookUp(CommonService.class, CommonServiceBean.class);
	}

	public UserManager getUserManagerBean() throws CCareEJBException {
		return getLookUp(UserManager.class, UserManagerBean.class);
	}

	public static void main(String[] args) throws Exception {

	}
}
