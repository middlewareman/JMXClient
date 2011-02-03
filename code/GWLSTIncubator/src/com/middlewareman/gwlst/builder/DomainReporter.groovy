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

	static getIsSetOnly() {
		def dr = new DomainReporter()
		dr.attrFilter.isSet = true
		return dr
	}

	static getNoDefaultsAndIsSetOnly() {
		def dr = new DomainReporter()
		dr.attrFilter.defaultValue = false
		dr.attrFilter.isSet = true
		return dr
	}

	WebLogicAttributeFilter keyFilter = new WebLogicAttributeFilter(deprecated:false,key:true)
	WebLogicAttributeFilter attrFilter = new WebLogicAttributeFilter(deprecated:false,key:false,mbeans:false)
	WebLogicAttributeFilter refFilter = new WebLogicAttributeFilter(deprecated:false,reference:true)
	WebLogicAttributeFilter childFilter = new WebLogicAttributeFilter(deprecated:false,child:true)

	def describe(MBean parent, MBeanAttributeInfo ai, isSet = null) {
		def result = []
		use(MBeanInfoCategory) {
			if (ai.isDeprecated()) result << 'deprecated'
			if (ai.hasDefaultValue()) result << "default=$ai.defaultValue"
		}
		if (isSet != null) result << "isSet=$isSet"
		return result.join(', ')
	}

	void report(String name, MBean mbean, IndentPrintWriter ipw) {
		def ais = mbean.info.attributes
		def aimap = new HashMap(ais.size())
		ais.each {
			aimap.put it.name, it
		}

		def keys = MBeanProperties.get(mbean.@home, mbean.@objectName, keyFilter)
		keys = keys.collect { key, value -> "$key:${value.inspect()}"}.join(',')
		ipw.indent("$name($keys) {", '}') {
			def attrs = MBeanProperties.get(mbean.@home, mbean.@objectName, attrFilter)
			attrs.each { key, value ->
				boolean isSet = mbean.isSet(key)
				if (attrFilter.acceptAttributeIsSet(isSet)) {
					def desc = describe(mbean, aimap[key], isSet)
					ipw.println "$key = ${value?.inspect()} \t\t/* $desc */"
				}
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
						report key, value, ipw
						break
					default:
						assert false, "$name, $key, $value"
				}
			}
			def refs = MBeanProperties.get(mbean.@home, mbean.@objectName, refFilter)
			refs.each { key, value ->
				def desc = describe(mbean, aimap[key])
				ipw.println "$key = $value \t\t/* $desc */"
			}
		}
	}

	void report(String name, MBean[] mbeans, IndentPrintWriter ipw) {
		for (mbean in mbeans) report name, mbean, ipw
	}
}
