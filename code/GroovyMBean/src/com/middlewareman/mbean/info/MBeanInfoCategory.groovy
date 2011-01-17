/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.info

import javax.management.MBeanAttributeInfo
import javax.management.MBeanFeatureInfo

class MBeanInfoCategory {

	private static final mbeanTypePattern =
	~/^\[*Ljavax\.management\.ObjectName;$|^javax\.management\.ObjectName(\[\])*$/

	// any
	static boolean isDeprecated(MBeanFeatureInfo fi) {
		'deprecated' in fi.descriptor.fieldNames
	}

	// attribute, parameter
	static boolean hasDefaultValue(MBeanFeatureInfo fi) {
		'defaultValue' in fi.descriptor.fieldNames
	}
	
	// attribute, parameter
	static Object getDefaultValue(MBeanFeatureInfo fi) {
		fi.descriptor.getFieldValue 'defaultValue'
	}

	static boolean isMBean(MBeanAttributeInfo ai) {
		ai.type ==~ mbeanTypePattern
	}

	static String describe(MBeanAttributeInfo ai) {
		def dep = isDeprecated(ai) ? 'DEP' : 'dep'
		def defa = hasDefaultValue(ai) ? 'DEF' : 'def'
		def mb = isMBean(ai) ? 'MB' : 'mb'
		"$dep$defa$mb"
	}
}
