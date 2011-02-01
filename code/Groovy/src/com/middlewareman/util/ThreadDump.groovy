/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.util

@Immutable
class ThreadDump implements Comparable<ThreadDump>{
	String name
	String extra
	String status
	String action
	List<String> stack

	String toString() {
		"$status\t$name\t$action\t${stack.size()}"
	}

	int compareTo(ThreadDump that) {
		this.stack <=> that.stack ?: this.status <=> that.status ?: this.action <=> that.action ?: this.name <=> that.name
	}
}