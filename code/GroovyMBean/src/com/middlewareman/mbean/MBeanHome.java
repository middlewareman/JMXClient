package com.middlewareman.mbean;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public abstract class MBeanHome {

	private static final Object[] NOARGS = new Object[0];

	static Object[] argsArray(Object obj) {
		if (obj == null)
			return NOARGS;
		else if (obj instanceof Object[])
			return (Object[]) obj;
		else
			return new Object[] { obj };
	}

	public boolean assertRegistered = true;

	public abstract MBeanServerConnection getMBeanServerConnection();

	protected MBean createMBean(ObjectName objectName) {
		return new MBean(this, objectName);
	}

	public MBean getMBean(ObjectName objectName)
			throws InstanceNotFoundException, IOException {
		if (assertRegistered) {
			if (!getMBeanServerConnection().isRegistered(objectName))
				throw new InstanceNotFoundException(objectName.toString());
		}
		return createMBean(objectName);
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
		else if (unwrapped instanceof ObjectName[]) {
			ObjectName[] objectNames = (ObjectName[]) unwrapped;
			MBean[] wrapped = new MBean[objectNames.length];
			for (int i = 0; i < objectNames.length; i++)
				if (objectNames[i] != null)
					wrapped[i] = createMBean(objectNames[i]);
			return wrapped;
		} else if (unwrapped instanceof Object[]) {
			Object[] objects = (Object[]) unwrapped;
			Object[] wrapped = new Object[objects.length]; // TODO Or reuse?
			for (int i = 0; i < objects.length; i++)
				wrapped[i] = wrap(objects[i]);
			return wrapped;
		} else if (unwrapped instanceof ObjectName)
			return createMBean((ObjectName) unwrapped);
		else
			return unwrapped;
	}

	private Object unwrap(Object wrapped) {
		if (wrapped == null)
			return null;
		else if (wrapped instanceof MBean[]) {
			MBean[] mbeans = (MBean[]) wrapped;
			ObjectName[] objectNames = new ObjectName[mbeans.length];
			for (int i = 0; i < mbeans.length; i++)
				if (mbeans[i] != null)
					objectNames[i] = mbeans[i].objectName;
			return objectNames;
		} else if (wrapped instanceof Object[]) {
			Object[] objects = (Object[]) wrapped;
			Object[] unwrapped = new Object[objects.length]; // TODO Or reuse?
			for (int i = 0; i < objects.length; i++) {
				unwrapped[i] = unwrap(objects[i]);
			}
			return unwrapped;
		} else if (wrapped instanceof MBean)
			return ((MBean) wrapped).objectName;
		else
			return wrapped;
	}

}
