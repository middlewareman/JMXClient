/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic.shell

import com.middlewareman.groovy.StackTraceCleaner 
import com.middlewareman.mbean.weblogic.WebLogicMBeanHomeFactory 

class GWLST {
	
	static usage = """\
gwlst [options]                   (read from GUI or terminal console)
gwlst [options] -e expr args...   (evaluate given expression)
gwlst [options] filename args...  (read script from file and evaluate)
gwlst [options] - args...         (read script from stdin and evaluate)
  options: -url {url} -username {username} -password {password} -timeout {timeout}
  system properties: -Dgwlst.url= -Dgwlst.username= -Dgwlst.password -Dgwlst.timeout=
  environment variables: GWLST_URL GWLST_USERNAME GWLST_PASSWORD GWLST_TIMEOUT
  groovy-all.jar and wlfullclient.jar in same directory as gwlst.jar or on classpath.
  http://www.middlewareman.com/gwlst"""
	
	static final StackTraceCleaner cleaner = new StackTraceCleaner()
	
	static void main(String[] args) {
		if (args && args[0] ==~ /-h.*|-\?/) {
			println "Usage:\n$usage"
			System.exit(0)
		}
		
		def hf = WebLogicMBeanHomeFactory.default
		hf.loadEnvironmentProperties()
		hf.loadSystemProperties()
		args = hf.loadArguments(args)
		
		def binding = new Binding()
		try {
			GWLSTBindings.bind(hf,binding)	// TODO graceful
		} catch(Exception e) {
			System.err.println "Could not connect with url=$hf.url username=$hf.username"
			cleaner.deepClean e
			e.printStackTrace System.err
		}
		def shell = new GroovyShell(binding)
		
		/* If no more arguments, start GUI Console or prompt from terminal Console. */
		if (!args) {
			// Can we start GUI?
			Console terminal = System.console()
			if (terminal != null) {
				System.err.println binding.status.call()
				String line
				while ( (line = terminal.readLine('GWLST> ')) != null) {
					try {
						def result = shell.evaluate(line)
						if (result != null) println result
					} catch (Exception e) {
						cleaner.deepClean e
						e.printStackTrace System.err
						/** Keep calm and carry on. */
					}
				}
			} else {
				System.in.withReader { evaluate shell, it }
			}
		} else if (args[0] == '-e') {
			if (args.length < 1) {
				System.err.println "ERROR: gwlst -e without expression. Usage:\$usage"
				System.exit 1
			}
			String expr = args[1]
			binding.args = tail(args,2)
			evaluate shell, expr
		} else if (args[0] == '-') {
			binding.args = tail(args,1)
			System.in.withReader { evaluate shell, it }
		} else {
			File file = new File(args[0])
			binding.args = tail(args,1)
			if (!file.canRead()) {
				System.err.println "ERROR: gwlst filename $file.absolutePath cannot be read. Usage:\n$usage"
				System.exit 1
			}
			file.withReader { evaluate shell, it }
		}
	}
	
	private static String[] tail(String[] args, int start) {
		if (start >= args.length) return new String[0]
		def nsa = new String[args.length-start]
		System.arraycopy(args, start, nsa, 0, args.length-start)
		return nsa
	}
	
	private static void evaluate(GroovyShell shell, script) {
		try {
			def result = shell.evaluate(script)
			if (result != null) println result
		} catch(Exception e) {
			cleaner.deepClean e
			e.printStackTrace System.err
			System.exit 1
		}
	}
}
