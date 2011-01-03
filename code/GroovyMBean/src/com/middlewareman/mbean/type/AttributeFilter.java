/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.type;

import javax.management.MBeanAttributeInfo;

/**
 * Filtering for MBean attribute based on info and value. Clients should take
 * the following steps when iterating over attributes:
 * <ol>
 * <li>If not {@link #acceptAttribute(MBeanAttributeInfo)}, ignore the
 * attribute.</li>
 * <li>Retrieve the value.</li>
 * <li>If not {@link #acceptAttribute(MBeanAttributeInfo, Object)}, ignore the
 * attribute.</li>
 * <li>Handle attribute name and value.</li>
 * </ol>
 * 
 * @author Andreas Nyberg
 */
public interface AttributeFilter {

	/** Returns true if this attribute should be included. */
	boolean acceptAttribute(MBeanAttributeInfo attributeInfo);

	/** Returns true if this attribute should be included. */
	boolean acceptAttribute(MBeanAttributeInfo attributeInfo, Object value);

}
