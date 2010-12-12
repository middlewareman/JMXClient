/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic.builder


import com.middlewareman.mbean.MBean 
import com.middlewareman.mbean.util.MBeanIterator 
import com.middlewareman.mbean.weblogic.DomainRuntimeServer 
import com.middlewareman.mbean.weblogic.EditServer 
import com.middlewareman.mbean.weblogic.RuntimeServer 
import com.middlewareman.mbean.weblogic.WebLogicMBeanHomeFactory 
import groovy.xml.MarkupBuilder 
import javax.management.ObjectName 

class HtmlExporterTest extends GroovyTestCase {
	
	RuntimeServer rs = new RuntimeServer(WebLogicMBeanHomeFactory.default)
	
	private File getNamedFile() {
		new File("${getClass().getSimpleName()}.${name}.html")
	}
	
	void testDomainMBean() {
		def domain = rs.runtimeService.DomainConfiguration
		assert domain?.Type == "Domain"
		namedFile.withPrintWriter { 
			def htmle = new HtmlExporter(it)
			htmle.mbean domain
		}
	}
	
	void testDomainMBeanInfo() {
		MBean domain = rs.runtimeService.DomainConfiguration
		assert domain?.Type == "Domain"
		namedFile.withPrintWriter {
			def htmle = new HtmlExporter(it)
			htmle.mbean domain.info
		}
	}
	
	void testServerRuntimeRecursive() {
		def sr = rs.runtimeService.ServerRuntime
		File file = namedFile
		long start = System.currentTimeMillis()
		file.withPrintWriter { 
			def htmle = new HtmlExporter(it)
			recurse(sr,htmle,new LinkedHashSet<ObjectName>())
		}
		long finish = System.currentTimeMillis()
		println "$name wrote ${file.length()} bytes to $file.name in ${finish - start} ms."
	}
	
	void recurse(MBean mbean, HtmlExporter he, Set<ObjectName> visited) {
		def on = mbean.@objectName
		if (on in visited) {
			return
		}
		visited.add mbean.@objectName
		he.mbean mbean
		def ais = mbean.info.attributes
		for (ai in ais) {
			if (ai.type.contains('javax.management.ObjectName')) {
				def child = mbean.getProperty(ai.name)
				if (child != null) {
					if (child instanceof MBean) {
						recurse child, he, visited
					} else if (child instanceof MBean[] || child instanceof Collection<MBean>) {
						for (mb in child) 
							recurse mb, he, visited
					} else {
						System.err.println "ignoring $child"
					}
				}
			}
		}
	}
	
	void maxSize(Iterator<MBean> iter) {
		CharArrayWriter out = new CharArrayWriter(64<<10)
		def markup = new MarkupBuilder(out)
		def html = new HtmlExporter(markup)
		def max = 0
		for (mbean in iter) {
			html.mbean mbean
			if (out.size() > max) max = out.size()
			out.reset()
			assert out.size() == 0
		}
		println "$name greatest size $max"
	}
	
	void testMaxSizeRuntime() {
		def runtimeServer = new RuntimeServer(WebLogicMBeanHomeFactory.default)
		def iter = new MBeanIterator(runtimeServer.runtimeService)
		maxSize iter
	}
	
	void testMaxSizeDomainRuntime() {
		def domainRuntimeServer = new DomainRuntimeServer(WebLogicMBeanHomeFactory.default)
		def iter = new MBeanIterator(domainRuntimeServer.domainRuntimeService)
		maxSize iter
	}
	
	void testMaxSizeEdit() {
		def editServer = new EditServer(WebLogicMBeanHomeFactory.default)
		def iter = new MBeanIterator(editServer.editService)
		maxSize iter
	}

}
