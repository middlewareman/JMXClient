/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.type

import com.middlewareman.mbean.type.AttributeFilter.OnException 
import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName 
import javax.management.openmbean.OpenMBeanAttributeInfo 

/**
 * @author Andreas Nyberg
 */
class SimpleAttributeFilter implements AttributeFilter {
	
	/** Don't include deprecated attributes. */
	boolean noDeprecated
	
	/** Only include readable attributes. */
	boolean onlyReadable
	
	/** Only include writable attributes. */
	boolean onlyWritable
	
	/** Don't include any attributes that refer to other MBeans. */
	boolean noMBeans
	
	/** Only include attributes that refer to other MBeans. */
	boolean onlyMBeans
	
	/** Don't include any attributes that declare a default value that is equal to the current value. */
	boolean noDefaultValue
	
	/** @see AttributeFilter#isDecapitalise() */
	boolean decapitalise
	
	/** 
	 * Defaults to throwing any exception. 
	 * @see AttributeFilter#getOnException() 
	 */
	OnException onException = OnException.THROW
	
	boolean acceptAttribute(MBeanAttributeInfo ai) {
		if (noDeprecated && ai.descriptor.getFieldValue('deprecated')) return false
		if (onlyReadable && !ai.isReadable()) return false
		if (onlyWritable && !ai.isWritable()) return false
		if (noMBeans) {
			assert !onlyMBeans
			if (ai.attributeType.contains(ObjectName.getClass().getName())) 
				return false
		} else if (onlyMBeans && !ai.attributeType.contains(ObjectName.getClass().getName())) 
			return false;
		return true;
	}
	
	boolean acceptAttribute(MBeanAttributeInfo ai, Object value) {
		if (noDefaultValue && ai instanceof OpenMBeanAttributeInfo 
		&& ai.hasDefaultValue() && ai.getDefaultValue() != value) 
			return false
	}
}

