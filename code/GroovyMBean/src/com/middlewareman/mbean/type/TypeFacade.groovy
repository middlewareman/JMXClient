/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.type

import com.middlewareman.mbean.MBean 
import java.util.regex.Matcher 
import javax.management.MBeanAttributeInfo 
import javax.management.ObjectName 
import javax.management.openmbean.OpenType


/**
 * 
 * @author Andreas Nyberg
 */
class TypeFacade {
	
	enum Type { 
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
	
	private static final objectNameName = ObjectName.class.name
	private static final mbeanName = MBean.class.name
	
	final Type type
	final String typeName
	final int dim
	
	TypeFacade(MBeanAttributeInfo ai) {
		this(ai.type)
	}
	
	TypeFacade(String typeName) {
		Matcher matcher
		if ((matcher = typeName =~ primitiveRE)) {
			this.type = Type.PRIMITIVE
			this.typeName = matcher.group(1)
			this.dim = matcher.group(2).size()/2
		} else if ((matcher = typeName =~ classRE)) {
			this.type = (typeName ==~ objectNameRE) ? Type.MBEAN : Type.OBJECT
			this.typeName = matcher.group(1)
			this.dim = matcher.group(2).size()/2
		} else if ((matcher = typeName =~ binPrimitiveRE)) {
			this.type = Type.PRIMITIVE
			this.typeName = binMap[matcher.group(2)]
			this.dim = matcher.group(1).size()
		} else if ((matcher = typeName =~ binClassRE)) {
			this.type = (typeName ==~ objectNameRE) ? Type.MBEAN : Type.OBJECT
			this.typeName = matcher.group(2)
			this.dim = matcher.group(1).size()
		} else {
			assert false, typeName
		}
	}
	
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
		type == Type.PRIMITIVE || (type == Type.OBJECT && isCertainlySimple())
	}
	
	def getStats() {
		def buf = []
		if (isPrimitive()) buf << 'primitive'
		if (isArray()) buf << 'array'
		if (isCertainlySimple()) buf << 'certainlySimple'
		if (isCertainlyMBean()) buf << 'certainlyMBean'
		if (isMaybeMBean()) buf << 'maybeMBean'
		if (isCertainlyNotMBean()) buf << 'certainlyNotMBean'
		return buf.join('\t')
	}
	
	String getLongName() {
		isCertainlyMBean() ? mbeanName : typeName
	}
	
	String getShortName() {
		longName.substring(longName.lastIndexOf('.')+1)
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

