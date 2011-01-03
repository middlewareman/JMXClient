/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import javax.management.ObjectName;

public class InstanceException extends HomeException {

	public final ObjectName objectName;

	InstanceException(Object url, ObjectName objectName, String message) {
		super(url, objectName.toString() + ": " + message);
		this.objectName = objectName;
	}

	InstanceException(Object url, ObjectName objectName, Throwable cause) {
		super(url, objectName.toString(), cause);
		this.objectName = objectName;
	}
}
