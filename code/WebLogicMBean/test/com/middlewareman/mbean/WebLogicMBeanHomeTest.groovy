/*
 * $Id$
 * Copyright © 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean

import com.middlewareman.mbean.type.SimpleAttributeFilter
import com.middlewareman.mbean.util.MBeanIterator
import com.middlewareman.mbean.util.MBeanProperties
import com.middlewareman.mbean.weblogic.*

class WebLogicMBeanHomeTest extends GroovyTestCase {

	RuntimeServer getRuntimeServer() {
		new RuntimeServer(WebLogicMBeanHomeFactory.default)
	}

	DomainRuntimeServer getDomainRuntimeServer() {
		new DomainRuntimeServer(WebLogicMBeanHomeFactory.default)
	}

	MBean getBean() {
		runtimeServer.runtimeService
	}

	void testProperties() {
		def bean1 = runtimeServer.runtimeService
		bean1.@home.listProperties = true
		def bean2 = runtimeServer.runtimeService
		bean2.@home.listProperties = true
		assert !(bean1.is(bean2))
		assert bean1 == bean2
		assert bean1?.hashCode() == bean2.hashCode()

		Map props1 = bean1.properties
		Map props1b = bean1.properties

		assert props1.keySet() == props1b.keySet()
		for (key in props1.keySet()) {
			assert props1[key] == props1b[key]
			def entry1 = props1.entrySet().find { it.key == key }
			assert entry1
			def entry1b = props1b.entrySet().find { it.key == key }
			assert entry1b
			assert entry1.key == entry1b.key
			assert entry1.key.hashCode() == entry1b.key.hashCode()
			assert entry1.value == entry1b.value
			compare entry1.value, entry1b.value
			assert entry1.equals(entry1b)
		}


		assert props1 == props1b

		def props2 = bean2.properties
		assert props1 == props2
	}

	boolean compare(one, two) {
		if (one == null) return (two == null)
		if (one instanceof MBean[] && two instanceof MBean[]) {
			assert one.size() == two.size()
			int siz = one.size()
			for (i in 0..<siz) {
				println "Comparing one[$i] and two[$i]"
				compare one[i], two[i]
			}
		}
		if (one == two) {
			if (one.hashCode() == two.hashCode()) return true
			else {
				println "EQUAL but hashCode differs!"
				println "$one (${one.hashCode()}) ${one.class.name}"
				println "$two (${two.hashCode()}) ${two.class.name}"
				return true
			}
		}
	}

	void testDomainRuntimeBulkCompareNobulk() {
		def drs1 = domainRuntimeServer.domainRuntimeService
		MBeanIterator it1 = new MBeanIterator(drs1)
		def drs2 = domainRuntimeServer.domainRuntimeService
		MBeanIterator it2 = new MBeanIterator(drs2)

		assert drs1 == drs2

		def filter = new SimpleAttributeFilter(noNullValue:true)	// TODO should not need noNullValue!

		while (it1.hasNext() && it2.hasNext()) {
			def mbean1 = it1.next()
			def mbean2 = it2.next()
			equals mbean1, mbean2
			if (mbean1 && mbean2) {
				assert mbean1 == mbean2
				Map props1 = MBeanProperties.getSingles(mbean1.@home, mbean1.@objectName, filter)
				//equals props1, props1
				Map props2 = MBeanProperties.getBulk(mbean2.@home, mbean2.@objectName, filter)
				//equals props2, props2
				//equals props1, props2
				Set keys1 = props1.keySet()
				Set keys2 = props2.keySet()
				Set left = keys1 - keys2
				if (left) {
					println "$name LEFT singles has more $mbean1"
					for (key in left) println "$key\t${props1[key]}"
				}
				Set right = keys2 - keys1
				if (right) {
					println "$name RIGHT bulk has more $mbean1"
					for (key in right) println "$key\t${props2[key]}"
				}
				// assert !left && !right  TODO Oracle bug for EmbeddedLDAP ?
			}
		}
		assert !it1.hasNext() && !it2.hasNext()
	}

	private void equals(Object obj1, Object obj2) {
		if (obj1 == null) assert obj2 == null
		else if (obj1 instanceof Map) {
			assert obj2 instanceof Map
			equalsMap obj1, obj2
		} else if (obj1 instanceof List) {
			assert obj2 instanceof List
			equalsList obj1, obj2
		} else if (obj1 instanceof Object[]) {
			assert obj2 instanceof Object[]
			equalsList obj1, obj2
		} else {
			if (obj1.equals(obj2)) return
				if (obj1.toString().equals(obj2.toString())) return
			//println "$name CANNOT GUARANTEE EQUALITY\n$obj1\n$obj2"
		}
	}

	private void equalsMap(Map map1, Map map2) {
		Set key1only = map1.keySet() - map2.keySet()
		if (key1only) {
			println "$name *** key1only $key1only"
			for (key in key1only) println "$key -> ${map1[key]}"
		}
		assert !key1only
		Set key2only = map2.keySet() - map1.keySet()
		if (key2only) {
			println "$name *** key2only $key2only"
			for (key in key2only) println "$key -> ${map2[key]}"
		}
		assert !key2only
		assert map1.size() == map2.size()
		assert map1.keySet() == map2.keySet()
		for (key in map1.keySet()) {
			def value1 = map1[key]
			def value2 = map2[key]
			if (value1 == null) assert value2 == null
			equals(value1, value2)
		}
	}

	private void equalsList(list1, list2) {
		assert list1.size() == list2.size()
		Iterator iter1 = list1.iterator()
		Iterator iter2 = list2.iterator()
		while (iter1.hasNext() && iter2.hasNext()) {
			def item1 = iter1.next()
			def item2 = iter2.next()
			equals item1, item2
		}
	}
}
