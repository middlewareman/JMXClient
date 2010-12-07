/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.platform.PlatformHomeTest

class ProxyPlatformRuntimeServerTest extends PlatformHomeTest {
	
	final ph
	
	ProxyPlatformRuntimeServerTest() {
		def rs = new RuntimeServer(WebLogicMBeanHomeFactory.default)
		ph = rs.getProxyPlatformHome()
	}
}
