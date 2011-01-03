/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.type

import java.util.regex.Matcher 
import javax.management.MBeanAttributeInfo 
import javax.management.openmbean.OpenType


/**
 * Interpreter of type specifications that provides classification and readable string representation.
 * 
 * @author Andreas Nyberg
 */
class TypeFacade {
	
	static enum Type { 
		PRIMITIVE, OBJECT, MBEAN
	}
	
	private static final primitiveRE = ~/^(boolean|byte|char|double|float|int|long|short|void)((?:\[\])*)$/
	private static final classRE = ~/^(\w+(?:\.\w+)*)(?:\$\w+(?:\.\w+)*)*((?:\[\])*)$/
	private static final binPrimitiveRE = /(\[*)(Z|B|C|D|F|I|J|S)$/
	private static final binClassRE = ~/^(\[*)L(\w+(?:\.\w+)*);$/
	private static final objectNameRE = ~/^\[*Ljavax\.management\.ObjectName;$|^javax\.management\.ObjectName(\[\])*$/
	
	private static final allowed = OpenType.ALLOWED_CLASSNAMES_LIST
	
	private static final Map<String,String> binMap = [
		'Z':'boolean',
		'B':'byte',
		'C':'char',
		'F':'float',
		'I':'int',
		'J':'long',
		'S':'short']
	
	//private static final objectNameName = ObjectName.class.name
	//private static final mbeanName = MBean.class.name
	
	final String originalTypeName
	Type type
	String typeName
	final int dim
	
	TypeFacade(MBeanAttributeInfo ai) {
		this(ai.type)
	}
	
	TypeFacade(String spec) {
		this.originalTypeName = spec
		Matcher matcher
		if ((matcher = spec =~ primitiveRE)) {
			this.type = Type.PRIMITIVE
			this.typeName = matcher.group(1)
			this.dim = matcher.group(2).size()/2
		} else if ((matcher = spec =~ classRE)) {
			this.type = (spec ==~ objectNameRE) ? Type.MBEAN : Type.OBJECT
			this.typeName = matcher.group(1)
			this.dim = matcher.group(2).size()/2
		} else if ((matcher = spec =~ binPrimitiveRE)) {
			this.type = Type.PRIMITIVE
			this.typeName = binMap[matcher.group(2)]
			this.dim = matcher.group(1).size()
		} else if ((matcher = spec =~ binClassRE)) {
			this.type = (spec ==~ objectNameRE) ? Type.MBEAN : Type.OBJECT
			this.typeName = matcher.group(2)
			this.dim = matcher.group(1).size()
		} else {
			assert false, typeName
		}
	}
	
	//	Type getType() { 
	//		type
	//	}
	//	
	//	String getTypeName() { 
	//		typeName
	//	}
	
	boolean isPrimitive() { 
		type == Type.PRIMITIVE
	}
	
	boolean isArray() {
		dim > 0
	}
	
	boolean isCertainlySimple() {
		type == Type.OBJECT && typeName in allowed
	}
	
	boolean isCertainlyMBean() { 
		type == Type.MBEAN
	}
	
	boolean isMaybeMBean() {
		type == Type.OBJECT && !(typeName in allowed)
	}
	
	boolean isCertainlyNotMBean() {
		type == Type.PRIMITIVE || isCertainlySimple()
	}
	
	List getStats() {
		def buf = []
		if (isPrimitive()) buf << 'primitive'
		if (isArray()) buf << 'array'
		if (isCertainlySimple()) buf << 'certainlySimple'
		if (isCertainlyMBean()) buf << 'certainlyMBean'
		if (isMaybeMBean()) buf << 'maybeMBean'
		if (isCertainlyNotMBean()) buf << 'certainlyNotMBean'
		return buf
	}
	
	String getLongName() {
		isCertainlyMBean() ? 'MBean' : typeName
	}
	
	String getShortName() {
		isCertainlyMBean() ? 'MBean' : longName.substring(longName.lastIndexOf('.')+1)
	}
	
	String getLongString() {
		longName + '[]' * dim
	}
	
	String getShortString() {
		shortName + '[]' * dim
	}
	
	String toString() {
		longString
	}
}

