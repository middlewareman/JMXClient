/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.groovy.util

import java.util.regex.Pattern

/**
 * Removes superfluous {@link StackTraceElement}s from an exception.
 * 
 * @author Andreas Nyberg
 */
class StackTraceCleaner {

	static final StackTraceCleaner defaultInstance = new StackTraceCleaner()

	static final String defaultGroovyExcludes = /groovy\..*|org\.codehaus\.groovy\..*|java\..*|javax\..*|sun\..*|gjdk\.groovy\..*/
	static final String defaultWeblogicExcludes = /weblogic.*\.internal\..*/

	/** Always include elements with matching className. */
	private final Pattern include

	/** Exclude elements with matching className unless explicitly included. */
	private final Pattern exclude

	private StackTraceCleaner() {
		this.include = ~''
		this.exclude = ~"$defaultGroovyExcludes|$defaultWeblogicExcludes"
	}

	StackTraceCleaner(String include, String exclude) {
		this.include = ~include
		this.exclude = ~exclude
	}

	/**
	 * Cleans a throwable (but not its causes).
	 * @param throwable
	 * @return the number of stack trace elements removed
	 */
	int shallowClean(Throwable throwable) {
		StackTraceElement[] before = throwable.getStackTrace()
		StackTraceElement[] after = before.findAll { StackTraceElement ste ->
			ste.className ==~include || !(ste.className ==~ exclude)
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
