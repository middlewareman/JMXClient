/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.util

@Immutable
class ThreadDump implements Comparable<ThreadDump>{
	String name
	String state
	String action
	List<String> trace

	String mytoString() {
		"$state \t$action \t$name"
	}

	int compareTo(ThreadDump that) {
		this.trace <=> that.trace ?: this.state <=> that.state ?: this.action <=> that.action ?: this.name <=> that.name
	}
}