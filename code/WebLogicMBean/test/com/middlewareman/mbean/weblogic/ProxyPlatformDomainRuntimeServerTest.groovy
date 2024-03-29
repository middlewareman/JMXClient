/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.platform.PlatformHomeTestAbstract 

class ProxyPlatformDomainRuntimeServerTest extends PlatformHomeTestAbstract {
	
	final ph
	
	ProxyPlatformDomainRuntimeServerTest() {
		def drs = new DomainRuntimeServer(WebLogicMBeanHomeFactory.default)
		def names = drs.domainRuntimeService.ServerRuntimes.Name
		ph = drs.getProxyPlatformHome(names[0])
	}
}
