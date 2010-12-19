/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.management.*;
import javax.management.openmbean.*;

import com.middlewareman.mbean.type.*;

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
			return getServerId().equals(mhother.getServerId());
		}
		return false;
	}

	/** Returns hashCode of server. */
	public int hashCode() {
		return getServerId().hashCode();
	}

	public String toString() {
		return getClass().getSimpleName() + "(" + getServerId().toString()
				+ ")";
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
			if (getMBeanServerConnection().isRegistered(objectName))
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
		return getMBeanServerConnection().getMBeanInfo(objectName);
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
		assert getMBeanServerConnection().getDefaultDomain() != null;
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

	/**
	 * Returns a wrapped object.
	 * 
	 * @throws IOException
	 * @throws InstanceNotFoundException
	 */
	private Object wrap(Object unwrapped) throws InstanceNotFoundException,
			IOException {
		if (unwrapped == null)
			return null;
		if (unwrapped instanceof ObjectName[]) {
			ObjectName[] objectNames = (ObjectName[]) unwrapped;
			MBean[] wrapped = new MBean[objectNames.length];
			for (int i = 0; i < objectNames.length; i++)
				if (objectNames[i] != null)
					wrapped[i] = getMBean(objectNames[i]);
			return listProperties ? Arrays.asList(wrapped) : wrapped;
		} else if (unwrapped instanceof Object[]) {
			Object[] objects = (Object[]) unwrapped;
			for (int i = 0; i < objects.length; i++)
				objects[i] = wrap(objects[i]);
			return listProperties ? Arrays.asList(objects) : objects;
		} else if (unwrapped instanceof ObjectName)
			return getMBean((ObjectName) unwrapped);
		else
			return OpenTypeWrapper.wrap(unwrapped); // TODO listProperties
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

	/**
	 * {@link #getProperties(ObjectName, AttributeFilter)} with default
	 * {@link #getPropertiesFilter}.
	 */
	public Map<String, ?> getProperties(ObjectName objectName)
			throws InstanceNotFoundException, IntrospectionException,
			AttributeNotFoundException, ReflectionException, MBeanException,
			IOException {
		return getProperties(objectName, getDefaultPropertiesFilter());
	}

	public Map<String, ?> getProperties(ObjectName objectName,
			SimpleAttributeFilter attributeFilter)
			throws InstanceNotFoundException, IntrospectionException,
			ReflectionException, IOException, AttributeNotFoundException,
			MBeanException {
		if (attributeFilter.isBulk())
			return getPropertiesBulk(objectName, attributeFilter);
		else
			return getPropertiesSingle(objectName, attributeFilter);
	}

	public Map<String, ?> getPropertiesSingle(ObjectName objectName,
			SimpleAttributeFilter attributeFilter)
			throws InstanceNotFoundException, IntrospectionException,
			ReflectionException, IOException, AttributeNotFoundException,
			MBeanException {
		MBeanAttributeInfo[] ais = getInfo(objectName).getAttributes();
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		if (attributeFilter.getOnException().equals(OnException.THROW)) {
			for (MBeanAttributeInfo attribute : ais) {
				if (attributeFilter.acceptAttribute(attribute)) {
					String name = attribute.getName();
					String newName = attributeFilter.isDecapitalise() ? decapitalise(name)
							: name;
					// TODO any other criteria for exceptions to pass through?
					Object value = getAttribute(objectName, name);
					if (attributeFilter.acceptAttribute(attribute, value))
						map.put(newName, value);
				}
			}
		} else {
			for (MBeanAttributeInfo attribute : ais) {
				if (attributeFilter.acceptAttribute(attribute)) {
					String name = attribute.getName();
					String newName = attributeFilter.isDecapitalise() ? decapitalise(name)
							: name;
					// TODO any other criteria for exceptions to pass through?
					try {
						Object value = getAttribute(objectName, name);
						if (attributeFilter.acceptAttribute(attribute, value))
							map.put(newName, value);
					} catch (IOException e) {
						throw e;
					} catch (InstanceNotFoundException e) {
						throw e;
						// TODO AttributeNotFoundException?
					} catch (Exception e) {
						switch (attributeFilter.getOnException()) {
						case OMIT:
							// TODO logging!
							break;
						case NULL:
							map.put(newName, null);
							// TODO logging!
							break;
						case RETURN:
							map.put(newName, e);
							// TODO logging?
							break;
						default:
							assert false : attributeFilter.getOnException();
						}
					}
				}
			}
		}
		return map;
	}

	public Map<String, ?> getPropertiesBulk(ObjectName objectName,
			SimpleAttributeFilter attributeFilter)
			throws InstanceNotFoundException, IntrospectionException,
			ReflectionException, IOException, AttributeNotFoundException,
			MBeanException {
		MBeanAttributeInfo[] ais = getInfo(objectName).getAttributes();
		Map<String, MBeanAttributeInfo> name2ai = new LinkedHashMap<String, MBeanAttributeInfo>(
				ais.length);
		for (MBeanAttributeInfo ai : ais) {
			if (attributeFilter.acceptAttribute(ai))
				name2ai.put(ai.getName(), ai);
		}
		String[] names = name2ai.keySet().toArray(new String[name2ai.size()]);
		try {
			Object[] values = getAttributes(objectName, names);
			assert names.length == values.length;
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			for (int i = 0; i < names.length; i++) {
				if (attributeFilter.acceptAttribute(name2ai.get(names[i]),
						values[i])) {
					String name = attributeFilter.isDecapitalise() ? decapitalise(names[i])
							: names[i];
					map.put(name, values[i]);
				}
			}
			return map;
		} catch (IOException e) {
			throw e;
		} catch (InstanceNotFoundException e) {
			throw e;
		} catch (Exception e) {
			System.err.println(getClass().getName()
					+ " getProperties bulk failed names="
					+ Arrays.toString(names) + ": " + e.getMessage());
			return getPropertiesSingle(objectName, attributeFilter);
		}
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

	private static String decapitalise(String name) {
		return java.beans.Introspector.decapitalize(name);
	}
}
