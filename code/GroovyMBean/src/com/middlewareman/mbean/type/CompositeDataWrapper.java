/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.type;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenType;

/**
 * Wrapper for {@link CompositeData} that unwraps {@link OpenType} values and
 * provides map-like access in Groovy and a readable {@link #toString() String
 * representation.
 * 
 * @author Andreas Nyberg
 */
public class CompositeDataWrapper {

	/** Public access to wrapped object. */
	final CompositeData delegate;

	public CompositeDataWrapper(CompositeData delegate) {
		this.delegate = delegate;
	}

	public Object get(String key) {
		OpenType<?> type = delegate.getCompositeType().getType(key);
		Object value = delegate.get(key);
		return OpenTypeWrapper.wrap(type, value);
	}

	/** Returns unwrapped values as an ordered map. */
	public Map<String, ?> getProperties() {
		Set<String> keys = delegate.getCompositeType().keySet();
		Map<String, Object> map = new LinkedHashMap<String, Object>(keys.size());
		for (String key : keys) {
			map.put(key, get(key));
		}
		return map;
	}

	public boolean containsKey(String key) {
		return delegate.containsKey(key);
	}

	public boolean equals(Object other) {
		if (other == null)
			return false;
		else if (other instanceof CompositeDataWrapper)
			return equals((CompositeDataWrapper) other);
		else if (other instanceof CompositeData)
			return equals((CompositeData) other);
		else if (other instanceof Map)
			return equals((Map<?, ?>) other);
		else
			return false;
	}

	public boolean equals(CompositeDataWrapper other) {
		return delegate.equals(other.delegate);
	}

	public boolean equals(CompositeData other) {
		return delegate.equals(other);
	}

	public boolean equals(Map<?, ?> other) {
		return getProperties().equals(other);
	}

	public int hashCode() {
		return delegate.hashCode();
	}

	/**
	 * Returns String representation of {@link #getProperties properties} map.
	 */
	public String toString() {
		return getProperties().toString();
	}

}
