/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.platform.PlatformHomeTest

class ProxyPlatformDomainRuntimeServerTest extends PlatformHomeTest {
	
	final ph
	
	ProxyPlatformDomainRuntimeServerTest() {
		def drs = new DomainRuntimeServer(WebLogicMBeanHomeFactory.default)
		ph = drs.getProxyPlatformHome('AdminServer')
	}
}
