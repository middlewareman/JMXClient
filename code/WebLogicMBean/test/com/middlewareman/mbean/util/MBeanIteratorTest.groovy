/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.util;

import com.middlewareman.mbean.type.OnException 
import com.middlewareman.mbean.type.SimpleAttributeFilter 
import com.middlewareman.mbean.weblogic.DomainRuntimeServer 
import com.middlewareman.mbean.weblogic.RuntimeServer 
import com.middlewareman.mbean.weblogic.WebLogicMBeanHomeFactory 

class MBeanIteratorTest extends GroovyTestCase {
	
	def filterBulk = new SimpleAttributeFilter(
	onlyReadable:true, bulk:true)
	
	def filterSingle = new SimpleAttributeFilter(
	onlyReadable:true, bulk:false, onException:OnException.NULL)
	
	RuntimeServer getRuntimeServer() { 
		new RuntimeServer(WebLogicMBeanHomeFactory.default)
	}
	
	DomainRuntimeServer getDomainRuntimeServer() {
		new DomainRuntimeServer(WebLogicMBeanHomeFactory.default)
	}
	
	void testDefaultVerbose() {
		def rs = runtimeServer.runtimeService
		int counter = 0;
		for (mbean in new MBeanIterator(rs)) {
			++counter
		} 
		println counter
	}
	
	void clock(mbean, attributeFilter = SimpleAttributeFilter.brief) {
		assert mbean
		println name
		long start = System.currentTimeMillis()
		def iter = new MBeanIterator(mbean)
		def n = iter.size()
		long stop = System.currentTimeMillis()
		println "  first  $n in ${stop-start} ms"
		
		start = System.currentTimeMillis()
		iter = new MBeanIterator(mbean)
		n = iter.size()
		stop = System.currentTimeMillis()
		println "  second $n in ${stop-start} ms"
	}
	
	void testRuntimeDefault() {
		def rs = runtimeServer.runtimeService
		clock rs
	}
	
	void testDomainRuntimeDefault() {
		def drs = domainRuntimeServer.domainRuntimeService
		clock drs
	}
	
	void testDomainRuntimeBulk() {
		def drs = domainRuntimeServer.domainRuntimeService
		clock drs, filterBulk
	}
	
	void testDomainRuntimeSingle() {
		def drs = domainRuntimeServer.domainRuntimeService
		clock drs, filterSingle
	}
	
	void testCacheMBeansOnly() {
		def rs = runtimeServer.runtimeService
		rs.@home.enableMBeanCache()
		rs.@home.disableMBeanInfoCache()
		clock rs
	}
	
	void testCacheMBeanInfosForeverOnly() {
		def rs = runtimeServer.runtimeService
		rs.@home.disableMBeanCache()
		rs.@home.enableMBeanInfoCache null
		clock rs
	}
	
	void testCacheMBeanInfosNeverOnly() {
		def rs = runtimeServer.runtimeService
		rs.@home.disableMBeanCache()
		rs.@home.enableMBeanInfoCache 0
		clock rs
	}
	
	void testCacheAll() {
		def rs = runtimeServer.runtimeService
		rs.@home.enableMBeanCache()
		rs.@home.enableMBeanInfoCache null
		clock rs
	}
}
