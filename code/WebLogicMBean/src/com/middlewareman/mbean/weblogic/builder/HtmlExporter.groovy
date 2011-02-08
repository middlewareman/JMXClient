/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic.builder

import groovy.xml.MarkupBuilder

import javax.management.*

import com.middlewareman.mbean.MBean
import com.middlewareman.mbean.info.*
import com.middlewareman.mbean.type.*
import com.middlewareman.mbean.util.MBeanProperties
import com.middlewareman.mbean.weblogic.info.WebLogicMBeanInfoCategory

/**
 * Generates an HTML report from an MBean or MBeanInfo.
 * 
 * @author Andreas Nyberg
 */
class HtmlExporter {

	final MarkupBuilder html

	HtmlExporter() {
		html = new MarkupBuilder()
	}

	HtmlExporter(PrintWriter pw) {
		html = new MarkupBuilder(pw)
	}

	HtmlExporter(MarkupBuilder mb) {
		html = mb
	}

	AttributeFilter attributeFilter = SimpleAttributeFilter.verbose
	DescriptorFilter descriptorFilter = new SimpleDescriptorFilter()	// TODO

	boolean operationInfoFilter(MBeanOperationInfo oi) {
		true
	}

	boolean constructorInfoFilter(MBeanConstructorInfo ci) {
		true
	}

	boolean notificationInfoFilter(MBeanNotificationInfo ni) {
		true
	}

	static void notice(delegate) {
		delegate.p {
			mkp.yield 'This page is generated using '
			a(href:'http://www.middlewareman.com/gwlst', title:'Groovy WebLogic Scripting Tool', 'GWLST')
			mkp.yield '.'
		}
	}

	void mbean(MBean mbean, Map extras = null) {
		long time0 = System.currentTimeMillis()
		def info = mbean.info
		html.html {
			head {
				title 'GWLST MBean Browser'
				link(rel:"stylesheet", type:"text/css", title:"Style", href:"style.css")
			}
			body {
				notice delegate
				h1 'GWLST MBean Browser'
				if (extras) {
					table('class':'properties') {
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
				blockquote {
					mkp.yieldUnescaped(info.description)	// TODO dangerous?
				}
				h2 'Attributes'
				long timeBeforeAttr = System.currentTimeMillis()
				attributes mbean, info, delegate
				long timeAfterAttr = System.currentTimeMillis()
				h2 'Operations'
				operations info, delegate
				h2 'Constructors'
				constructors info, delegate
				h2 'Notifications'
				notifications info, delegate
				h2 'Done'
				long timeEnd = System.currentTimeMillis()
				div "Done in ${timeEnd - time0} ms"
			}
		}
	}

	void mbean(String interfaceClassName, MBeanInfo info, subtypes) {
		long time0 = System.currentTimeMillis()
		html.html {
			head {
				title 'GWLST MBeanInfo Browser'
				link(rel:"stylesheet", type:"text/css", title:"Style", href:"style.css")
			}
			notice delegate
			h1 'GWLST MBean Type Browser'
			h2 'InterfaceClassName'
			pre interfaceClassName
			body {
				h2 'Description'
				blockquote {
					mkp.yieldUnescaped(info.description)	// TODO dangerous?
				}
				h2 'Attributes'
				attributes info, delegate
				h2 'Constructors'
				constructors info, delegate
				h2 'Operations'
				operations info, delegate
				h2 'Notifications'
				notifications info, delegate
				h2 'Subtypes'
				ul {
					for (type in subtypes) {
						li {
							a(href:"?interfaceClassName=$type") { pre type }
						}
					}
				}
				h2 'Done'
				div "Done in ${System.currentTimeMillis() - time0} ms"
			}
		}
	}

	void attributes(MBean mbean, MBeanInfo info, delegate) {
		def props = MBeanProperties.get(mbean.@home, mbean.@objectName, attributeFilter)
		def names = props.keySet().sort()
		delegate.table('class':'properties') {
			tr {
				th 'Name'
				th 'Descriptor'
				th 'Type'
				th 'ActualType'
				th 'Value'
			}
			for (key in names) {
				def ai = info.attributes.find { it.name == key }
				def val = props[key]
				tr {
					td { name ai, delegate }
					td { descriptor ai, delegate }
					td { type ai, delegate }
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
		def ais = info.attributes.findAll { attributeFilter.acceptAttribute(it) }.sort { it.name }
		delegate.table('class':'properties') {
			tr {
				th 'Name'
				th 'Descriptor'
				th 'Type'
			}
			for (ai in ais) {
				tr {
					td { name ai, delegate }
					td { descriptor ai, delegate }
					td { type ai, delegate }
				}
			}
		}
	}

	void operations(MBeanInfo info, delegate) {
		def of = new SimpleOperationFilter()
		delegate.table('class':'properties') {
			tr {
				th 'ReturnType'
				th 'Name'
				th 'Descriptor'
				th 'Parameters'
			}
			for (op in info.operations.findAll(of.&accept).sort(of)) {
				tr {
					td { type op, delegate }		// TODO
					td { name op, delegate }
					td { descriptor op, delegate }
					td { signature op.signature, delegate }
				}
			}
		}
	}

	void constructors(MBeanInfo info, delegate) {
		delegate.table('class':'properties') {
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
			delegate.table('class':'properties') {
				tr {
					th 'Type'
					th 'Descriptor'
					th 'Name'
				}
				for (pi in pis) {
					tr {
						td { type pi, delegate }
						td { descriptor pi, delegate }
						td { name pi, delegate }
					}
				}
			}
		}
	}

	void notifications(MBeanInfo info, delegate) {
		delegate.table('class':'properties') {
			tr {
				th 'Name'
				th 'Descriptor'
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
		use(WebLogicMBeanInfoCategory) {
			// TODO Global solution?
			for (fieldName in info.descriptor.fieldNames) {
				if (!descriptorFilter || descriptorFilter.accept(info,fieldName))
					delegate.div title:info[fieldName].inspect(), fieldName
			}
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
				case CompositeDataWrapper:
					map value.properties, delegate
					break
				case TabularDataWrapper:
					map value.properties, delegate
					break
				default:
					String str = String.valueOf(value)
					String str2 = (str =~ wordBreaks).replaceAll('$1\u200B')
					delegate.div title:value.getClass().getName(), str2
			}
		}
	}

	private wordBreaks = ~/(\W+)/ // ~/(;|:|,)/ // C:\\.*;|

	void array(Object[] array, delegate) {
		if (array) {
			delegate.table('class':'propeties') {
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
			delegate.table('class':'properties') {
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
		delegate.div 'class':'error', title:sw, e.message
	}

	void mbean(MBean mbean, delegate) {
		delegate.a(href:"?objectName=${mbean.@objectName}") { pre mbean.@objectName }
	}

	// TODO
	void type(String name, delegate) {
		def tf = new TypeFacade(name)
		delegate.pre title:name, tf.shortString
	}

	void type(MBeanFeatureInfo fi, delegate) {
		def tf = new InterfaceTypeFacade(fi)
		delegate.a(href:"?interfaceClassName=$tf.longName") {
			pre title:tf.originalTypeName, tf.shortString
		}
	}

	void type(Class clazz, delegate) {
		delegate.pre title:clazz.name, clazz.simpleName
	}
}
