/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.util

//@Immutable
class ThreadDump implements Comparable<ThreadDump> {
	final String name
	final String extra
	final String status
	final String action
	final List<String> stack

	ThreadDump(String name, String extra, String status, String action, List<String> stack) {
		this.name = name
		this.extra = extra
		this.status = status
		this.action = action
		this.stack = stack.asImmutable()
	}
	
	String toString() {
		"$status\t$name\t$action\t${stack.size()}"
	}

	int compareTo(ThreadDump that) {
		this.stack <=> that.stack ?: this.status <=> that.status ?: this.action <=> that.action ?: this.name <=> that.name
	}
}