package com.ticketmaster.portal.webui.shared.exception;

public class PortalException extends Exception {
	private static final long serialVersionUID = 1L;
	private String title;
	private String exception;
	private Throwable throwable;

	public PortalException() {
		super();
	}

	public PortalException(String exception) {
		super(exception);
		this.exception = exception;
	}

	public PortalException(String title, String exception) {
		super(exception);
		this.exception = exception;
		this.title = title;
	}

	public PortalException(String title, Throwable throwable) {
		super(title, throwable);
		this.title = title;
		this.throwable = throwable;
	}

	public String getException() {
		return exception;
	}

	public String getTitle() {
		return title;
	}

	public Throwable getThrowable() {
		return throwable;
	}
}
