package com.middlewareman.mbean;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.RuntimeMBeanException;

import com.middlewareman.mbean.type.MBeanAttributeInfoFilter;
import com.middlewareman.mbean.type.OpenTypeWrapper;

public abstract class MBeanHome implements MBeanServerConnectionFactory, Closeable {

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

	static String decapitalise(String string) {
		char first = string.charAt(0);
		if (Character.isUpperCase(first)) {
			char[] ca = string.toCharArray();
			ca[0] = Character.toLowerCase(first);
			return new String(ca);
		} else {
			return string;
		}
	}

	/** Used equality and logging etc. */
	public Object url;

	public boolean assertRegistered = true;

	public boolean blind = true;

	protected MBeanHome() {
	}

	public MBeanHome(Object url) {
		this.url = url;
	}
	
	public void ping() throws IOException {
		assert getMBeanServerConnection().getDefaultDomain() != null;
	}

	protected MBean createMBean(ObjectName objectName) {
		if (blind)
			return new BlindMBean(this, objectName);
		else
			return new BackedMBean(this, objectName);
	}

	public MBean getMBean(ObjectName objectName)
			throws InstanceNotFoundException, IOException {
		if (assertRegistered) {
			if (!getMBeanServerConnection().isRegistered(objectName))
				throw new InstanceNotFoundException(objectName.toString());
		}
		return createMBean(objectName);
	}

	public MBean getMBean(String objectName) throws InstanceNotFoundException,
			MalformedObjectNameException, NullPointerException, IOException {
		return getMBean(ObjectName.getInstance(objectName));
	}

	public Set<MBean> getMBeans(ObjectName name, QueryExp query)
			throws IOException, InstanceNotFoundException {
		Set<ObjectName> names = getMBeanServerConnection().queryNames(name,
				query);
		Set<MBean> mbeans = new LinkedHashSet<MBean>(names.size());
		for (ObjectName objectName : names)
			mbeans.add(getMBean(objectName));
		return mbeans;
	}

	public Set<MBean> getMBeans(String name) throws InstanceNotFoundException,
			MalformedObjectNameException, IOException {
		return getMBeans(new ObjectName(name), null);
	}

	public MBeanInfo getInfo(ObjectName objectName)
			throws InstanceNotFoundException, IntrospectionException,
			ReflectionException, IOException {
		return getMBeanServerConnection().getMBeanInfo(objectName);
	}

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

	public Object invokeOperation(ObjectName objectName, String operationName,
			Object[] params, String[] signature)
			throws InstanceNotFoundException, MBeanException,
			ReflectionException, IOException {
		// TODO already unwrapped?
		Object result = getMBeanServerConnection().invoke(objectName,
				operationName, params, signature);
		return wrap(result);
	}

	public Object getAttribute(ObjectName objectName, String attributeName)
			throws AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, IOException {
		Object result = getMBeanServerConnection().getAttribute(objectName,
				attributeName);
		return wrap(result);
	}

	public Object[] getAttributes(ObjectName objectName, String[] attributeNames)
			throws InstanceNotFoundException, ReflectionException, IOException {
		List<Attribute> attributes = getMBeanServerConnection().getAttributes(
				objectName, attributeNames).asList();
		Object[] result = new Object[attributes.size()];
		for (int i = 0; i < result.length; i++)
			result[i] = wrap(attributes.get(i).getValue());
		return result;
	}

	public void setAttribute(ObjectName objectName, String attributeName,
			Object value) throws InstanceNotFoundException,
			AttributeNotFoundException, InvalidAttributeValueException,
			MBeanException, ReflectionException, IOException {
		Attribute attribute = new Attribute(attributeName, unwrap(value));
		getMBeanServerConnection().setAttribute(objectName, attribute);
	}

	public void setAttributes(ObjectName objectName, Map<String, Object> map)
			throws InstanceNotFoundException, ReflectionException, IOException {
		AttributeList attributeList = new AttributeList(map.size());
		for (Entry<String, Object> entry : map.entrySet()) {
			attributeList.add(new Attribute(entry.getKey(), unwrap(entry
					.getValue())));
		}
		getMBeanServerConnection().setAttributes(objectName, attributeList);
	}

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

	public boolean equals(Object other) {
		if (other instanceof MBeanHome) {
			MBeanHome mhother = (MBeanHome) other;
			return url.equals(mhother.url);
		}
		return false;
	}

	private static MBeanAttributeInfoFilter getPropertiesFilter = new MBeanAttributeInfoFilter() {
		public boolean accept(MBeanAttributeInfo attributeInfo) {
			return attributeInfo.isReadable()
					&& attributeInfo.getDescriptor()
							.getFieldValue("deprecated") == null;
		}
	};

	public Map<String, ?> getProperties(ObjectName objectName,
			MBeanAttributeInfoFilter filter, boolean attributeCapitalisation)
			throws InstanceNotFoundException, IntrospectionException,
			AttributeNotFoundException, ReflectionException, MBeanException,
			IOException {
		if (filter == null)
			filter = getPropertiesFilter;
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (MBeanAttributeInfo attribute : getInfo(objectName).getAttributes()) {
			if (filter.accept(attribute)) {
				String name = attribute.getName();
				try {
					// TODO getAttributes (in bulk)?
					Object value = getAttribute(objectName, name);
					if (attributeCapitalisation)
						name = MBeanHome.decapitalise(name);
					map.put(name, value);
				} catch (RuntimeMBeanException e) {
					// TODO proper logging
					System.err.println("MBeanHome getProperties " + objectName
							+ " " + attribute.getName()
							+ " RuntimeMBeanException: "
							+ e.getCause().getMessage());
				}
			}
		}
		return map;
	}
}
