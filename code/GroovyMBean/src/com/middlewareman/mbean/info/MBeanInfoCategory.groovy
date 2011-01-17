/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.info

import java.util.Map;

import javax.management.MBeanAttributeInfo
import javax.management.MBeanFeatureInfo


class MBeanInfoCategory {

	static Map<String,?> getDescriptorMap(MBeanFeatureInfo fi, DescriptorFilter filter = null) {
		def map = [:]
		for (fieldName in fi.descriptor.fieldNames) {
			if (!filter || filter.accept(fi, fieldName))
				map[fieldName] = fi[fieldName]
		}
		return map
	}
	
	static Object getAt(MBeanFeatureInfo fi, String fieldName) {
		fi.descriptor.getFieldValue(fieldName)
	}
	
	static Object[] getAt(MBeanFeatureInfo fi, String[] fieldNames) {
		fi.descriptor.getFieldValues(fieldNames)
	}
	
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

	private static final mbeanTypePattern =
	~/^\[*Ljavax\.management\.ObjectName;$|^javax\.management\.ObjectName(\[\])*$/

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
