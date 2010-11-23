package com.middlewareman.mbean.weblogic.shell

import com.middlewareman.mbean.weblogic.WebLogicMBeanHomeFactory 

class GWLSTShell {
	
	static void main(String[] args) {
		def hf = WebLogicMBeanHomeFactory.default
		args = hf.loadArguments(args)
		def binding = new Binding()
		GWLSTBindings.bind(hf,binding)	// TODO graceful
		System.err.println binding.status.call()	// TODO logging...
		
		def shell = new GroovyShell(binding)
		
		if (args && args[0] == '-e') {
			def expr = args[1]	// TODO graceful
			binding.args = args[2..-1]
			def result = shell.evaluate(expr)
			if (result != null) println result
		} else if (args && args[0] == '-f') {
			File file = new File(args[1]) // TODO graceful
			assert file.exists(), file.absolutePath
			binding.args = args[2..-1]
			def result = shell.evaluate(file)
			if (result != null) println result
		} else {
			binding.args = args
			System.in.withReader { 
				def result = shell.evaluate(it)
				if (result != null) println result
			}
		}
	}
}
