/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

public class HomeException extends RuntimeException {

	final Object url;

	public HomeException(Object url, String message) {
		super(message);
		this.url = url;
	}

	public HomeException(Object url, String message, Throwable cause) {
		super(message, cause);
		this.url = url;
	}

}
