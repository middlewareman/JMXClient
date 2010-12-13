/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */

/**
 * Error page to be used from web.xml.
 * @author Andreas Nyberg
 */

import com.middlewareman.groovy.StackTraceCleaner 

String toString(Throwable t) {
	def sw = new StringWriter()
	sw.withPrintWriter { pw -> t.printStackTrace(pw) }
	return sw.toString()
}

Throwable t = request.'javax.servlet.error.exception' ?: new Throwable("Test Exception.groovy Throwable")
def full = toString(t)
int removed = new StackTraceCleaner().deepClean(t)
def brief = toString(t)

html.html {
	head { title 'Groovy Exception' }
	body {
		h1 t.class.name
		h2 'URI'
		pre request.'javax.servlet.error.request_uri' ?: 'testURI'
		h2 'Message'
		pre request.'javax.servlet.error.message' ?: 'testMessage'
		h2 'Exception brief'
		div {
			blockquote { pre brief }
			div "Removed $removed stack trace elements"
		}
		h2 'Exception full'
		blockquote { pre full }
	}
}