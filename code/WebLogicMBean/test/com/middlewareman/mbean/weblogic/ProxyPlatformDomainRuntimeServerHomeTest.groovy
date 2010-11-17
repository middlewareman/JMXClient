package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.platform.PlatformHomeTest
import com.middlewareman.mbean.platform.ProxyPlatformHome 

class ProxyPlatformDomainRuntimeServerHomeTest extends PlatformHomeTest {
	
	final ph
	
	ProxyPlatformDomainRuntimeServerHomeTest() {
		def hf = new DefaultMBeanHomeFactory(
				url:'t3://localhost:7001',username:'weblogic',password:'welcome1')
		def drs = new DomainRuntimeServerHome(hf)
		ph = drs.getProxyPlatformHome('AdminServer')
	}
}
