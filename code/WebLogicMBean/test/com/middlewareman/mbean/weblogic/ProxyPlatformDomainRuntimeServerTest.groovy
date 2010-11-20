package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.platform.PlatformHomeTest
import com.middlewareman.mbean.platform.ProxyPlatformHome 

class ProxyPlatformDomainRuntimeServerTest extends PlatformHomeTest {
	
	final ph
	
	ProxyPlatformDomainRuntimeServerTest() {
		def hf = new WebLogicMBeanHomeFactory(
				url:'t3://localhost:7001',username:'weblogic',password:'welcome1')
		def drs = new DomainRuntimeServer(hf)
		ph = drs.getProxyPlatformHome('AdminServer')
	}
}
