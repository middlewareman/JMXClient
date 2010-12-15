/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.type;

import javax.management.MBeanAttributeInfo;

/**
 * Filter to determine which MBean attributes to include and how to handle
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
public interface AttributeFilter {

	/**
	 * Indicates how to treat exceptions when retrieving an attribute.
	 * {@link #THROW} simply throws the exception. {@link #RETURN} returns the
	 * exception itself as the value. {@link #OMIT} omits the attribute from the
	 * result.
	 */
	public enum OnException {
		THROW, RETURN, OMIT
	}

	/** Returns true if this attribute should be included. */
	boolean acceptAttribute(MBeanAttributeInfo attributeInfo);

	/** Returns true if this attribute should be included. */
	boolean acceptAttribute(MBeanAttributeInfo attributeInfo, Object value);

	/**
	 * Returns true if attribute names should be decapitalised before returning
	 * to the client.
	 * 
	 * @see java.beans.Introspector#decapitalize(String)
	 */
	boolean isDecapitalise();

	/** Indicates how to handle exceptions during a bulk retrieval of values. */
	OnException getOnException();

}
