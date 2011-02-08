package com.middlewareman.mbean.info

class SimpleOperationFilterTest extends GroovyTestCase {

	SimpleOperationFilter sof = new SimpleOperationFilter()

	void testPattern() {
		assert 'destroyJdbcSomething' ==~ sof.pattern
		def match = 'destroyJDBCSystemResource' =~ sof.pattern
		assert match
		assert match.hasGroup()
		assert match[0][1] == 'destroy'
		assert match[0][2] == 'JDBCSystemResource'
	}

	void testNames() {
		assert sof.compare('lookupNothing', 'lookupNothing') == 0
		assert sof.compare('asdfg','sdfgh') < 0
		assert sof.compare('createJMS', 'destroyJDBC') > 0
	}
	
	// TODO more tests using MXBeans
}
