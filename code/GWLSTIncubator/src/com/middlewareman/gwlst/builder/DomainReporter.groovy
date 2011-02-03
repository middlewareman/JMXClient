/* $Id$ */
package com.middlewareman.gwlst.builder

import javax.management.MBeanAttributeInfo

import com.middlewareman.groovy.util.IndentPrintWriter
import com.middlewareman.mbean.MBean
import com.middlewareman.mbean.info.MBeanInfoCategory
import com.middlewareman.mbean.util.MBeanProperties
import com.middlewareman.mbean.weblogic.info.WebLogicAttributeFilter

class DomainReporter {

	static getNoDefaults() {
		def dr = new DomainReporter()
		dr.attrFilter.defaultValue = false
		return dr
	}

	WebLogicAttributeFilter keyFilter = new WebLogicAttributeFilter(key:true)
	WebLogicAttributeFilter attrFilter = new WebLogicAttributeFilter(key:false,mbeans:false)
	WebLogicAttributeFilter refFilter = new WebLogicAttributeFilter(reference:true)
	WebLogicAttributeFilter childFilter = new WebLogicAttributeFilter(child:true)

	def describe(MBean parent, MBeanAttributeInfo ai) {
		def result = []
		use(MBeanInfoCategory) {
			if (ai.isDeprecated()) result << 'deprecated'
			if (ai.hasDefaultValue()) result << "default=$ai.defaultValue"
		}
		boolean isSet = parent.isSet(ai.name)
		result << "isSet=$isSet"
		return result.join(', ')
	}

	void report(String name, MBean mbean, IndentPrintWriter ipw) {
		def ais = mbean.info.attributes
		def aimap = new HashMap(ais.size())
		ais.each { aimap.put it.name, it }

		def keys = MBeanProperties.get(mbean.@home, mbean.@objectName, keyFilter)
		//ipw.println "Key: $keys"
		ipw.indent("$name($keys) {", '}') {
			def attrs = MBeanProperties.get(mbean.@home, mbean.@objectName, attrFilter)
			attrs.each { key, value ->
				def desc = describe(mbean, aimap[key])
				ipw.println "$key = $value \t\t// attr, $desc"
			}
			def refs = MBeanProperties.get(mbean.@home, mbean.@objectName, refFilter)
			refs.each { key, value ->
				def desc = describe(mbean, aimap[key])
				ipw.println "$key = $value \t\t// ref, $desc"
			}
			def children = MBeanProperties.get(mbean.@home, mbean.@objectName, childFilter)
			children.each { key, value ->
				switch(value) {
					case null:
						ipw.println "child\t$key = null"
						break
					case MBean[]:
					case List:
					case MBean:
						report name, value, ipw
						break
					default:
						assert false, "$name, $key, $value"
				}
			}
		}
	}

	void report(String name, MBean[] mbeans, IndentPrintWriter ipw) {
		for (mbean in mbeans) report name, mbean, ipw
	}
}
