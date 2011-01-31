/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.util

import java.util.regex.Pattern

/**
 * Parser for a thread dump. 
 * The result is a list of ThreadDump elements that can be grouped by its attributes.
 * The first such grouping is by trace as it aggregates all threads with the same
 * call stack.
 * 
 * @author Andreas Nyberg
 */
class ThreadDumpAnalyzer {

	String threadPattern = ~/^([ \t]*)"(\[(.+)\] )?(.*)" (.*)$((?:\s*^[ \t]+(?:(?:\S+\(.+\))|(?:at .+)|(?:- .+))$)*)/
	// all, spaces, statePart, state, name, state, name, action, stack

	String stackElementPattern = ~ /\s*^[ \t]+((?:at )?(\S+\(.+\))|(?:- waiting .*)|(?:- locked .*))$/  ///^((\S+\(.+\))|(at .+)|(- .+))\s*$/
	// spacesAndLocation, location

	final List<ThreadDump> parsed = []

	void parse(String text) {
		def allpat = Pattern.compile(threadPattern, Pattern.MULTILINE)
		def stackpat = Pattern.compile(stackElementPattern, Pattern.MULTILINE)
		def dumps = []
		text.eachMatch(allpat) { all ->
			String state = all[3]
			String name = all[4]
			String action = all[5]
			String stack = all[6]
			def list = []
			stack.eachMatch(stackpat) { line ->
				list.add line[1]
			}
			assert !stack || list, stack
			parsed.add new ThreadDump(name,state,action,list)
		}
	}

	void report(PrintWriter out = new PrintWriter(System.out)) {
	}

	static void sort(Map<?,Collection> source, Closure putter) {
		source.entrySet().sort { -it.value.size() }.each { key, value -> putter key, value }
	}

	/**
	 * Returns a LinkedHashMap with elements in descending order by the size of their values.
	 */
	static Map<?,Collection<ThreadDump>> sort(Map<?,Collection<ThreadDump>> source) {
		def target = new LinkedHashMap(source.size())
		source.entrySet().sort { -it.value.size() }.each { key, value -> target[key] = value }
		return target
	}

	static Map<?,Collection<ThreadDump>> top(Map<?,Collection<ThreadDump>> source) {
	}

	/**
	 * Return a new map that is a subset of the given map.
	 * @params totalFraction include only elements until the total fraction of threads are reached.
	 * @params individualFraction include only elements with number of threads greater than this fraction.
	 */
	static Map findTop(Map<?,ThreadDump> map, totalFraction, individualFraction) {
		int totalCount = map.values().inject(0) { tally, td ->
			tally + td.trace.size()
		}
		println "total count is $totalCount"
		println "finding top $totalFraction of total -> ${totalFraction*totalCount}"
		println "including only individuals $individualFraction -> ${individualFraction*totalCount}"
		def topKeys = map.keySet().sort {
			-map[it].trace.size()
		}
		int totalTally = 0
		Map<?,ThreadDump> targetMap = new LinkedHashMap()
		for (key in topKeys) {
			def td = map[key]
			int tdc = td.trace.size()
			if (!individualFraction || tdc/totalCount >= individualFraction)
				targetMap[key] = td
			totalTally += tdc
			if (totalTally/totalCount > totalFraction)
				break
		}
		return targetMap
	}
}
