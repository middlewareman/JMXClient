package com.middlewareman.mbean;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public abstract class MBeanHome {

	public abstract MBeanServerConnection getMBeanServerConnection();

	public Object invokeOperation(ObjectName objectName, String operationName,
			Object args) throws InstanceNotFoundException, MBeanException,
			ReflectionException, IOException {
		Object[] params = null; // TODO
		String[] signature = null; // TODO
		return invokeOperation(objectName, operationName, params, signature);
	}

	public Object invokeOperation(ObjectName objectName, String operationName,
			Object[] params, String[] signature)
			throws InstanceNotFoundException, MBeanException,
			ReflectionException, IOException {
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
		AttributeList al = getMBeanServerConnection().getAttributes(objectName,
				attributeNames);
		List<Attribute> attributes = al.asList();
		Object[] result = new Object[attributes.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = wrap(attributes.get(i).getValue());
		}
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

	public Object wrap(Object unwrapped) {
		return null; // TODO
	}

	public Object unwrap(Object wrapped) {
		return null; // TODO
	}

}
