/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.meta;

import groovy.lang.MetaMethod;
import groovy.lang.MetaObjectProtocol;
import groovy.lang.MetaProperty;

import java.util.List;

import javax.management.MBeanInfo;

/**
 * MetaObjectProtocol part of experimental Groovy MetaClass per MBean (or
 * MBeanInfo or MBean type).
 * 
 * @author Andreas Nyberg
 */
public class MBeanMetaObjectProtocol implements MetaObjectProtocol {

	public MBeanMetaObjectProtocol(MBeanInfo info) {

	}

	@Override
	public List<MetaProperty> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MetaMethod> getMethods() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MetaMethod> respondsTo(Object obj, String name,
			Object[] argTypes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MetaMethod> respondsTo(Object obj, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetaProperty hasProperty(Object obj, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetaProperty getMetaProperty(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetaMethod getStaticMetaMethod(String name, Object[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetaMethod getMetaMethod(String name, Object[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class getTheClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object invokeConstructor(Object[] arguments) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object invokeMethod(Object object, String methodName,
			Object[] arguments) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object invokeMethod(Object object, String methodName,
			Object arguments) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object invokeStaticMethod(Object object, String methodName,
			Object[] arguments) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getProperty(Object object, String property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProperty(Object object, String property, Object newValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getAttribute(Object object, String attribute) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(Object object, String attribute, Object newValue) {
		// TODO Auto-generated method stub

	}

}
