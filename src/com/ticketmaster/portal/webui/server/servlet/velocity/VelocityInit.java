package com.ticketmaster.portal.webui.server.servlet.velocity;

import java.lang.reflect.Field;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class VelocityInit extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3413962735670967652L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			final VelocityEngine ve = com.isomorphic.velocity.Velocity.getEngine();

			Field ri = ve.getClass().getDeclaredField("ri");
			ri.setAccessible(true);
			RuntimeInstance runtimeInstance = (RuntimeInstance) ri.get(ve);

			Field initializing = RuntimeInstance.class.getDeclaredField("initializing");
			initializing.setAccessible(true);
			initializing.set(runtimeInstance, false);

			Field initialized = RuntimeInstance.class.getDeclaredField("initialized");
			initialized.setAccessible(true);
			initialized.set(runtimeInstance, false);

			runtimeInstance.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			runtimeInstance.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			runtimeInstance.setProperty("input.encoding", "UTF-8");
			runtimeInstance.setProperty("output.encoding", "UTF-8");
			runtimeInstance.setProperty("class.resource.loader.cache", "true");

			runtimeInstance.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
