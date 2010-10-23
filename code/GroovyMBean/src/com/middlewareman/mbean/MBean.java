package com.middlewareman.mbean;

import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClass;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.codehaus.groovy.runtime.InvokerHelper;

public class MBean implements GroovyObject {

	// enum ResolveStrategy {
	// MBeanOnly, MBeanFirst, MetaOnly, MetaFirst
	// }
	// public ResolveStrategy resolveStrategy = ResolveStrategy.MBeanFirst;

	private MetaClass metaClass;

	public final MBeanHome home;
	public final ObjectName objectName;

	public MBean(MBeanHome home, ObjectName objectName) {
		this.home = home;
		this.objectName = objectName;
	}

	@Override
	public Object invokeMethod(String methodName, Object args) {
		try {
			return home.invokeOperation(objectName, methodName, args);
		} catch (InstanceNotFoundException e) {
			throw new GroovyRuntimeException(e);
		} catch (MBeanException e) {
			throw new GroovyRuntimeException(e);
		} catch (ReflectionException e) {
			throw new GroovyRuntimeException(e);
		} catch (IOException e) {
			throw new GroovyRuntimeException(e);
		}
	}

	@Override
	public Object getProperty(String propertyName) {
		try {
			return home.getAttribute(objectName, propertyName);
		} catch (AttributeNotFoundException e) {
			throw new GroovyRuntimeException(e);
		} catch (InstanceNotFoundException e) {
			throw new GroovyRuntimeException(e);
		} catch (MBeanException e) {
			throw new GroovyRuntimeException(e);
		} catch (ReflectionException e) {
			throw new GroovyRuntimeException(e);
		} catch (IOException e) {
			throw new GroovyRuntimeException(e);
		}
	}

	@Override
	public void setProperty(String propertyName, Object value) {
		try {
			home.setAttribute(objectName, propertyName, value);
		} catch (InstanceNotFoundException e) {
			throw new GroovyRuntimeException(e);
		} catch (AttributeNotFoundException e) {
			throw new GroovyRuntimeException(e);
		} catch (InvalidAttributeValueException e) {
			throw new GroovyRuntimeException(e);
		} catch (MBeanException e) {
			throw new GroovyRuntimeException(e);
		} catch (ReflectionException e) {
			throw new GroovyRuntimeException(e);
		} catch (IOException e) {
			throw new GroovyRuntimeException(e);
		}
	}

	@Override
	public synchronized MetaClass getMetaClass() {
		if (metaClass == null)
			metaClass = InvokerHelper.getMetaClass(getClass());
		return metaClass;
	}

	@Override
	public synchronized void setMetaClass(MetaClass metaClass) {
		this.metaClass = metaClass;
	}

}
