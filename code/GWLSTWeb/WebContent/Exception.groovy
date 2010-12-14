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
	sw.withPrintWriter { pw ->
		t.printStackTrace(pw)
	}
	return sw.toString()
}

html.html {
	head { title 'GWLST Error' }
	body {
		h2 'Status code'
		pre request.'javax.servlet.error.status_code' ?: 'Not available'
		h2 'Exception type'
		pre request.'javax.servlet.error.exception_type'?.name ?: 'Not available' 
		h2 'URI'
		pre request.'javax.servlet.error.request_uri' ?: 'Not available'
		h2 'Servlet name'
		pre request.'javax.servlet.error.servlet_name' ?: 'Not available'
		h2 'Message'
		pre request.'javax.servlet.error.message' ?: 'No message provided'
		h2 'Exception'
		Throwable t = request.'javax.servlet.error.exception'
		if (t) {
			int removed = new StackTraceCleaner().deepClean(t)
			def brief = toString(t)
			div {
				blockquote { pre brief }
				div "Removed $removed stack trace elements"
			}
		} else {
			pre 'Not available'
		}
	}
}