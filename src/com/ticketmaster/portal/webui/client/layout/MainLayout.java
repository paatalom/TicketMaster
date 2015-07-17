package com.ticketmaster.portal.webui.client.layout;

import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public class MainLayout extends VLayout {

	private boolean height100 = true;
	private Body body;

	public MainLayout() {
		setPadding(0);
		setMargin(0);
		setMembersMargin(5);
		setHeight100();
		setWidth100();
		addResizedHandler(new ResizedHandler() {

			@Override
			public void onResized(ResizedEvent event) {
				if (getHeight() < 750) {
					setHeight(800);
					height100 = false;
				} else if (!height100) {
					height100 = true;
					setHeight100();
				}
			}
		});
//		Footer footer = new Footer();
//		Header header = new Header();
//		body = new Body(header, footer);
//		addMembers(header, body, footer);
	}

	public Body getBody() {
		return body;
	}
}
