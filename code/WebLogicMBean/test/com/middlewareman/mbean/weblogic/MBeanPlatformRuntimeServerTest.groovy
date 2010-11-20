package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.platform.MBeanPlatformHome 
import com.middlewareman.mbean.platform.PlatformHomeTest

class MBeanPlatformRuntimeServerTest extends PlatformHomeTest {
	
	final ph
	
	MBeanPlatformRuntimeServerTest() {
		def hf = new WebLogicMBeanHomeFactory(
				url:'t3://localhost:7001',username:'weblogic',password:'welcome1')
		def rs = new RuntimeServer(hf)
		ph = rs.getMBeanPlatformHome()
	}
}
