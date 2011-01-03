/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.groovy.util;

class IndentPrintWriterTest extends GroovyTestCase {

	void testIndent() {
		def ipw = new IndentPrintWriter()
		ipw.println "From outside"
		ipw.indent("Header {", "} // footer") {
			ipw.print "partial "
			ipw.println "rest"
			ipw.indent { ipw.println "next level" }
		}
	}
	
	void testIndentNewline() {
		def ipw = new IndentPrintWriter()
		ipw.indent('HeadNewLineBegin') {
			ipw.println 'Next\nLevel'
		}
	}
}
