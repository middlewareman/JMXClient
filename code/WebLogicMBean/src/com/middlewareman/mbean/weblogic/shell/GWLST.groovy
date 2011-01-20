/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic.shell

import java.util.logging.*

import com.middlewareman.groovy.util.StackTraceCleaner
import com.middlewareman.mbean.weblogic.WebLogicMBeanHomeFactory

/**
 * DSL launcher for GWLST scripts.
 * Consumes environment variables, system properties and command line parameters
 * and executes a GWLST script in a Groovy shell containing bindings used by
 * the script.
 * 
 * @author Andreas Nyberg
 */
class GWLST {

	static final Logger logger = Logger.getLogger(GWLST.class.name)

	static usage = """\
Groovy WebLogic Scripting Tool version ${GWLST.class.package.implementationVersion}
http://www.middlewareman.com/gwlst
Usage:
  java [options] -jar gwlst.jar [flags] [-console [args...]] 
  java [options] -jar gwlst.jar [flags] -e expr [args...]     (evaluate given expression)
  java [options] -jar gwlst.jar [flags] filename [args...]    (read script from file and evaluate)
  java [options] -jar gwlst.jar [flags] - [args...]           (read script from stdin and evaluate)
    environment: GWLST_URL GWLST_USERNAME GWLST_TIMEOUT GWLST_LOGLEVEL
    options:     -Dgwlst.url= -Dgwlst.username= -Dgwlst.password -Dgwlst.timeout= -Dgwlst.loglevel=
    flags:       -url {url} -username {username} -password {password} -timeout {timeout}
groovy-all.jar and wlfullclient.jar in same directory as gwlst.jar or on classpath."""

	static final StackTraceCleaner cleaner = StackTraceCleaner.defaultInstance

	boolean console
	String expression
	File source
	boolean stdin
	WebLogicMBeanHomeFactory hf = new WebLogicMBeanHomeFactory()
	String[] args

	/**
	 * Consume original args into our properties including an MBeanHomeFactory.
	 */
	void loadArguments(String[] args) {
		args = hf.loadArguments(args)
		if (args) {
			switch (args[0]) {
				case '-console':
					console = true
					args = (args.length > 1) ? args[1..-1] : []
					break
				case '-e':
					if (args.length < 2) {
						System.err.println "ERROR: gwlst -e without expression.\n$usage"
						System.exit 1
					}
					expression = args[1]
					args = (args.length > 2) ? args[2..-1] : []
					break
				case '-':
					stdin = true
					args = (args.length > 1) ? args[1..-1] : []
					break
				default:
					source = new File(args[0])
					if (!source.canRead()) {
						System.err.println "ERROR: gwlst filename not readable: '$source'.\n$usage"
						System.exit 1
					}
					args = (args.length > 1) ? args[1..-1] : []
			}
		}
		this.args = args
	}

	/**
	 * Prompt user for any missing properties.
	 */
	void topupHomeFactory() {
		if (console || !System.console())
			hf.promptPopup()
		else
			hf.promptConsole()
	}

	/**
	 * Create shell and evaluate script in whatever form it comes.
	 */
	void launch() {
		def binding = new Binding()
		try {
			GWLSTBindings.bind(hf,binding)	// TODO graceful
		} catch(Exception e) {
			System.err.println "Could not connect with url=$hf.url username=$hf.username"
			cleaner.deepClean e
			e.printStackTrace()
			System.err.println()
			System.err.println usage
			System.exit 1
		}
		binding.args = args
		def shell = new GroovyShell(binding)

		if (expression) {
			logger.fine "Evaluating expression '$expression'"
			evaluate shell, expression
		} else if (source) {
			logger.fine "Evaluating source $source.absolutePath"
			source.withReader {
				hashbang it
				evaluate shell, it
			}
		} else if (stdin) {
			logger.fine "Evaluating stdin"
			System.in.withReader {
				/* it is not a BufferedReader with mark */
				def br = new BufferedReader(it)
				hashbang br
				evaluate shell, br
			}
		} else {
			/* Console or terminal */
			Console terminal = System.console()
			if (console || !terminal) {
				/* GUI Console */
				logger.fine "Starting GUI Console (console=$console, terminal=$terminal)"
				new groovy.ui.Console(binding).run()
			} else {
				/* Terminal */
				logger.fine "Reading from terminal $terminal"
				System.err.println binding.status.call()
				String line
				while ( (line = terminal.readLine('GWLST> ')) != null) {
					if (line.length() == 0) break	// Empty line exits
					try {
						def result = shell.evaluate(line)
						if (result != null) println result
					} catch (Exception e) {
						cleaner.deepClean e
						e.printStackTrace System.err
						/** Keep calm and carry on. */
					}
				}
			}
		}
	}

	static void main(String[] args) {
		if (args && args[0] ==~ /-h.*|-\?/) {
			println usage
			System.exit(0)
		}

		loglevel()

		GWLST gwlst = new GWLST()
		gwlst.hf.loadEnvironmentProperties()
		gwlst.hf.loadSystemProperties()
		gwlst.loadArguments args
		gwlst.topupHomeFactory()
		gwlst.launch()
	}

	private static void loglevel() {
		String loglevel = System.getenv('GWLST_LOGLEVEL')
		loglevel = System.getProperty('gwlst.loglevel', loglevel)
		if (loglevel) {
			Level level = Level.parse(loglevel)
			Logger toplogger = Logger.getLogger('com.middlewareman')
			toplogger.level = level
			def consoleHandlers = toplogger.handlers.findAll { it instanceof ConsoleHandler }
			if (consoleHandlers) {
				for (ConsoleHandler handler in consoleHandlers) {
					if (handler.level.intValue() > level.intValue())
						handler.level = level
				}
			} else {
				toplogger.addHandler new ConsoleHandler(level:level)
			}
			// TODO formatter
			logger.config "Configured java.util.logging.Level $level"
		}
	}

	private static void hashbang(Reader reader) {
		assert reader.markSupported()
		reader.mark 1
		char first = reader.read()
		if (first == '#' as char) {
			reader.reset()
			String line = reader.readLine()
			logger.log(Level.FINE, "Discarding hashbang line: '$line'")
		} else {
			reader.reset()
		}
	}

	private static void evaluate(GroovyShell shell, script) {
		try {
			def result = shell.evaluate(script)
			if (result != null) println result
		} catch(Exception e) {
			cleaner.deepClean e
			e.printStackTrace()
			System.exit 1
		}
	}
}
