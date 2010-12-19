/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.*;

/**
 * Identity of an MBean on an MBeanServer. Note that property access and method
 * calls are intercepted by the subclass implementation for Groovy callers.
 * 
 * @author Andreas Nyberg
 */
public abstract class MBean {

	static final Logger logger = Logger.getLogger(MBean.class.getName());

	/**
	 * Returns the object that identifies and provides access to the MBeanServer
	 * and is used to access this and other MBeans on it and and wrap/unwrap any
	 * of their values.
	 * <p>
	 * This public Java attribute is accessed as <code>mbean.@home</code> from
	 * Groovy to avoid property access interception.
	 * </p>
	 */
	public final MBeanHome home;

	/**
	 * Returns the identity of the MBean on an MBeanServer.
	 * <p>
	 * This public Java attribute is accessed as <code>mbean.@objectName</code>
	 * from Groovy to avoid property access interception.
	 * </p>
	 */
	public final ObjectName objectName;

	public MBean(MBeanHome home, ObjectName objectName) {
		this.home = home;
		this.objectName = objectName;
	}

	public String toString() {
		return getClass().getSimpleName() + "(" + objectName + ")";
	}

	public int hashCode() {
		return objectName.hashCode();
	}

	/**
	 * True if other is {@link MBean} with equal {@link #objectName} and
	 * {@link #home}.
	 */
	public boolean equals(Object other) {
		if (other instanceof MBean) {
			MBean mother = (MBean) other;
			return objectName.equals(mother.objectName)
					&& home.equals(mother.home);
		} else
			return false;
	}

	/**
	 * Groovy truth: this object is true if and only if we can successfully
	 * connect to its MBeanServer and an MBean with the same ObjectName is
	 * registered there.
	 */
	public boolean asBoolean() {
		try {
			boolean isreg = home.getMBeanServerConnection().isRegistered(
					objectName);
			if (logger.isLoggable(Level.FINER))
				logger.log(Level.FINER, "isRegistered " + this + " -> " + isreg);
			return isreg;
		} catch (IOException e) {
			// TODO clean exception before logging
			if (logger.isLoggable(Level.FINE))
				logger.log(Level.INFO, "exception " + this + " -> false", e);
			return false;
		}
	}

	/**
	 * Return a fresh or cached MBeanInfo for this MBean. Note that this value
	 * might only be accessible as the property <code>mbean.info</code> rather
	 * than through its getter
	 * <code>mbean.getInfo()<code> due to method interception.
	 * 
	 * @see MBeanHome#getInfo(ObjectName)
	 */
	public MBeanInfo getInfo() throws InstanceNotFoundException,
			IntrospectionException, ReflectionException, IOException {
		return home.getInfo(objectName);
	}

}
