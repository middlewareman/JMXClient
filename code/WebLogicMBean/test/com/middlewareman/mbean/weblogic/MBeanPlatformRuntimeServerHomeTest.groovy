package com.middlewareman.mbean.weblogic

import com.middlewareman.mbean.platform.MBeanPlatformHome 
import com.middlewareman.mbean.platform.PlatformHomeTest

class MBeanPlatformRuntimeServerHomeTest extends PlatformHomeTest {
	
	final ph
	
	MBeanPlatformRuntimeServerHomeTest() {
		def hf = new DefaultMBeanHomeFactory(
				url:'t3://localhost:7001',username:'weblogic',password:'welcome1')
		def rs = new RuntimeServerHome(hf)
		ph = rs.getMBeanPlatformHome()
	}
}
