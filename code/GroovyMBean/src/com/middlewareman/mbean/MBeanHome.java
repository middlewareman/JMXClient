/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import com.middlewareman.mbean.type.AttributeFilter;
import com.middlewareman.mbean.type.CompositeDataWrapper;
import com.middlewareman.mbean.type.OpenTypeWrapper;
import com.middlewareman.mbean.type.SimpleAttributeFilter;

/**
 * {@link MBean} factory that handles access to MBeanServer and wraps and
 * unwraps values from and to the MBeanServer respectively. An
 * {@link ObjectName} value from the server is wrapped into an {@link MBean}.
 * {@link CompositeData} and {@link TabularData} values from the server are
 * wrapped into {@link CompositeDataWrapper} and {@link TabularDataWrapper}
 * respectively and recursively by {@link OpenTypeWrapper}. Combined with
 * property and method delegation of an {@link MBean} subclass, this allows
 * transparent and intuitive access to a remote MBean and its attributes as if
 * they were plain Groovy objects.
 * 
 * @author Andreas Nyberg
 */
public abstract class MBeanHome implements MBeanServerConnectionFactory,
		Closeable {

	private static final Object[] NOARGS = new Object[0];

	static Object[] argsArray(Object obj) {
		if (obj == null)
			return NOARGS;
		else if (obj instanceof Object[])
			return (Object[]) obj;
		else
			return new Object[] { obj };
	}

	static String capitalise(String string) {
		char first = string.charAt(0);
		if (!Character.isUpperCase(first)) {
			char[] ca = string.toCharArray();
			ca[0] = Character.toUpperCase(first);
			return new String(ca);
		} else {
			return string;
		}
	}

	static String decapitalise(String name) {
		return java.beans.Introspector.decapitalize(name);
	}

	/** Used equality and logging etc. */
	protected Object url;

	/**
	 * If true, the existence of an MBean with a given ObjectName is verified
	 * before an {@link MBean} instance is returned to the client. This provides
	 * early failure instead of invalid {@link MBean} proxies to invalid remote
	 * MBeans at the costs a network round trip for each access.
	 */
	public boolean assertRegistered = false;

	private boolean blind = true;

	/** Filter to use for {@link #GetPropertiesAttributeFilter}. */
	public final AttributeFilter getPropertiesFilter = SimpleAttributeFilter
			.getSafe();

	/** Serialization only. */
	protected MBeanHome() {
	}

	/**
	 * @param url
	 *            Arbitrary object to identify the server instance used for
	 *            logging and equality.
	 */
	public MBeanHome(Object url) {
		this.url = url;
	}

	/**
	 * @throws IOException
	 *             if MBeanServer is not available.
	 */
	public void ping() throws IOException {
		assert getMBeanServerConnection().getDefaultDomain() != null;
	}

	/**
	 * Creates an {@link MBean} instance. Subclass may override to provide
	 * different implementation or caching.
	 */
	protected MBean createMBean(ObjectName objectName) {
		if (blind)
			return new BlindMBean(this, objectName);
		else
			return new BackedMBean(this, objectName);
	}

	/**
	 * @param objectName
	 *            Uniquely identifies the MBean on this MBeanServer.
	 * @throws InstanceNotFoundException
	 *             if {@link #assertRegistered} and the MBean does not exist.
	 * @throws IOException
	 *             if access to MBeanServer failed.
	 */
	public MBean getMBean(ObjectName objectName)
			throws InstanceNotFoundException, IOException {
		if (assertRegistered) {
			if (!getMBeanServerConnection().isRegistered(objectName))
				throw new InstanceNotFoundException(objectName.toString());
		}
		return createMBean(objectName);
	}

	/**
	 * @param objectName
	 *            Uniquely identifies the MBean on this MBeanServer.
	 * @throws InstanceNotFoundException
	 *             if {@link #assertRegistered} and the MBean does not exist.
	 * @throws IOException
	 *             if access to MBeanServer failed.
	 * @throws MalformedObjectNameException
	 *             of the object name was invalid.
	 */
	public MBean getMBean(String objectName) throws InstanceNotFoundException,
			MalformedObjectNameException, IOException {
		return getMBean(ObjectName.getInstance(objectName));
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
		Set<ObjectName> names = getMBeanServerConnection().queryNames(name,
				query);
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

	/** Delegates to {@link MBeanServerConnection#getMBeanInfo(ObjectName)}. */
	public MBeanInfo getInfo(ObjectName objectName)
			throws InstanceNotFoundException, IntrospectionException,
			ReflectionException, IOException {
		return getMBeanServerConnection().getMBeanInfo(objectName);
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
		Object result = getMBeanServerConnection().invoke(objectName,
				operationName, params, signature);
		return wrap(result);
	}

	/**
	 * Returns the wrapped attribute value of remote MBean.
	 */
	public Object getAttribute(ObjectName objectName, String attributeName)
			throws AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, IOException {
		Object result = getMBeanServerConnection().getAttribute(objectName,
				attributeName);
		return wrap(result);
	}

	/**
	 * Returns wrapped attribute values of remote MBean in bulk.
	 */
	public Object[] getAttributes(ObjectName objectName, String[] attributeNames)
			throws InstanceNotFoundException, ReflectionException, IOException {
		List<Attribute> attributes = getMBeanServerConnection().getAttributes(
				objectName, attributeNames).asList();
		Object[] result = new Object[attributes.size()];
		for (int i = 0; i < result.length; i++)
			result[i] = wrap(attributes.get(i).getValue());
		return result;
	}

	/** Sets attribute of remote MBean with unwrapped value. */
	public void setAttribute(ObjectName objectName, String attributeName,
			Object value) throws InstanceNotFoundException,
			AttributeNotFoundException, InvalidAttributeValueException,
			MBeanException, ReflectionException, IOException {
		Attribute attribute = new Attribute(attributeName, unwrap(value));
		getMBeanServerConnection().setAttribute(objectName, attribute);
	}

	/** Sets attributes of remote MBean with unwrapped values in bulk. */
	public void setAttributes(ObjectName objectName, Map<String, Object> map)
			throws InstanceNotFoundException, ReflectionException, IOException {
		AttributeList attributeList = new AttributeList(map.size());
		for (Entry<String, Object> entry : map.entrySet()) {
			attributeList.add(new Attribute(entry.getKey(), unwrap(entry
					.getValue())));
		}
		getMBeanServerConnection().setAttributes(objectName, attributeList);
	}

	/** Returns a wrapped object. */
	private Object wrap(Object unwrapped) {
		if (unwrapped == null)
			return null;
		assert !(unwrapped instanceof Collection); // TODO Can this happen?
		if (unwrapped instanceof ObjectName[]) {
			ObjectName[] objectNames = (ObjectName[]) unwrapped;
			MBean[] wrapped = new MBean[objectNames.length];
			for (int i = 0; i < objectNames.length; i++)
				if (objectNames[i] != null)
					wrapped[i] = createMBean(objectNames[i]);
			return wrapped;
		} else if (unwrapped instanceof Object[]) {
			Object[] objects = (Object[]) unwrapped;
			for (int i = 0; i < objects.length; i++)
				objects[i] = wrap(objects[i]);
			return objects;
		} else if (unwrapped instanceof ObjectName)
			return createMBean((ObjectName) unwrapped);
		else
			return OpenTypeWrapper.wrap(unwrapped);
	}

	/** Returns an unwrapped object. */
	private Object unwrap(Object wrapped) {
		if (wrapped == null)
			return null;
		assert !(wrapped instanceof Collection); // TODO Can this happen?
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
			return OpenTypeWrapper.unwrap(wrapped);
	}

	public String toString() {
		return getClass().getSimpleName() + "(" + url.toString() + ")";
	}

	/** Returns true if the other object is an MBeanHome with the same url. */
	public boolean equals(Object other) {
		if (other instanceof MBeanHome) {
			MBeanHome mhother = (MBeanHome) other;
			return url.equals(mhother.url);
		}
		return false;
	}

	/** Returns hashCode of url. */
	public int hashCode() {
		return url.hashCode();
	}

	/**
	 * {@link #getProperties(ObjectName, AttributeFilter)} with default
	 * {@link #getPropertiesFilter}.
	 */
	public Map<String, ?> getProperties(ObjectName objectName)
			throws InstanceNotFoundException, IntrospectionException,
			ReflectionException, IOException {
		return getProperties(objectName, getPropertiesFilter);
	}

	/**
	 * Return selected attributes of an MBean as a map.
	 * 
	 * @param attributeFilter
	 *            Specifies which attributes to include and how to handle
	 *            decapitalisation and exceptions.
	 * @return Map of key-value and key-exception pairs.
	 * @throws IOException
	 * @throws ReflectionException
	 * @throws IntrospectionException
	 * @throws InstanceNotFoundException
	 */
	public Map<String, ?> getProperties(ObjectName objectName,
			AttributeFilter attributeFilter) throws InstanceNotFoundException,
			IntrospectionException, ReflectionException, IOException {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		// TODO getAttributes (in bulk)?
		for (MBeanAttributeInfo attribute : getInfo(objectName).getAttributes()) {
			if (attributeFilter.acceptAttribute(attribute)) {
				String name = attribute.getName();
				String newName = attributeFilter.isDecapitalise() ? MBeanHome
						.decapitalise(name) : name;
				// TODO any other criteria for exceptions to pass through?
				try {
					Object value = getAttribute(objectName, name);
					map.put(newName, value);
				} catch (IOException e) {
					throw e;
				} catch (InstanceNotFoundException e) {
					throw e;
					// TODO AttributeNotFoundException?
				} catch (Exception e) {
					switch (attributeFilter.getOnException()) {
					case RETURN:
						map.put(newName, e);
						// TODO logging?
						break;
					case THROW:
						throw new InstanceException(url, objectName, e);
						// TODO AttributeException?
					case OMIT:
						// TODO logging?
					}
				}
			}
		}
		return map;
	}
}
