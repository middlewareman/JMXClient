/* $Id$ */
package com.middlewareman.util

import java.lang.management.ManagementFactory
import java.util.regex.Pattern

class ThreadDumpAnalyzerTest extends GroovyTestCase {

	void testParseThreadMXBeanDump() {
		def sw = new StringWriter()
		ManagementFactory.threadMXBean.dumpAllThreads(true,true).each { sw.println it }
		String text = sw.toString()
		println name
		println text
		assert text
		def tda = new ThreadDumpAnalyzer()
		tda.parse text
		println tda.parsed.size()
		assert tda.parsed
		println tda.parsed[0]
		assert tda.parsed.any { it.stack }
	}

	void testJava5Snippet() {
		println "*** $name ***"
		def text = '''
"[STANDBY] ExecuteThread: '28' for queue: 'weblogic.kernel.Default (self-tuning)'" daemon prio=10 tid=0x029e3b98 nid=0x11ba in Object.wait() [0x457ff000..0x457ff970]
	at java.lang.Object.wait(Native Method)
	- waiting on <0xee3a0bb8> (a weblogic.work.ExecuteThread)
	at java.lang.Object.wait(Object.java:474)
	at weblogic.work.ExecuteThread.waitForRequest(ExecuteThread.java:156)
	- locked <0xee3a0bb8> (a weblogic.work.ExecuteThread)
	at weblogic.work.ExecuteThread.run(ExecuteThread.java:177)

'''
		def tda = new ThreadDumpAnalyzer()
		tda.parse text
		println text
		println tda.parsed.name
		assert tda.parsed.size() == 1
		println tda.parsed[0]
		println tda.parsed[0].stack
		assert tda.parsed[0].stack.size() >= 4
	}

	void testJava5Full() {
		println "*** $name ***"
		String text = new File('Java5FullThreadDump.txt').text
		assert text
		def tda = new ThreadDumpAnalyzer()
		tda.parse text
		println tda.parsed.size()
		assert tda.parsed.size() == 113
		println tda.parsed[0]
		assert tda.parsed.any { it.stack }
		new File('Java5FullThreadDump.analysed.txt').withPrintWriter { tda.report it }
	}

	void testWLS1002Snippet() {
		println "*** $name ***"
		def text = '''        
          
            "[STANDBY] ExecuteThread: '236' for queue: 'weblogic.kernel.Default (self-tuning)'" waiting for lock weblogic.work.ExecuteThread@7e7702 WAITING
          
               java.lang.Object.wait(Native Method)
          
               java.lang.Object.wait(Object.java:474)
          
               weblogic.work.ExecuteThread.waitForRequest(ExecuteThread.java:156)
          
               weblogic.work.ExecuteThread.run(ExecuteThread.java:177)
          
            '''
		def tda = new ThreadDumpAnalyzer()
		tda.parse text
		println text
		assert tda.parsed.size() == 1
		println tda.parsed[0]
		println tda.parsed[0].stack
		assert tda.parsed[0].stack.size() == 4
	}

	void testWLS1002() {
		println "*** $name ***"
		String text = new File('WLS1002CopyPastedThreadDump.txt').text
		assert text
		def tda = new ThreadDumpAnalyzer()
		tda.parse text
		println tda.parsed.size()
		Pattern namePattern = Pattern.compile(/\s*"(?:\[(?:.+)\] )?(.*)".*$/, Pattern.MULTILINE)
		assert tda.parsed.size() == 608
		println tda.parsed[0]
		assert tda.parsed.any{ it.stack }
		new File('WLS1002CopyPastedThreadDump.analysed.txt').withPrintWriter { tda.report it }
	}
}
