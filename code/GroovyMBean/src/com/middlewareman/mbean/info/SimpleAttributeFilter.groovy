/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.info

import java.util.regex.Pattern

import javax.management.*


/**
 * Filter(?) to determine which MBean attributes to include and how to handle
 * attribute name capitalisation and exceptions. Clients should take the
 * following steps when iterating over attributes:
 * <ol>
 * <li>If not {@link #acceptAttribute(MBeanAttributeInfo)}, ignore the
 * attribute.</li>
 * <li>Retrieve the value and handle any exception according to
 * {@link #getOnException()}.</li>
 * <li>If not {@link #acceptAttribute(MBeanAttributeInfo, Object)}, ignore the
 * attribute.</li>
 * <li>If {@link #isDecapitalise()}, decapitalise the attribute name.</li>
 * <li>Handle attribute name and value.</li>
 * </ol>
 * <p>
 * Decapitalisation is useful when dealing with MBeans that have a familiar Java
 * interface, such as the Java Platform MXBeans. Where the Java interface
 * specifies <code>Type getXyz()</code> and/or
 * <code>void setXyz(Type value)</code>, the corresponding Groovy property is
 * <code>object.xyz</code>. However, because the name of the MBean attribute is
 * typically <code>Xyz</code>, decapitalisation maintains consistency between
 * the two access models.
 * </p>
 * 
 * @author Andreas Nyberg
 */
class SimpleAttributeFilter implements AttributeFilter {

	/** 
	 * Instance to emulate behaviour of the default GDK
	 * {@link org.codehaus.groovy.runtime.DefaultGroovyMethods#getProperties(Object) 
	 * getProperties()}.
	 */
	static SimpleAttributeFilter getNative() {
		new SimpleAttributeFilter(decapitalise:true)
	}

	/** Instance to include all attributes, and include any exception as a value. */
	static SimpleAttributeFilter getVerbose() {
		new SimpleAttributeFilter(onException:OnException.RETURN)
	}

	/** 
	 * Instance to include only non-deprecated attributes that are readable, 
	 * not null, not defaulted and do not throw an exception. 
	 */
	static SimpleAttributeFilter getBrief() {
		new SimpleAttributeFilter(deprecated:false, readable:true,
				nullValue:false, defaultValue:false)
	}

	/** 
	 * Instance to include only non-deprecated attributes that are readable and
	 * do not throw an exception. Acts as {@link #getNative()} but possibly 
	 * faster as it does not even try to read non-readable attributes.
	 */
	static SimpleAttributeFilter getSafe() {
		new SimpleAttributeFilter(deprecated:false, readable:true)
	}

	Boolean deprecated
	Boolean readable
	Boolean writable
	Boolean mbeans
	Boolean nullValue
	Boolean defaultValue

	Pattern typeInclude

	/**
	 * Returns true if attribute names should be decapitalised before returning
	 * to the client.
	 * 
	 * @see java.beans.Introspector#decapitalize(String)
	 */
	boolean decapitalise

	/** 
	 * Defines behaviour when getting an exception.
	 * 
	 * @see AttributeFilter#getOnException() 
	 */
	OnException onException = OnException.OMIT

	boolean acceptAttribute(MBeanAttributeInfo ai) {
		use(MBeanInfoCategory) {
			if (deprecated == false && ai.isDeprecated()) return false
			if (deprecated == true && !ai.isDeprecated()) return false
			if (readable == false && ai.isReadable()) return false
			if (readable == true && ai.isReadable()) return false
			if (writable == false && ai.isWritable()) return false
			if (writable == true && !ai.isWritable()) return false
			if (mbeans == false && ai.isMBean()) return false
			if (mbeans == true && !ai.isMBean()) return false
			if (typeInclude && !(ai.type ==~ typeInclude)) return false
		}
		return true
	}

	boolean acceptAttribute(MBeanAttributeInfo ai, Object value) {
		if (nullValue == false && value == null) return false
		if (nullValue == true && value != null) return false
		use(MBeanInfoCategory) {
			if (defaultValue == false && ai.hasDefaultValue() && ai.getDefaultValue() == value) return false
			if (defaultValue == true && ai.hasDefaultValue() && ai.getDefaultValue() != value) return false
		}
		return true
	}
}

