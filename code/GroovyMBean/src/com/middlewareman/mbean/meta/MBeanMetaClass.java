package com.middlewareman.mbean.meta;

import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;

import java.util.List;

import javax.management.MBeanInfo;

import org.codehaus.groovy.ast.ClassNode;


public class MBeanMetaClass extends MBeanMetaObjectProtocol implements
		MetaClass {

	public MBeanMetaClass(MBeanInfo i) {
		super(i);
	}
	
	@Override
	public Object invokeMethod(Class sender, Object receiver,
			String methodName, Object[] arguments, boolean isCallToSuper,
			boolean fromInsideClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getProperty(Class sender, Object receiver, String property,
			boolean isCallToSuper, boolean fromInsideClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProperty(Class sender, Object receiver, String property,
			Object value, boolean isCallToSuper, boolean fromInsideClass) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object invokeMissingMethod(Object instance, String methodName,
			Object[] arguments) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object invokeMissingProperty(Object instance, String propertyName,
			Object optionalValue, boolean isGetter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAttribute(Class sender, Object receiver,
			String messageName, boolean useSuper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(Class sender, Object receiver, String messageName,
			Object messageValue, boolean useSuper, boolean fromInsideClass) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public ClassNode getClassNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MetaMethod> getMetaMethods() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int selectConstructorAndTransformArguments(int numberOfConstructors,
			Object[] arguments) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MetaMethod pickMethod(String methodName, Class[] arguments) {
		// TODO Auto-generated method stub
		return null;
	}

}
