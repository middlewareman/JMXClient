/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.util

import java.util.regex.Pattern

import com.middlewareman.groovy.util.IndentPrintWriter

/**
 * Parser for a thread dump. 
 * The result is a list of ThreadDump elements that can be grouped by its attributes.
 * The first such grouping is by trace as it aggregates all threads with the same
 * call stack.
 * 
 * @author Andreas Nyberg
 */
class ThreadDumpAnalyzer {

	String threadPattern = ~/^([ \t]*)"(\[(.+)\] )?(.*)" (.*(?:\w+=\w+ )+)?(runnable.*|RUNNABLE.*|waiting.*|TIMED_WAITING.*|WAITING.*|in.*)$((?:\s*^[ \t]+(?:(?:\S+\(.+\))|(?:at .+)|(?:- .+))$)*)/

	String stackElementPattern = ~ /\s*^[ \t]+((?:at )?(\S+\(.+\))|(?:- waiting .*)|(?:- locked .*))$/  ///^((\S+\(.+\))|(at .+)|(- .+))\s*$/

	final List<ThreadDump> parsed = []

	void parse(String text) {
		def allpat = Pattern.compile(threadPattern, Pattern.MULTILINE)
		def stackpat = Pattern.compile(stackElementPattern, Pattern.MULTILINE)
		def dumps = []
		text.eachMatch(allpat) { all ->
			//all.eachWithIndex { item, ind -> println "all[$ind]:\t$item" }
			String state = all[3]
			String name = all[4]
			String extra = all[5]
			String action = all[6]
			String stack = all[7]
			def list = []
			stack.eachMatch(stackpat) { line ->
				list.add line[1]
			}
			assert !stack || list, stack
			parsed.add new ThreadDump(name,extra,state,action,list)
		}
	}

	private fraction(int numerator, int denomenator) {
		"$numerator of $denomenator = ${numerator*100/denomenator} %"
	}

	void report(out = System.out) {
		def ipw = new IndentPrintWriter(out)
		int total = parsed.size()

		ipw.indent('BY ACTION (more than one thread)','\n') {
			topBy { it.action }.each { action, byAction ->
				if (byAction.size() > 1)
					ipw.indent("\n${fraction(byAction.size(), total)}:\t$action") {
						byAction.sort { it.name }.each { ipw.println "[$it.status] $it.name" }
					}
			}
		}

		ipw.indent('BY STACK','\n') {
			topBy { it.stack }.each { stack, byStack ->
				ipw.indent("\n${fraction(byStack.size(),total)}") {
					byStack.sort { it.name }.each { ipw.println "[$it.status] $it.name ($it.action)" }
					ipw.indent('STACK') {
						stack.each { ipw.println it }
					}
				}
			}
		}
	}

	Map topBy(Closure cut) {
		topBy(parsed, cut)
	}

	static Map topBy(List list, Closure cut) {
		sort(list.groupBy(cut))
	}

	/**
	 * Returns a LinkedHashMap with elements in descending order by the size of their values.
	 */
	static Map<?,Collection<ThreadDump>> sort(Map<?,Collection<ThreadDump>> source) {
		def target = new LinkedHashMap(source.size())
		source.keySet().sort {
			-source[it].size()
		}.each {
			target[it] = source[it]
		}
		assert source.size() == target.size()
		return target
	}

	/**
	 * Return a new map that is a subset of the given map.
	 * @params totalFraction include only elements until the total fraction of threads are reached.
	 * @params individualFraction include only elements with number of threads greater than this fraction.
	 */
	private static Map findTop(Map<?,Collection<ThreadDump>> map, totalFraction, individualFraction) {
		int totalCount = map.values().inject(0) { tally, Collection<ThreadDump> td ->
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
