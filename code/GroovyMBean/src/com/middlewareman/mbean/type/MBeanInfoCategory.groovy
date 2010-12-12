/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.type

import javax.management.Descriptor 
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
}
