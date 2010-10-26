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

	public final MetaClass metaClass;
	public final MBeanHome home;
	public final ObjectName objectName;

	public MBean(MBeanHome home, ObjectName objectName, MetaClass metaClass) {
		this.home = home;
		this.objectName = objectName;
		this.metaClass = metaClass;
	}

	public MBean(MBeanHome home, ObjectName objectName) {
		this(home, objectName, InvokerHelper.getMetaClass(MBean.class));
	}

	public Object invokeMethod(String methodName, Object args) {
		// Object[] argss = MBeanHome.argsArray(args);
		// MetaMethod mm = getMetaClass().getMetaMethod(methodName, argss);
		// if (mm != null) {
		// return mm.invoke(this, argss);
		// } else {
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
		// }
	}

	public Object getProperty(String propertyName) {
		// MetaProperty mp = getMetaClass().getMetaProperty(propertyName);
		// if (mp != null) {
		// return mp.getProperty(this);
		// } else {
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
		// }
	}

	public void setProperty(String propertyName, Object value) {
		// MetaProperty mp = getMetaClass().getMetaProperty(propertyName);
		// if (mp != null) {
		// mp.setProperty(this, value);
		// } else {
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
		// }
	}

	public MetaClass getMetaClass() {
		return metaClass;
	}

	public void setMetaClass(MetaClass metaClass) {
		throw new UnsupportedOperationException();
	}

	public String toString() {
		return getClass().getSimpleName() + "(" + objectName + ")";
	}

	public int hashCode() {
		return objectName.hashCode();
	}

	public boolean equals(Object other) {
		if (other instanceof MBean) {
			MBean mother = (MBean) other;
			return objectName.equals(mother.objectName) && home.equals(mother.home);
		} else
			return false;
	}

	boolean asBoolean() {
		try {
			return home.getMBeanServerConnection().isRegistered(objectName);
		} catch (IOException e) {
			// TODO Log error?
			return false;
		}
	}

}
