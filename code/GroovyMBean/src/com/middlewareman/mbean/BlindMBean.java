/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;

import java.io.IOException;
import java.util.Map;

import javax.management.*;
import javax.management.openmbean.OpenType;

import org.codehaus.groovy.runtime.InvokerHelper;

import com.middlewareman.mbean.info.AttributeFilter;
import com.middlewareman.mbean.util.MBeanProperties;

/**
 * MBean implementation that blindly proxies a pretend method to an MBean
 * operation and a pretend property to an MBean attribute similarly to
 * {@link groovy.util.GroovyMBean} but backed by an {@link MBeanHome} and
 * providing <a href="http://groovy.codehaus.org/GPath">GPath</a>-like access to
 * referenced MBeans and {@link OpenType} values.
 * 
 * @author Andreas Nyberg
 */
public class BlindMBean extends MBean implements GroovyObject {

	private transient MetaClass metaClass;

	/**
	 * If true, property names are always capitalised before used as attribute
	 * names.
	 * <p>
	 * This is useful when dealing with MBeans that have a familiar Java
	 * interface, such as the Java Platform MXBeans. Where the Java interface
	 * specifies <code>Type getXyz()</code> and/or
	 * <code>void setXyz(Type value)</code>, the corresponding Groovy property
	 * is <code>object.xyz</code>. However, because the name of the MBean
	 * attribute is typically <code>Xyz</code>, capitalisation is needed to
	 * maintain consistency between the two access models. However, it breaks in
	 * the unusual case that an MBean actually attribute name starts with a
	 * lower case letter. (Another implementation might check for this
	 * condition, but it needs to consider that retrieving the {@link MBeanInfo}
	 * might be expensive, especially if it cannot be cached.)
	 * </p>
	 * <p>
	 * This public attribute is accessed as
	 * <code>mbean.@attributeCapitalisation</code> in Groovy to bypass the
	 * pretend property interception.
	 * </p>
	 * 
	 * @see AttributeFilter
	 */
	public boolean attributeCapitalisation = true;

	public BlindMBean(MBeanHome home, ObjectName objectName) {
		super(home, objectName);
		this.metaClass = InvokerHelper.getMetaClass(this.getClass());
	}

	/**
	 * Blindly proxy method call to MBean operation.
	 * 
	 * @see GroovyObject#invokeMethod(String, Object)
	 */
	public Object invokeMethod(String methodName, Object args) {
		try {
			return home.invokeOperation(objectName, methodName, args);
		} catch (InstanceNotFoundException e) {
			throw new RuntimeException(e);
		} catch (MBeanException e) {
			throw new RuntimeException(e);
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Blindly get MBean attribute with given (capitalised) name, except local
	 * properties {@link #getProperties() properties} and {@link #getInfo()
	 * info}, which breaks in the unusual case that the MBean actually has these
	 * (lower case) attribute names. (Another implementation might detect this
	 * condition.)
	 * 
	 * @see GroovyObject#getProperty(String)
	 */
	public Object getProperty(String propertyName) {
		try {
			if (propertyName.equals("properties"))
				return getProperties();
			if (propertyName.equals("info"))
				return getInfo();
			if (attributeCapitalisation)
				propertyName = capitalise(propertyName);
			return home.getAttribute(objectName, propertyName);
		} catch (AttributeNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstanceNotFoundException e) {
			throw new RuntimeException(e);
		} catch (MBeanException e) {
			throw new RuntimeException(e);
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (IntrospectionException e) { // getProperties only
			throw new RuntimeException(e);
		}
	}

	/**
	 * Blindly set MBean attribute with given (capitalised) name.
	 * 
	 * @see GroovyObject#setProperty(String, Object)
	 */
	public void setProperty(String propertyName, Object value) {
		if (attributeCapitalisation)
			propertyName = capitalise(propertyName);
		try {
			home.setAttribute(objectName, propertyName, value);
		} catch (InstanceNotFoundException e) {
			throw new RuntimeException(e);
		} catch (AttributeNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InvalidAttributeValueException e) {
			throw new RuntimeException(e);
		} catch (MBeanException e) {
			throw new RuntimeException(e);
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public MetaClass getMetaClass() {
		if (metaClass == null)
			metaClass = InvokerHelper.getMetaClass(this.getClass());
		return metaClass;
	}

	public void setMetaClass(MetaClass metaClass) {
		this.metaClass = metaClass;
	}

	/**
	 * Returns a map of all MBean attributes.
	 * 
	 * @see MBeanHome#getProperties(ObjectName)
	 * */
	public Map<String, ?> getProperties() throws InstanceNotFoundException,
			IntrospectionException, AttributeNotFoundException,
			ReflectionException, MBeanException, IOException {
		return MBeanProperties.get(home, objectName,
				home.getDefaultPropertiesFilter());
	}

	private static String capitalise(String string) {
		char first = string.charAt(0);
		if (!Character.isUpperCase(first)) {
			char[] ca = string.toCharArray();
			ca[0] = Character.toUpperCase(first);
			return new String(ca);
		} else {
			return string;
		}
	}

}
