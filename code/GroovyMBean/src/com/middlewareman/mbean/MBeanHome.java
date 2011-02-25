/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.*;
import javax.management.openmbean.*;

import com.middlewareman.mbean.info.SimpleAttributeFilter;
import com.middlewareman.mbean.type.CompositeDataWrapper;
import com.middlewareman.mbean.type.OpenTypeWrapper;

/**
 * {@link MBean} factory that manages an {@link MBeanServerConnection} and
 * access over it, and wraps and unwraps values from and to the MBeanServer
 * respectively. An {@link ObjectName} value from the server is wrapped into an
 * {@link MBean}. {@link CompositeData} and {@link TabularData} values from the
 * server are wrapped into {@link CompositeDataWrapper} and
 * {@link TabularDataWrapper} respectively and recursively by
 * {@link OpenTypeWrapper}. Combined with property and method delegation of an
 * {@link MBean} implementation, this provides <a
 * href="http://groovy.codehaus.org/GPath">GPath</a>-like access to referenced
 * MBeans and {@link OpenType} values.
 * 
 * @author Andreas Nyberg
 */
public abstract class MBeanHome implements MBeanServerConnectionFactory,
		MBeanFactory, MBeanInfoFactory, Closeable {

	public final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * If true, the existence of an MBean with a given ObjectName is verified
	 * before an {@link MBean} instance is returned to the client. This provides
	 * early failure instead of invalid {@link MBean} proxies to invalid remote
	 * MBeans at the costs a network round trip for each access.
	 */
	public boolean assertRegistered = false;

	/**
	 * Return lists instead of arrays. This is helpful when collection behaviour
	 * is desired, such as comparing property maps of MBeans; two arrays
	 * containing the same elements are not equal, but lists are.
	 */
	public boolean listProperties = false;

	/** Defaults to <code>this</code>. */
	public MBeanFactory mbeanFactory;

	/** Defaults to <code>this</code>. */
	public MBeanInfoFactory mbeanInfoFactory;

	/** Filter to use for {@link #getProperties(ObjectName)}. */
	private SimpleAttributeFilter defaultPropertiesFilter;

	public MBeanHome() {
		this.mbeanFactory = this;
		this.mbeanInfoFactory = this;
	}

	/** Returns true if the other object is an MBeanHome with the same server. */
	public boolean equals(Object other) {
		if (other instanceof MBeanHome) {
			MBeanHome mhother = (MBeanHome) other;
			return getAddress().equals(mhother.getAddress());
		}
		return false;
	}

	/** Returns hashCode of server. */
	public int hashCode() {
		return getAddress().hashCode();
	}

	public String toString() {
		return getClass().getSimpleName() + "(" + getAddress().toString() + ")";
	}

	public void enableMBeanCache() {
		mbeanFactory = new CachingMBeanFactory(this);
	}

	public void disableMBeanCache() {
		mbeanFactory = this;
	}

	public void enableMBeanInfoCache(Long timeout) {
		mbeanInfoFactory = new CachingMBeanInfoFactory(this, timeout);
	}

	public void disableMBeanInfoCache() {
		mbeanInfoFactory = this;
	}

	public SimpleAttributeFilter getDefaultPropertiesFilter() {
		if (defaultPropertiesFilter == null)
			setDefaultPropertiesFilter(SimpleAttributeFilter.getNative());
		return defaultPropertiesFilter;
	}

	public synchronized void setDefaultPropertiesFilter(
			SimpleAttributeFilter filter) {
		defaultPropertiesFilter = filter;
	}

	/**
	 * Default implementation to create {@link MBean} instances. Client should
	 * use {@link #getMBean(ObjectName)} or {@link #getMBean(String)} to benefit
	 * from caching etc.
	 * 
	 * @see #mbeanFactory
	 */
	public MBean createMBean(ObjectName objectName)
			throws InstanceNotFoundException, IOException {
		if (assertRegistered) {
			if (getConnection().isRegistered(objectName))
				throw new InstanceNotFoundException(objectName.toString());
		}
		return new BlindMBean(this, objectName);
	}

	/**
	 * Default implementation to retrieve {@link MBeanHome} instances. Clients
	 * should use {@link #getInfo(ObjectName)} to benefit from caching etc.
	 * 
	 * @see #mbeanInfoFactory
	 */
	public MBeanInfo createMBeanInfo(ObjectName objectName)
			throws InstanceNotFoundException, IntrospectionException,
			ReflectionException, IOException {
		return getConnection().getMBeanInfo(objectName);
	}

	/**
	 * Returns an {@link MBean} instance.
	 * 
	 * @throws InstanceNotFoundException
	 * @throws IOException
	 */
	public MBean getMBean(ObjectName objectName)
			throws InstanceNotFoundException, IOException {
		return mbeanFactory.createMBean(objectName);
	}

	/**
	 * Returns an {@link MBean} instance.
	 * 
	 * @throws InstanceNotFoundException
	 * @throws MalformedObjectNameException
	 * @throws IOException
	 */
	public MBean getMBean(String objectName) throws InstanceNotFoundException,
			MalformedObjectNameException, IOException {
		return getMBean(new ObjectName(objectName));
	}

	/**
	 * Returns an {@link MBeanInfo} instance.
	 * 
	 * @throws InstanceNotFoundException
	 * @throws IOException
	 */
	public MBeanInfo getInfo(ObjectName objectName)
			throws InstanceNotFoundException, IntrospectionException,
			ReflectionException, IOException {
		return mbeanInfoFactory.createMBeanInfo(objectName);
	}

	/**
	 * @throws IOException
	 *             if MBeanServer is not available.
	 */
	public void ping() throws IOException {
		assert getConnection().getDefaultDomain() != null;
	}

	/**
	 * Called by any client encountering IOException. Subclass might override to
	 * provide some recovery behaviour.
	 */
	public void recover() {
		logger.logp(Level.INFO, this.getClass().getName(), "recover()",
				"A problem with the connection has been indicated");
	}

	/**
	 * Delegates to
	 * {@link MBeanServerConnection#queryNames(ObjectName, QueryExp)} and
	 * returns the wrapped result.
	 * 
	 * @throws IOException
	 *             if access to MBeanServer failed.
	 * @throws InstanceNotFoundException
	 *             should not happen as we just found it.
	 */
	public Set<MBean> getMBeans(ObjectName name, QueryExp query)
			throws IOException, InstanceNotFoundException {
		Set<ObjectName> names = getConnection().queryNames(name, query);
		Set<MBean> mbeans = new LinkedHashSet<MBean>(names.size());
		for (ObjectName objectName : names)
			mbeans.add(getMBean(objectName));
		return mbeans;
	}

	/** Simplified version of {@link #getMBeans(ObjectName, QueryExp). */
	public Set<MBean> getMBeans(String objectName)
			throws InstanceNotFoundException, MalformedObjectNameException,
			IOException {
		return getMBeans(new ObjectName(objectName), null);
	}

	/**
	 * Unwrap parameters, invoke operation on remote MBean and return the
	 * wrapped result.
	 */
	public Object invokeOperation(ObjectName objectName, String operationName,
			Object args) throws InstanceNotFoundException, MBeanException,
			ReflectionException, IOException {
		args = unwrap(args);
		Object[] params = argsArray(args);
		String[] signature = new String[params.length];
		for (int i = 0; i < params.length; i++) {
			if (params[i] == null)
				signature[i] = Object.class.getName(); // TODO Really valid?
			else
				signature[i] = params[i].getClass().getName();
		}
		return invokeOperation(objectName, operationName, params, signature);
	}

	/**
	 * Unwrap parameters, invoke operation on remote MBean and return the
	 * wrapped result.
	 */
	public Object invokeOperation(ObjectName objectName, String operationName,
			Object[] params, String[] signature)
			throws InstanceNotFoundException, MBeanException,
			ReflectionException, IOException {
		// TODO already unwrapped?
		Object result = getConnection().invoke(objectName, operationName,
				params, signature);
		return wrap(result);
	}

	/**
	 * Returns the wrapped attribute value of remote MBean.
	 */
	public Object getAttribute(ObjectName objectName, String attributeName)
			throws AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, IOException {
		Object result = getConnection().getAttribute(objectName, attributeName);
		return wrap(result);
	}

	/**
	 * Returns wrapped attribute name-values pairs retrieved from a remote MBean
	 * in bulk. Note that the returned key set might not contain all names.
	 */
	public Map<String, Object> getAttributes(ObjectName objectName,
			String[] attributeNames) throws InstanceNotFoundException,
			ReflectionException, IOException {
		List<Attribute> attributes = getConnection().getAttributes(objectName,
				attributeNames).asList();
		Map<String, Object> result = new LinkedHashMap<String, Object>(
				attributes.size());
		for (Attribute attribute : attributes)
			result.put(attribute.getName(), wrap(attribute.getValue()));
		return result;
	}

	/** Sets attribute of remote MBean with unwrapped value. */
	public void setAttribute(ObjectName objectName, String attributeName,
			Object value) throws InstanceNotFoundException,
			AttributeNotFoundException, InvalidAttributeValueException,
			MBeanException, ReflectionException, IOException {
		Attribute attribute = new Attribute(attributeName, unwrap(value));
		getConnection().setAttribute(objectName, attribute);
	}

	/** Sets attributes of remote MBean with unwrapped values in bulk. */
	public void setAttributes(ObjectName objectName, Map<String, Object> map)
			throws InstanceNotFoundException, ReflectionException, IOException {
		AttributeList attributeList = new AttributeList(map.size());
		for (Entry<String, Object> entry : map.entrySet()) {
			attributeList.add(new Attribute(entry.getKey(), unwrap(entry
					.getValue())));
		}
		getConnection().setAttributes(objectName, attributeList);
	}

	/**
	 * Returns a wrapped object.
	 * 
	 * @throws IOException
	 * @throws InstanceNotFoundException
	 */
	private Object wrap(Object notwrapped) throws InstanceNotFoundException,
			IOException {
		if (notwrapped == null)
			return null;
		if (notwrapped instanceof ObjectName[]) {
			ObjectName[] objectNames = (ObjectName[]) notwrapped;
			MBean[] wrapped = new MBean[objectNames.length];
			for (int i = 0; i < objectNames.length; i++)
				if (objectNames[i] != null)
					wrapped[i] = getMBean(objectNames[i]);
			return listProperties ? Arrays.asList(wrapped) : wrapped;
		} else if (notwrapped instanceof Object[]) {
			Object[] notwrappedArray = (Object[]) notwrapped;
			Object[] objects = new Object[notwrappedArray.length];
			for (int i = 0; i < objects.length; i++)
				objects[i] = wrap(notwrappedArray[i]);
			return listProperties ? Arrays.asList(objects) : objects;
		} else if (notwrapped instanceof ObjectName)
			return getMBean((ObjectName) notwrapped);
		else
			return OpenTypeWrapper.wrap(notwrapped); // TODO listProperties
	}

	/** Returns an unwrapped object. */
	private Object unwrap(Object wrapped) {
		if (wrapped == null)
			return null;
		if (listProperties && wrapped instanceof List)
			wrapped = ((List<?>) wrapped).toArray();
		// TODO will miss MBean[] match, but does it matter?
		if (wrapped instanceof MBean[]) {
			MBean[] mbeans = (MBean[]) wrapped;
			ObjectName[] objectNames = new ObjectName[mbeans.length];
			for (int i = 0; i < mbeans.length; i++)
				if (mbeans[i] != null)
					objectNames[i] = mbeans[i].objectName;
			return objectNames;
		} else if (wrapped instanceof Object[]) {
			Object[] objects = (Object[]) wrapped;
			for (int i = 0; i < objects.length; i++) {
				objects[i] = unwrap(objects[i]);
			}
			return objects;
		} else if (wrapped instanceof MBean)
			return ((MBean) wrapped).objectName;
		else
			return OpenTypeWrapper.unwrap(wrapped); // TODO List ?
	}

	private static final Object[] NOARGS = new Object[0];

	private static Object[] argsArray(Object obj) {
		if (obj == null)
			return NOARGS;
		else if (obj instanceof Object[])
			return (Object[]) obj;
		else
			return new Object[] { obj };
	}

}
