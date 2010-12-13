/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.groovy

import java.util.regex.Pattern 

/**
 * Removes superfluous {@link StackTraceElement}s from an exception.
 * 
 * @author Andreas Nyberg
 */
class StackTraceCleaner {
	
	static final String groovyExcludes = /groovy\..*|org\.codehaus\.groovy\..*|java\..*|javax\..*|sun\..*|gjdk\.groovy\..*/
	static final String weblogicExcludes = /weblogic.*\.internal\..*/
	
	/** Always include elements with matching className. */
	Pattern classNameInclude = ~''
	
	/** Exclude elements with matching className unless explicitly included. */
	Pattern classNameExclude = ~"$groovyExcludes|$weblogicExcludes"
	
	int shallowClean(Throwable throwable) {
		StackTraceElement[] before = throwable.getStackTrace()
		StackTraceElement[] after = before.findAll { StackTraceElement ste ->
			ste.className ==~classNameInclude || !(ste.className ==~ classNameExclude)
		}
		int removed = before.length - after.length
		if (removed) throwable.setStackTrace after
		return removed
	}
	
	/** 
	 * Cleans throwable and all its causes.
	 * @param top 
	 * @return the number of stack trace elements removed
	 */
	int deepClean(Throwable top) {
		int removed = 0
		Throwable cause = top
		while (cause) {
			removed += shallowClean(cause)
			cause = cause.cause
		}
		return removed
	}
}
