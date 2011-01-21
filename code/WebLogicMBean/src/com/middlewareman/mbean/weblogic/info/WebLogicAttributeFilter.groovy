/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic.info

import javax.management.MBeanAttributeInfo

import com.middlewareman.mbean.MBean
import com.middlewareman.mbean.info.SimpleAttributeFilter

class WebLogicAttributeFilter extends SimpleAttributeFilter {

	Boolean key
	Boolean reference
	Boolean child
	Boolean unharvestable
	Boolean isSet

	boolean acceptAttribute(MBeanAttributeInfo ai) {
		if (!super.acceptAttribute(ai)) return false
		use(WebLogicMBeanInfoCategory) {
			if (key == true && !ai.isKey()) return false
			if (key == false && ai.isKey()) return false
			if (reference == true && !ai.isReference()) return false
			if (reference == false && ai.isReference()) return false
			if (child == true && !ai.isChild()) return false
			if (child == false && ai.isChild()) return false
			if (unharvestable == true && !ai.isUnharvestable()) return false
			if (unharvestable == false && ai.isUnharvestable()) return false
			return true
		}
	}

	boolean acceptAttribute(MBeanAttributeInfo ai, Object value) {
		if (!super.acceptAttribute(ai, value)) return false
		return true
	}

	boolean acceptAttribute(MBeanAttributeInfo ai, MBean parent) {
		if (isSet == true && !parent.isSet(ai.name)) return false
		if (isSet == false && parent.isSet(ai.name)) return false
		return true
	}
}