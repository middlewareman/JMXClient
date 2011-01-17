/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic.info

import javax.management.MBeanAttributeInfo
import javax.management.MBeanFeatureInfo

class MBeanInfoCategory {

	static boolean isKey(MBeanAttributeInfo ai) {
		'com.bea.key' in ai.descriptor.fieldNames
	}

	static boolean isUnharvestable(MBeanAttributeInfo ai) {
		'com.bea.unharvestable' in ai.descriptor.fieldNames
	}

	static boolean isReference(MBeanAttributeInfo ai) {
		ai.descriptor.getFieldValue('com.bea.relationship') == 'reference'
	}

	static boolean isChild(MBeanAttributeInfo ai) {
		ai.descriptor.getFieldValue('com.bea.relationship') == 'containment'
	}
	
	static boolean getCreator(MBeanAttributeInfo ai) {
		ai.descriptor.getFieldValue 'com.bea.creator'
	}
	
	static boolean getDestroyer(MBeanAttributeInfo ai) {
		ai.descriptor.getFieldValue 'com.bea.destroyer'
	}
	
	static String describe(MBeanFeatureInfo fi) {
		def key = isKey(fi) ? 'K' : 'k'
		def unharvestable = isUnharvestable(fi) ? 'U' : 'u'
		def ref = isReference(fi) ? 'R' : 'r'
		def child = isChild(fi) ? 'C' : 'c'
		"$key$unharvestable$ref$child"
	}
}
