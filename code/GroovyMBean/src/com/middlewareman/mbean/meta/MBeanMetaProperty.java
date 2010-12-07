/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.meta;

import groovy.lang.MetaProperty;

import javax.management.MBeanAttributeInfo;

/**
 * Experimental MetaProperty for MBean attribute.
 * 
 * @author Andreas Nyberg
 */
public class MBeanMetaProperty extends MetaProperty {

	public MBeanMetaProperty(MBeanAttributeInfo ai)
			throws ClassNotFoundException {
		super(ai.getName(), Class.forName(ai.getType()));
	}

	@Override
	public Object getProperty(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProperty(Object object, Object newValue) {
		// TODO Auto-generated method stub

	}

}
