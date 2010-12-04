package com.middlewareman.mbean.type;

import com.middlewareman.mbean.type.CompositeDataWrapper 
import com.middlewareman.mbean.type.TabularDataWrapper;

import groovy.util.GroovyTestCase;
import javax.management.openmbean.CompositeData 
import javax.management.openmbean.CompositeDataSupport 
import javax.management.openmbean.CompositeType 
import javax.management.openmbean.OpenType 
import javax.management.openmbean.SimpleType 
import javax.management.openmbean.TabularData 
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType 

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
	
	
	void testComposite(CompositeDataWrapper cdw) {
		println cdw
		println cdw.properties
	}
	
	void testSimpleComposite() {
		def sc = simpleComposite()
		println "simpleComposite\n\t$sc"
		def scw = new CompositeDataWrapper(sc)
		testComposite scw
	}
	
	void testNestedComposite() {
		def nc = nestedComposite()
		println "nestedComposite\n\t$nc"
		def ncw = new CompositeDataWrapper(nc)
		testComposite ncw
	}
	
	TabularData keyValueTable() {
		def ct = new CompositeType('myKeyValueCompositeType','myKeyValueCompositeTypeDescription',
				['key', 'value']as String[],
				[
					'keyDescription',
					'valueDescription']
				as String[],
				[
					SimpleType.STRING,
					SimpleType.STRING]
				as OpenType[])
		def tt = new TabularType('myKeyValueTabularType','myKeyValueTabularTypeDescription',ct,['key']as String[])
		def td = new TabularDataSupport(tt)
		def cd = new CompositeDataSupport(ct,['key':'myKey','value':'myValue'])
		td.put cd
		return td
	}
	
	void testKeyValueTable() {
		def td = keyValueTable()
		println td
		assert td.get(['myKey'] as Object[]).value == 'myValue'
		def tdw = new TabularDataWrapper(td)
		assert tdw.get('myKey') == 'myValue'
		assert tdw.myKey == 'myValue'
		assert tdw['myKey'] == 'myValue'
		assert tdw.get(key:'myKey') == 'myValue'
		assert tdw.get(key:'myKey', value:'myValue') == 'myValue'
		assert tdw.get(key:'myKey', value:'notMyValue') == null
		assert tdw.get(notKey:'myKey') == null
		assert tdw.get(notKey:'myKey',key:'myKey') == 'myValue'
	}
	
	// TODO: Test key, key, value, value
}
