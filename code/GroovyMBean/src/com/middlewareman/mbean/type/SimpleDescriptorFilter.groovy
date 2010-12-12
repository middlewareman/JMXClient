/*
* $Id$
* Copyright � 2010 Middlewareman Limited. All rights reserved.
*/
package com.middlewareman.mbean.type

import javax.management.MBeanFeatureInfo 

public class SimpleDescriptorFilter implements DescriptorFilter {
	
	static {
		MBeanFeatureInfo.mixin MBeanInfoCategory	// TODO Configure where?
	}
	
	boolean accept(MBeanFeatureInfo fi, String fieldName) {
		// TODO precompile regex?
		if (fieldName == 'descriptorType') return false
		if (fieldName ==~ /.*Name/ && fi[fieldName] == fi.name) return false
		if (fieldName ==~ /.*description/ && fi[fieldName] == fi.description) return false
		if (fieldName ==~ /.+(?:R|r)ole/ && fi[fieldName] == fi['role']) return false
		return true;
	}
}
