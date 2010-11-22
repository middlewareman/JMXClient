package com.middlewareman.mbean.weblogic.shell

import com.middlewareman.mbean.weblogic.*
import groovy.ui.Console

class GWLSTConsole {
	
	static void main(String[] args) {
		def hf = WebLogicMBeanHomeFactory.default
		args = hf.loadArguments(args)
		File file
		if (args) {
			file = new File(args[0])
			assert file.exists(), "First argument file $file does not exist"
			args = args[1..-1]
		}
		def binding = new Binding()
		GWLSTBindings.bind(hf,binding)
		def console = new Console(binding)
		console.run()
		if (file) console.loadScriptFile(file)
	}
}
