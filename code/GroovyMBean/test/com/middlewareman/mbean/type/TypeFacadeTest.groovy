/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.type

import com.middlewareman.mbean.MBean 
import javax.management.ObjectName 

class TypeFacadeTest extends GroovyTestCase {
	
	void testPrimitive() {
		testPrimitive new TypeFacade("boolean")
	}
	
	void testPrimitiveBin() {
		testPrimitive new TypeFacade(boolean.class.name)
	}
	
	void testPrimitive(TypeFacade tf) {
		assert tf.longString == "boolean"
		assert tf.shortString == "boolean"
		assert tf.isPrimitive()
		assert !tf.isArray()
		assert !tf.isCertainlySimple()
		assert !tf.isCertainlyMBean()
		assert !tf.isMaybeMBean()
		assert tf.isCertainlyNotMBean()
		assert tf.dim == 0
	}
	
	void testPrimitiveArray() {
		testPrimitiveArray new TypeFacade("int[]")
	}
	
	void testPrimitiveArrayBin() {
		testPrimitiveArray new TypeFacade(int[].class.name)
	}
	
	void testPrimitiveArray(TypeFacade tf) {
		assert tf.longString == "int[]"
		assert tf.shortString == "int[]"
		assert tf.isPrimitive()
		assert tf.isArray()
		assert !tf.isCertainlySimple()
		assert !tf.isCertainlyMBean()
		assert !tf.isMaybeMBean()
		assert tf.isCertainlyNotMBean()
		assert tf.dim == 1
	}
	
	void testSimple() {
		testSimple new TypeFacade("java.lang.String")
	}
	
	void testSimpleBin() {
		testSimple new TypeFacade(String.class.name)
	}
	
	void testSimple(TypeFacade tf) {
		assert tf.longString == "java.lang.String"
		assert tf.shortString == "String"
		assert !tf.isPrimitive()
		assert !tf.isArray()
		assert tf.isCertainlySimple()
		assert !tf.isCertainlyMBean()
		assert !tf.isMaybeMBean()
		assert tf.isCertainlyNotMBean()
		assert tf.dim == 0
	}
	
	void testSimpleArray() {
		testSimpleArray new TypeFacade("java.lang.String[][]")
	}
	
	void testSimpleArrayBin() {
		testSimpleArray new TypeFacade(String[][].class.name)
	}
	
	void testSimpleArray(TypeFacade tf) {
		assert tf.longString == "java.lang.String[][]"
		assert tf.shortString == "String[][]"
		assert !tf.isPrimitive()
		assert tf.isArray()
		assert tf.isCertainlySimple()
		assert !tf.isCertainlyMBean()
		assert !tf.isMaybeMBean()
		assert tf.isCertainlyNotMBean()
		assert tf.dim == 2
	}
	
	void testMBean() {
		testMBean new TypeFacade(ObjectName.class.name)
	}
	
	void testMBean(TypeFacade tf) {
		assert tf.typeName == "javax.management.ObjectName"
		assert tf.longString == MBean.class.name
		assert tf.shortString == "MBean"
		assert !tf.isPrimitive()
		assert !tf.isArray()
		assert !tf.isCertainlySimple()
		assert tf.isCertainlyMBean()
		assert !tf.isMaybeMBean()
		assert !tf.isCertainlyNotMBean()
		assert tf.dim == 0
	}
	
	void testOtherClass() {
		testOtherClass new TypeFacade("com.middlewareman.mbean.ConnectingMBeanHome[]")
	}
	
	void testOtherClassBin() {
		testOtherClass new TypeFacade(com.middlewareman.mbean.ConnectingMBeanHome[].class.name)
	}
	
	void testOtherClass(TypeFacade tf) {
		assert tf.longString == "com.middlewareman.mbean.ConnectingMBeanHome[]"
		assert tf.shortString == "ConnectingMBeanHome[]"
		assert !tf.isPrimitive()
		assert tf.isArray()
		assert !tf.isCertainlySimple()
		assert !tf.isCertainlyMBean()
		assert tf.isMaybeMBean()
		assert !tf.isCertainlyNotMBean()
		assert tf.dim == 1
	}
}
