package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.platform.PlatformHomeTest
import com.middlewareman.mbean.platform.ProxyPlatformHome 

class ProxyPlatformRuntimeServerTest extends PlatformHomeTest {
	
	final ph
	
	ProxyPlatformRuntimeServerTest() {
		def hf = new DefaultMBeanHomeFactory(
				url:'t3://localhost:7001',username:'weblogic',password:'welcome1')
		def rs = new RuntimeServer(hf)
		ph = rs.getProxyPlatformHome()
	}
}
