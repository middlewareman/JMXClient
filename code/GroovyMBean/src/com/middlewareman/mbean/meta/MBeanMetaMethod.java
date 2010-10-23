package com.middlewareman.mbean.meta;

import groovy.lang.MetaMethod;

import javax.management.MBeanOperationInfo;

import org.codehaus.groovy.reflection.CachedClass;

public class MBeanMetaMethod extends MetaMethod {

	public MBeanMetaMethod(MBeanOperationInfo oi) {
		
	}
	
	@Override
	public int getModifiers() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class getReturnType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CachedClass getDeclaringClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object invoke(Object object, Object[] arguments) {
		// TODO Auto-generated method stub
		return null;
	}

}
