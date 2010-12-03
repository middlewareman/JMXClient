package com.middlewareman.mbean.weblogic.builder

import java.util.Map;
import java.util.regex.Matcher 

import com.middlewareman.mbean.MBean 
import groovy.xml.MarkupBuilder 
import javax.management.*

class HtmlExporter {
	
	MarkupBuilder html = new MarkupBuilder()
	
	boolean attributeInfoFilter(MBeanAttributeInfo ai) {
		true
	}
	
	boolean attributeValueFilter(MBeanAttributeInfo ai, def value) {
		true
	}
	
	boolean operationInfoFilter(MBeanOperationInfo oi) {
		true
	}
	
	boolean constructorInfoFilter(MBeanConstructorInfo ci) {
		true
	}
	
	boolean notificationInfoFilter(MBeanNotificationInfo ni) {
		true
	}
	
	boolean descriptorFieldNameFilter(String name) {
		true
	}
	
	void mbean(MBean mbean, Map extras = null) {
		def info = mbean.info
		html.html {
			head { title 'WebLogic MBean Browser' }
			body {
				h1 'WebLogic MBean Browser'
				if (extras) {
					table(border:'1') {
						extras.each { key, value ->
							tr {
								td key
								td { pre value }
							}
						}
					}
				}
				h2 'MBeanHome'
				pre mbean.@home
				h2 'ObjectName'
				pre mbean.@objectName
				h2 'Description'
				div mkp.yieldUnescaped(info.description)	// TODO dangerous?
				h2 'Attributes'
				attributes mbean, info, delegate
				h2 'Operations'
				operations info, delegate
				h2 'Constructors'
				constructors info, delegate
				h2 'Notifications'
				notifications info, delegate
				h2 'Done.'
			}
		}
	}
	
	void mbean(MBeanInfo info) {
		html.html {
			head { title 'MBeanInfo Browser' }
			body {
				h2 'Description'
				div mkp.yieldUnescaped(info.description)	// TODO dangerous?
				h2 'Attributes'
				attributes info, delegate
				h2 'Constructors'
				constructors info, delegate
				h2 'Operations'
				operations info, delegate
				h2 'Notifications'
				notifications info, delegate
				h2 'Done.'
			}
		}
	}
	
	void attributes(MBean mbean, MBeanInfo info, delegate) {
		def ais = info.attributes // .findAll &attributeInfoFilter
		ais.sort { it.name }
		delegate.table(border:'1') {
			tr {
				th 'Name'
				th 'Descriptor'
				th 'Type'
				th 'ActualType'
				th 'Value'
			}
			for (ai in ais) {
				if (!attributeInfoFilter(ai))
					continue
				def val
				try {
					val = mbean.getProperty(ai.name)
					if (!attributeValueFilter(ai,val)) 
						continue
				} catch(Exception e) {
					val = e
				}
				tr {
					td { name ai, delegate }
					td { descriptor ai, delegate }
					td { type ai.type, delegate }
					td {
						if (val != null) 
							type val.getClass(), delegate
					}
					td { value val, delegate }
				}
			}
		}
	}
	
	void attributes(MBeanInfo info, delegate) {
		def ais = info.attributes // .findAll &attributeInfoFilter
		ais.sort { it.name }
		delegate.table(border:'1') {
			tr {
				th 'Name'
				th 'Descriptor'
				th 'Type'
			}
			for (ai in ais) {
				if (!attributeInfoFilter(ai))
					continue
				tr {
					td { name ai, delegate }
					td { descriptor ai, delegate }
					td { type ai.type, delegate }
				}
			}
		}
	}
	
	void operations(MBeanInfo info, delegate) {
		delegate.table(border:'1') {
			tr {
				th 'ReturnType'
				th 'Name'
				th 'Descriptor'
				th 'Parameters'
			}
			for (op in info.operations) {
				tr {
					td { type op.returnType, delegate }
					td { name op, delegate }
					td { descriptor op, delegate }
					td { signature op.signature, delegate }
				}
			}
		}
	}
	
