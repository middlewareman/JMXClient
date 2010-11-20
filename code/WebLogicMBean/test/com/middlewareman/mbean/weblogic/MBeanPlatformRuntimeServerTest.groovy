package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.platform.PlatformHomeTest

class MBeanPlatformRuntimeServerTest extends PlatformHomeTest {
	
	final ph
	
	MBeanPlatformRuntimeServerTest() {
		def rs = new RuntimeServer(WebLogicMBeanHomeFactory.default)
		ph = rs.getMBeanPlatformHome()
	}
}
