package com.middlewareman.mbean;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;

import javax.management.ObjectName;

public class MBean implements GroovyObject {

	public final ObjectName objectName;

	public MBean(ObjectName objectName) {
		this.objectName = objectName;
	}

	@Override
	public Object invokeMethod(String name, Object args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getProperty(String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProperty(String propertyName, Object newValue) {
		// TODO Auto-generated method stub
	}

	@Override
	public MetaClass getMetaClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMetaClass(MetaClass metaClass) {
		// TODO Auto-generated method stub
	}

}