	void constructors(MBeanInfo info, delegate) {
		delegate.table(border:'1') {
			tr {
				th 'Name'
				th 'Parameters'
			}
			for (con in info.constructors) {
				if (!constructorInfoFilter(con)) 
					continue
				tr {
					td { name con, delegate }
					td { signature con.signature, delegate }
				}
			}
		}
	}
	
	void signature(MBeanParameterInfo[] pis, delegate) {
		if (pis) {
			delegate.table(border:'1') {
				for (pi in pis) {
					tr {
						td { type pi.type, delegate }
						td { descriptor pi, delegate }
						td { name pi, delegate }
					}
				}
			}
		}
	}
	
	void notifications(MBeanInfo info, delegate) {
		delegate.table(border:'1') {
			tr {
				th 'Name'
				th 'NotificationTypes'
			}
			for (ni in info.notifications) {
				if (!notificationInfoFilter(ni))
					continue
				tr {
					td { name ni, delegate }
					td { descriptor ni, delegate }
					td {
						for (nt in ni.notifTypes) pre nt
					}
				}
			}
		}
	}
	
	void name(MBeanFeatureInfo info, delegate) {
		def deprecated = info.descriptor.getFieldValue('deprecated')
		if (deprecated) {
			delegate.del {
				pre title:info.description, info.name
			}
		} else {
			delegate.pre title:info.description, info.name
		}
	}
	
	void descriptor(MBeanFeatureInfo info, delegate) {
		def names = info.descriptor.fieldNames
		def values = info.descriptor.getFieldValues(names)
		assert names.size() == values.size()
		for (i in 0..<names.length) {
			if (descriptorFieldNameFilter(names[i]))
				delegate.div title:values[i], names[i]
		}
	}
	
	void value(value, delegate) {
		if (value == null) {
			delegate.i 'null'
		} else {
			switch(value) {
				case Object[]:
					array value, delegate
					break
				case Map:
					map value, delegate
					break
				case Exception:
					exception value, delegate
					break
				case MBean:
					mbean value, delegate
					break
				default:
					delegate.pre title:value.getClass().getName(), value
			}
		}
	}
	
	void array(Object[] array, delegate) {
		if (array) {
			delegate.table(border:'1') {
				for (i in 0..<array.length) {
					def v = array[i]
					tr {
						td { pre i }
						td { value v, delegate }
					}
				}
			}
		} else {
			delegate.pre '[]'
		}
	}
	
	void map(Map map, delegate) {
		if (map) {
			delegate.table(border:'1') {
				map.each { mapKey, mapVal ->
					tr {
						td { pre mapKey }
						td { value mapVal, delegate }
					}
				}
			}
		} else {
			delegate.pre '[:]'
		}
	}
	
	void exception(Exception e, delegate) {
		def sw = new StringWriter()
		sw.withPrintWriter { e.printStackTrace it }
		delegate.i title:sw, e.message
	}
	
	void mbean(MBean mbean, delegate) {
		delegate.a(href:"?objectName=${mbean.@objectName}") { pre mbean.@objectName }
	}
	
	def primitiveRE = ~/^(boolean|byte|char|double|float|int|long|short|void)((\[\])*)$/
	def binaryClassRE = ~/^(\[*)L(.*);$/
	
	void type(String name, delegate) {
		Matcher matcher
		if (name ==~ primitiveRE) {
			delegate.pre name
		} else if ((matcher = name =~ binaryClassRE)) {
			assert matcher.hasGroup()
			def dim = matcher[0][1].size()
			String className = matcher[0][2]
			className = className.substring(className.lastIndexOf('.')+1)
			if (dim) className += '[]' * dim
			delegate.pre title:name, className
		} else {
			String className = name.substring(name.lastIndexOf('.')+1)
			delegate.pre title:name, className
		}
	}
	
	void type(Class clazz, delegate) {
		delegate.pre title:clazz.name, clazz.simpleName
	}
}
