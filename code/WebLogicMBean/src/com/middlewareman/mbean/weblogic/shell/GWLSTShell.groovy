/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic.shell

import com.middlewareman.mbean.weblogic.WebLogicMBeanHomeFactory 

class GWLSTShell {
	
	private static String[] tail(String[] args, int start) {
		if (start >= args.length) return new String[0]
		def nsa = new String[args.length-start]
		System.arraycopy(args, start, nsa, 0, args.length-start)
		return nsa
	}
	
	static void main(String[] args) {
		def hf = WebLogicMBeanHomeFactory.default
		args = hf.loadArguments(args)
		def binding = new Binding()
		GWLSTBindings.bind(hf,binding)	// TODO graceful
		System.err.println binding.status.call()	// TODO logging...
		
		def shell = new GroovyShell(binding)
		
		if (args && args[0] == '-e') {
			def expr = args[1]	// TODO graceful
			binding.args = tail(args,2)
			def result = shell.evaluate(expr)
			if (result != null) println result
		} else if (args && args[0] == '-f') {
			File file = new File(args[1]) // TODO graceful
			assert file.exists(), file.absolutePath
			binding.args = tail(args,2)
			def result = shell.evaluate(file)
			if (result != null) println result
		} else {
			binding.args = args
			if (System.console() != null) {
				String line
				while ( (line = System.console().readLine('GWLST> ')) != null) {
					try {
						def result = shell.evaluate(line)
						if (result != null) println result
					} catch (Exception e) {
						e.printStackTrace() // TODO clean?
					}
				}
			} else {
				System.in.withReader { 
					def result = shell.evaluate(it)
					if (result != null) println result
				}
			}
		}
	}
}
