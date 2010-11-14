package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.platform.PlatformHomeTest
import com.middlewareman.mbean.platform.ProxyPlatformHome 

class ProxyPlatformRuntimeServerHomeTest extends PlatformHomeTest {
	
	final ph
	
	ProxyPlatformRuntimeServerHomeTest() {
		def hf = new DefaultMBeanHomeFactory(
				url:'t3://localhost:7001',username:'weblogic',password:'welcome1')
		def rs = new RuntimeServerHome(hf)
		def mbh = rs.getHome()
		ph = new ProxyPlatformHome(mbh)
	}
}
