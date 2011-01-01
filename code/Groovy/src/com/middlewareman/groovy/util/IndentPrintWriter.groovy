/*
 * $Id$
 * Copyright © 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.groovy.util

/**
 * PrintWriter with indentation using closures. 
 * Indentation is automatically added at the beginning of a new line.
 * A future feature could be to scan the string for the newline character to 
 * call {@link #println()} for each of these rather than just writing it, 
 * as this will break indentation.
 * 
 * @author Andreas Nyberg
 */
class IndentPrintWriter extends PrintWriter {

	/** The object to print as a single indentation; defaults to a string of four spaces. */
	def tab = '    '

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
