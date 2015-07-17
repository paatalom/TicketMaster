package com.ticketmaster.portal.webui.server.utils;

import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.ticketmaster.portal.webui.shared.exception.PortalException;

public class RCNGenerator {
	private Logger logger = Logger.getLogger(RCNGenerator.class.getName());

	private static RCNGenerator instance;

	public static synchronized RCNGenerator getInstance() {
		if (instance == null) {
			instance = new RCNGenerator();
		}
		return instance;
	}

	public Long initRcn(EntityManager oracleManager, Timestamp date,
			String userName, String operation) throws PortalException {
		StringBuilder log = new StringBuilder("\nGenerating RCN.\n");
		try {
			long time = System.currentTimeMillis();
			Query query = oracleManager
					.createNativeQuery("select RandomGenerator.CREATE_RANDOM_NUMBER(?,?,?) from dual");
			query.setParameter(1, userName);
			query.setParameter(2, date);
			query.setParameter(3, operation);
			Long rcn = new Long(query.getSingleResult().toString());
			time = System.currentTimeMillis() - time;

			log.append("Data = ").append(date).append("\n");
			log.append("User = ").append(userName).append("\n");
			log.append("Operation = ").append(operation).append("\n");
			log.append("RCN Value = ").append(rcn).append("\n");
			log.append("RCN Generation Time = ").append(time).append("\n");
			logger.info(log.toString());
			return rcn;
		} catch (Exception e) {
			e.printStackTrace();
			throw new PortalException("შეცდომა ისტორიის შექმნისას : "
					+ e.toString());

		}
	}

	// public static void main(String[] args) throws Exception {
	// EMF.getEntityManager();
	// org.hibernate.dialect.Oracle10gDialect od = new Oracle10gDialect();
	// org.hibernate.dialect.Dialect d = od;
	// EntityManagerFactory emf = Persistence.createEntityManagerFactory("ds");
	// EntityManager em = emf.createEntityManager();
	// User u = em.find(User.class, 1L);
	// System.out.println(u);
	//
	// FileOutputStream fos = new FileOutputStream("classes.txt");
	//
	// File dir = new File("C:/Users/Koba/workspace/CCareFE/lib");
	// for (File dr : dir.listFiles()) {
	// for (File jar : dr.listFiles()) {
	// ZipFile zipFile = new ZipFile(jar);
	//
	// Enumeration zipEntries = zipFile.entries();
	//
	// while (zipEntries.hasMoreElements()) {
	// // Process the name, here we just print it out
	// String name = ((ZipEntry) zipEntries.nextElement()).getName();
	// if (!name.endsWith(".class"))
	// continue;
	// name = name.replaceAll("/", ".");
	// fos.write((name + "\t" + jar.getName() + "\n").getBytes("UTF8"));
	// }
	// }
	// fos.flush();
	// fos.close();
	// }
	// }
}
