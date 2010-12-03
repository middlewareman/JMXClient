package com.middlewareman.mbean.weblogic.builder

import com.middlewareman.mbean.MBean 
import com.middlewareman.mbean.weblogic.RuntimeServer 
import com.middlewareman.mbean.weblogic.WebLogicMBeanHomeFactory 
import javax.management.ObjectName 

class HtmlExporterTest extends GroovyTestCase {
	
	RuntimeServer rs = new RuntimeServer(WebLogicMBeanHomeFactory.default)
	
	void testDomainMBean() {
		def domain = rs.runtimeService.DomainConfiguration
		assert domain?.Type == "Domain"
		def htmle = new HtmlExporter()
		htmle.mbean domain
	}
	
	void testDomainMBeanInfo() {
		MBean domain = rs.runtimeService.DomainConfiguration
		assert domain?.Type == "Domain"
		def htmle = new HtmlExporter()
		htmle.mbean domain.info
	}
	
	void testServerRuntimeRecursive() {
		def sr = rs.runtimeService.ServerRuntime
		def htmle = new HtmlExporter()
		recurse(sr,htmle,new LinkedHashSet<ObjectName>())
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
}
