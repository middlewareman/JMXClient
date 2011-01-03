/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.util

import com.middlewareman.mbean.LocalPlatformMBeanHome
import com.middlewareman.mbean.platform.MBeanPlatformHome
import com.middlewareman.mbean.type.SimpleAttributeFilter

class MBeanPropertiesTest extends GroovyTestCase {

	def filters = [
		SimpleAttributeFilter.native,
		SimpleAttributeFilter.brief,
		SimpleAttributeFilter.verbose,
		SimpleAttributeFilter.safe
	]

	def runtime = new MBeanPlatformHome(new LocalPlatformMBeanHome()).runtime


	void testProperties() {
		assert runtime.properties
	}

	void testGet() {
		for (filter in filters) {
			assert MBeanProperties.get(runtime.@home, runtime.@objectName, filter)
		}
	}

	void testGetSingles() {
		for (filter in filters) {
			assert MBeanProperties.getSingles(runtime.@home, runtime.@objectName, filter)
		}
	}

	void testGetBulk() {
		for (filter in filters) {
			assert MBeanProperties.getBulk(runtime.@home, runtime.@objectName, filter)
		}
	}

	void testCompareSinglesBulk() {
		for (filter in filters) {
			def singles = MBeanProperties.getSingles(runtime.@home, runtime.@objectName, filter)
			def bulk = MBeanProperties.getBulk(runtime.@home, runtime.@objectName, filter)
			assert singles.keySet() == bulk.keySet()
			for (key in singles.keySet()) {
				if (singles[key] != bulk[key])
					println "$name $key not equal\n\t${singles[key]}\n\t${bulk[key]}"
			}
		}
	}
}
