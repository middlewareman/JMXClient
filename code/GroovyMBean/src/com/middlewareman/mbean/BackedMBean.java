/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import groovy.lang.MetaClass;

import java.io.IOException;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.middlewareman.mbean.meta.MBeanMetaClass;

/**
 * Experimental MBean implementation that uses Groovy MetaClass instead of
 * simple pretend methods and properties.
 * 
 * @author Andreas Nyberg
 */
public class BackedMBean extends BlindMBean {

	private transient MBeanMetaClass metaClass;
	public boolean blind;

	public BackedMBean(MBeanHome home, ObjectName objectName) {
		super(home, objectName);
	}

	public Object getProperty(String propertyName) {
		if (blind)
			return super.getProperty(propertyName);
		else
			return getMetaClass().getProperty(this, propertyName);
	}

	public void setProperty(String propertyName, Object value) {
		if (blind)
			super.setProperty(propertyName, value);
		else
			getMetaClass().setProperty(this, propertyName, value);
	}

	public Object invokeMethod(String methodName, Object args) {
		if (blind)
			return super.invokeMethod(methodName, args);
		else
			return getMetaClass().invokeMethod(this, methodName, args);
	}

	public synchronized MetaClass getMetaClass() {
		if (metaClass == null) {
			try {
				metaClass = new MBeanMetaClass(home.getInfo(objectName));
				return metaClass;
			} catch (InstanceNotFoundException e) {
				throw new RuntimeException(e);
			} catch (IntrospectionException e) {
				throw new RuntimeException(e);
			} catch (ReflectionException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			// TODO Check expired
			return metaClass;
		}
	}

	public void setMetaClass(MetaClass metaClass) {
		throw new UnsupportedOperationException(
				"Cannot explicitly set metaClass for BackedMBean");
	}
}
