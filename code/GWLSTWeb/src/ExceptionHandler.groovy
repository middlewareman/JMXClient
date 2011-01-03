/*
 * $Id$
 * Copyright © 2010 Middlewareman Limited. All rights reserved.
 */

import com.middlewareman.groovy.util.StackTraceCleaner

import javax.servlet.RequestDispatcher 
import javax.servlet.ServletContext 
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * Handles forwarding to error page for groovlets that are not happy with GroovyServlet behaviour.
 * 
 * @author Andreas Nyberg
 */
class ExceptionHandler {
	
	/** 
	 * Move values into places that the error page expects and forward.
	 */
	static void exception(Throwable t, HttpServletRequest request,
	HttpServletResponse response, RequestDispatcher dispatcher) {
		
		//request.'javax.servlet.error.status_code'
		request.'javax.servlet.error.exception_type' = t.getClass()
		request.'javax.servlet.error.message' = t.message
		request.'javax.servlet.error.request_uri' = request.requestURI
		request.'javax.servlet.error.exception' = t
		//request.'javax.servlet.error.servlet_name'
		
		dispatcher.forward request, response
	}
	
	private binding;
	private errorPage;
	
	ExceptionHandler(Binding binding, String errorPage = '/Exception.groovy') {
		this.binding = binding;
		this.errorPage = errorPage;
	}
	
	void exception(Throwable t) {
		HttpServletRequest request = binding.request
		assert request
		HttpServletResponse response = binding.response
		assert response
		if (response.isCommitted()) {
			def message = "${getClass().getName()} response already committed bufferSize=$response.bufferSize"
			StackTraceCleaner.defaultInstance.deepClean t
			ServletContext context = binding.context
			if (context) {
				context.log message, t
			} else {
				System.err.println message
				t.printStackTrace(System.err)
			}
		} else {
			RequestDispatcher dispatcher = request.getRequestDispatcher(errorPage)
			assert dispatcher
			exception t, request, response, dispatcher
		}
	}
	
	void wrap(Closure script) {
		try {
			script.call()
		} catch (Exception e) {
			exception e
		}
	}
}
