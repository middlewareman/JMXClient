/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic.shell

import groovy.ui.Console

import javax.swing.JFrame

class ConsoleCategory {

	static frameConsoleDelegates(address) {
		[
			rootContainerDelegate:{
				frame(
				title: "Groovy WebLogic Scripting Tool $address",
				iconImage: imageIcon("/groovy/ui/ConsoleIcon.png").image,
				defaultCloseOperation: JFrame.EXIT_ON_CLOSE,
				) {
					current.locationByPlatform = true
					containingWindows += current
				}
			},
			menuBarDelegate: {arg->
				current.JMenuBar = build(arg)
			}
		]
	}

	static void updateTitle(Console self) {
		return
	}

	private static void actuallyUpdateTitle(Console self) {
		def runtimeServer = self.shell.context.runtimeServer
		def state
		try {
			state = runtimeServer.runtimeService.ServerRuntime.State
		} catch (Exception e) {
			e.printStackTrace()
			state = 'unreachable'
		}
		self.frame.title = "GWLST $runtimeServer.home.address ($state)"
	}

	static void showAbout(Console self, EventObject evt = null) {
		def version = GWLST.class.package.implementationVersion
		def pane = self.swing.optionPane()
		// TODO Clickable link to web site
		pane.message = [
			'Welcome to Groovy WebLogic Scripting Tool',
			"(version $version)",
			'http://www.middlewareman.com/gwlst'] as String[]
		def dialog = pane.createDialog(self.frame, 'About GWLST')
		dialog.show()
	}
}
