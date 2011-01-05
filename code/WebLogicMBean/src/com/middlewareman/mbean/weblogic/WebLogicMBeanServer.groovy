/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.MBeanHome

class WebLogicMBeanServer {

	final MBeanHome home

	WebLogicMBeanServer(MBeanHome home) {
		this.home = home
	}

	boolean asBoolean() {
		home
	}
}
