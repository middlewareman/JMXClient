/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.info

import javax.management.MBeanOperationInfo

class SimpleOperationFilter implements Comparator<MBeanOperationInfo> {

	final pattern = ~/(\p{javaLowerCase}+)(\p{javaUpperCase}.*)/

	/** Sort aaaBbb according to Bbb first. */
	int compare(String left, String right) {
		def leftm = left =~ pattern
		def rightm = right =~ pattern
		if (leftm && rightm)
			leftm[0][2] <=> rightm[0][2] ?: leftm[0][1] <=> rightm[0][1]
		else
			left <=> right
	}

	int compare(MBeanOperationInfo left, MBeanOperationInfo right) {
		compare(left.name,right.name) ?: right.signature.length <=> left.signature.length
	}

	Boolean deprecated

	boolean accept(MBeanOperationInfo oi) {
		(deprecated != false || MBeanInfoCategory.isDeprecated(oi)) &&
				(deprecated != true || MBeanInfoCategory.isDeprecated(oi))
	}
}
