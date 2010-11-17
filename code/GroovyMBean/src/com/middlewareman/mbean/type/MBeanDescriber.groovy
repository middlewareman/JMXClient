package com.middlewareman.mbean.type

import groovy.util.IndentPrinter;

import com.middlewareman.mbean.MBean 
import javax.management.MBeanInfo 
import javax.management.openmbean.*

class MBeanDescriber {
	
	void describe(Object object, IndentPrinter ip = new IndentPrinter()) {
		if (object == null) {
			ip.printIndent()
			ip.println 'null'
		} else if (object instanceof MBean) {
			MBean bean = (MBean) object
			MBeanInfo info = bean.@home.getInfo(bean.@objectName) 
			for (attr in info.attributes) {
				def child = root.appendNode(attr.name, [type:attr.type])
				def value = bean."$attr.name"
				// TODO
			}
		} else if (object instanceof CompositeData) {
			CompositeData cd = object
			ip.printIndent()
			ip.println "{ // CompositeData"
			ip.incrementIndent()
			for (key in cd.getCompositeType().keySet()) {
				ip.printIndent()
				ip.print "$key = "
				describe(cd.get(key), ip)
			}
			ip.decrementIndent()
			ip.printIndent()
			ip.println "}"
		} else if (object instanceof TabularData) {
			TabularData td = object
			ip.printIndent()
			ip.print "{ // TabularData"
			ip.incrementIndent()
			for (row in td.keySet()) {
				ip.printIndent()
				ip.println "$row: "
				describe(td[row as Object[]],ip)
				//describe(td.get(row as Object[]),ip)
			}
			ip.decrementIndent()
			ip.printIndent()
			ip.println "}"
		} else {
			ip.printIndent()
			ip.println "(simple) $object"
		}
		ip.flush()
	}
	
}
