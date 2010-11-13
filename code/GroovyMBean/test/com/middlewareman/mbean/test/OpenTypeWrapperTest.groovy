package com.middlewareman.mbean.test;

import com.middlewareman.mbean.LocalMBeanHome 
import com.middlewareman.mbean.MBean 
import com.middlewareman.mbean.MBeanHome 
import com.middlewareman.mbean.type.CompositeDataWrapper 
import groovy.util.GroovyTestCase;
import java.lang.management.ManagementFactory 
import javax.management.MBeanServer 
import javax.management.openmbean.CompositeData 
import javax.management.openmbean.CompositeDataSupport 
import javax.management.openmbean.CompositeType 
import javax.management.openmbean.OpenType 
import javax.management.openmbean.SimpleType 

class OpenTypeWrapperTest extends GroovyTestCase {
	
	
	CompositeData simpleComposite() {
		def ct = new CompositeType('mySimpleCompositeType','mySimpleCompositeTypeDescription',
				['mySimpleInteger']as String[],
				[
					'mySimpleIntegerDescription']
				as String[],
				[SimpleType.INTEGER]as OpenType[])
		return new CompositeDataSupport(ct,[mySimpleInteger:42])
	}
	
	CompositeData nestedComposite() {
		CompositeData sc = simpleComposite()
		def ct = new CompositeType('myNestedCompositeType','myNestedCompositeTypeDescription',
				[
					'myOtherSimpleInteger',
					'mySimpleComposite'
				]
				as String[],
				[
					'myOtherSimpleIntegerDescription',
					'mySimpleCompositeDescription'
				]
				as String[],
				[
					SimpleType.INTEGER,
					sc.compositeType
				]
				as OpenType[])
		return new CompositeDataSupport(ct,[myOtherSimpleInteger:42,mySimpleComposite:sc])
	}
	
	def getMan() {
	}
	
	void testCompositeDataWrapper(CompositeDataWrapper cdw) {
		println cdw
		println cdw.properties
	}
	
	void testWrapSimpleComposite() {
		def sc = simpleComposite()
		println "simpleComposite\n\t$sc"
		def scw = new CompositeDataWrapper(sc)
		testCompositeDataWrapper scw
	}
	
	void testWrapNestedComposite() {
		def nc = nestedComposite()
		println "nestedComposite\n\t$nc"
		def ncw = new CompositeDataWrapper(nc)
		testCompositeDataWrapper ncw
	}
}
