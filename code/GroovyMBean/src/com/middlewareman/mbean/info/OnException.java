package com.middlewareman.mbean.info;

/**
 * Indicates how to treat exceptions when retrieving an attribute.
 * <ul>
 * <li>{@link #OMIT} omits the attribute from the result. This is consistent
 * with GDK
 * {@link org.codehaus.groovy.runtime.DefaultGroovyMethods#getProperties(Object)
 * getProperties()} that also omits any property that throws an exception on
 * get.</li>
 * <li>{@link #THROW} simply throws the exception.</li>
 * <li>{@link #RETURN} returns the exception itself as the value.</li>
 * </ul>
 */
public enum OnException {
	OMIT, THROW, RETURN
}
