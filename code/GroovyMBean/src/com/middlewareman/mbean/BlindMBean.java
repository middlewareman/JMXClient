package com.middlewareman.mbean;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;

import java.io.IOException;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.codehaus.groovy.runtime.InvokerHelper;

public class BlindMBean extends MBean implements GroovyObject {

	private transient MetaClass metaClass;

	public boolean attributeCapitalisation = true;

	public BlindMBean(MBeanHome home, ObjectName objectName) {
		super(home, objectName);
		this.metaClass = InvokerHelper.getMetaClass(this.getClass());
	}

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

	public Object getProperty(String propertyName) {
		try {
			if (propertyName.equals("properties"))
				return getProperties();
			if (attributeCapitalisation)
				propertyName = MBeanHome.capitalise(propertyName);
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

	public void setProperty(String propertyName, Object value) {
		if (attributeCapitalisation)
			propertyName = MBeanHome.capitalise(propertyName);
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

	public Map<String, ?> getProperties() throws InstanceNotFoundException,
			IntrospectionException, AttributeNotFoundException,
			ReflectionException, MBeanException, IOException {
		return home.getProperties(objectName, null, attributeCapitalisation);
	}

}
