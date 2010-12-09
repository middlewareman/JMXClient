/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.type

import com.middlewareman.mbean.type.AttributeFilter.OnException 

import javax.management.MBeanAttributeInfo
import javax.management.ObjectName 
import javax.management.openmbean.OpenMBeanAttributeInfo 

/**
 * @author Andreas Nyberg
 */
class SimpleAttributeFilter implements AttributeFilter {
	
	/** Instance to include all attributes, and include any exception as a value. */
	static SimpleAttributeFilter getVerbose() {
		new SimpleAttributeFilter(onException:OnException.RETURN)
	}
	
	/** 
	 * Instance to include only non-deprecated attributes that are readable, 
	 * not defaulted and do not throw an exception. 
	 */
	static SimpleAttributeFilter getBrief() {
		new SimpleAttributeFilter(noDeprecated:true, onlyReadable:true,
				noDefaultValue:true, onException:OnException.OMIT)
	}
	
	/** 
	 * Instance to include only non-deprecated attributes that are readable, 
	 * and include any exception as a value. 
	 */
	static SimpleAttributeFilter getSafe() {
		new SimpleAttributeFilter(noDeprecated:true, onlyReadable:true,
				onException:OnException.RETURN)
	}
	
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
	
	/** 
	 * Defaults to true.
	 * @see AttributeFilter#isDecapitalise() 
	 */
	boolean decapitalise = true
	
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
		} else if (onlyMBeans && !ai.type.contains(ObjectName.class.getName())) 
			return false;
		return true;
	}
	
	boolean acceptAttribute(MBeanAttributeInfo ai, Object value) {
		if (noDefaultValue && ai instanceof OpenMBeanAttributeInfo 
		&& ai.hasDefaultValue() && ai.getDefaultValue() != value) 
			return false
		return true
	}
}

