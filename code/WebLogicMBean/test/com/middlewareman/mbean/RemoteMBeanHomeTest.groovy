/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean 

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
	
	void testConnectedSerial() {
		def hf = WebLogicMBeanHomeFactory.default
		hf.reconnect = false
		def rs = new RuntimeServer(hf).runtimeService
		def home1 = rs.@home
		assert home1
		assert home1 instanceof MBeanHome
		def con1 = home1.connection
		def baos = new ByteArrayOutputStream()
		try {
			baos.withObjectOutputStream { it.writeObject home1 }
		} catch(Exception e) {
			return
		}
		assert false
	}
	
	void testConnectingSerial() {
		def hf = WebLogicMBeanHomeFactory.default
		hf.reconnect = true
		def rs = new RuntimeServer(hf).runtimeService
		def home1 = rs.@home
		assert home1
		assert home1 instanceof MBeanHome
		def con1 = home1.connection
		println "$name writing $home1"
		def baos = new ByteArrayOutputStream()
		baos.withObjectOutputStream { it.writeObject home1 }
		def data1 = baos.toByteArray()
		println "$name serialized size $data1.length"
		baos.reset()
		rs.domainConfiguration.servers.name // Just do something that triggers caching
		baos.withObjectOutputStream { it.writeObject rs.@home }
		def data2 = baos.toByteArray()
		println "$name serialized size $data2.length"
		assert data1 == data2
		def home2 = new ByteArrayInputStream(data1).withObjectInputStream { it.readObject() }
		assert home2 instanceof MBeanHome
		assert home2 == home1
		def con2 = home2.connection
		assert ! (con1.is(con2))
		println "$name read $home2"
	}
}
