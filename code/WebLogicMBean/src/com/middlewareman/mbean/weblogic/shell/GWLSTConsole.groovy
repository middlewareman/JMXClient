/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic.shell

import com.middlewareman.mbean.weblogic.*
import groovy.ui.Console

class GWLSTConsole {
	
	static void main(String[] args) {
		def hf = WebLogicMBeanHomeFactory.default
		hf.loadEnvironmentProperties()
		hf.loadSystemProperties()
	
		args = hf.loadArguments(args)

		def binding = new Binding()
		GWLSTBindings.bind(hf,binding)
		def console = new Console(binding)
		console.run()
	}
}
