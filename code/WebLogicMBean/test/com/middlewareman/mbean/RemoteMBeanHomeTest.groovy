/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean 

import com.middlewareman.mbean.MBean 
import com.middlewareman.mbean.weblogic.RuntimeServer 
import com.middlewareman.mbean.weblogic.WebLogicMBeanHomeFactory 

class RemoteMBeanHomeTest extends GroovyTestCase {
	
	void testConnecting() {
		def hf = WebLogicMBeanHomeFactory.default
		hf.reconnect = true
		MBean rs = new RuntimeServer(hf).runtimeService
		println "$name home before ${rs.@home}"
		assert rs.@home
		assert rs
		println "$name home after ${rs.@home}"
		rs.@home.close()
		println "$name home closed ${rs.@home}"
		assert rs.@home
		assert rs
		println "$name home reopened ${rs.@home}"
	}
	
	void testConnected() {
		def hf = WebLogicMBeanHomeFactory.default
		hf.reconnect = false
		MBean rs = new RuntimeServer(hf).runtimeService
		println "$name home before ${rs.@home}"
		assert rs.@home
		assert rs
		println "$name home after ${rs.@home}"
		rs.@home.close()
		println "$name home closed ${rs.@home}"
		assert !rs.@home
		assert !rs
		println "$name home reopened ${rs.@home}"
	}
}
