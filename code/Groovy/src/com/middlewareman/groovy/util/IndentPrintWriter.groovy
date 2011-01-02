/*
 * $Id$
 * Copyright © 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.groovy.util

import java.util.regex.Pattern

/**
 * PrintWriter with indentation using closures. 
 * The appropriate number of {@link #tab} is automatically added at the 
 * beginning of each new line.
 * End of line sequences {@link #endOfLine} are recognised when printing strings
 * and will result in a sequence of {@link PrintWriter#println(String)} being called to
 * preserve indentation.
 * 
 * @author Andreas Nyberg
 */
class IndentPrintWriter extends PrintWriter {

	/** The object to print as a single indentation; defaults to a string of four spaces. */
	def tab = '    '

	/** Pattern in String to recognise as end of line; defaults to <code>\n</code>. */
	Pattern endOfLine = ~'\n'

	private int level = 0
	private boolean newline = true

	/** Default constructor for {@link System#out} with autoFlush. */
	IndentPrintWriter() {
		this(System.out, true)
	}

	IndentPrintWriter(File file, String csn)
	throws FileNotFoundException, UnsupportedEncodingException {
		super(file, csn);
	}

	IndentPrintWriter(File file) throws FileNotFoundException {
		super(file);
	}

	IndentPrintWriter(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
	}

	IndentPrintWriter(OutputStream out) {
		super(out);
	}

	IndentPrintWriter(String fileName, String csn)
	throws FileNotFoundException, UnsupportedEncodingException {
		super(fileName, csn);
	}

	IndentPrintWriter(String fileName) throws FileNotFoundException {
		super(fileName);
	}

	IndentPrintWriter(Writer out, boolean autoFlush) {
		super(out, autoFlush);
	}

	IndentPrintWriter(Writer out) {
		super(out);
	}

	private void indent0() {
		if (newline) {
			newline = false
			level.times { super.print tab }
		}
	}

	void write(int c) {
		indent0()
		super.write(c);
	}

	void write(char[] buf, int off, int len) {
		indent0()
		super.write(buf, off, len);
	}

	void write(char[] buf) {
		indent0()
		super.write(buf);
	}

	void write(String s, int off, int len) {
		indent0()
		super.write(s, off, len);
	}

	void write(String s) {
		indent0()
		super.write(s);
	}

	void println() {
		super.println()
		newline = true
	}

	void print(String s) {
		if (s != null && endOfLine != null) {
			String[] lines = endOfLine.split(s, -1)
			int i = 0;
			while (i < lines.length - 1) {
				println lines[i++]
			}
			super.print lines[i]
		} else {
			super.print(s)
		}
	}

	void indent(Closure closure) {
		++level
		closure.call()
		--level
	}

	void indent(header, Closure closure) {
		println header
		indent closure
	}

	void indent(header, footer, Closure closure) {
		println header
		indent closure
		println footer
	}
}
