package com.middlewareman.mbean.type;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenType;

public class CompositeDataWrapper {

	final CompositeData delegate;

	public CompositeDataWrapper(CompositeData delegate) {
		this.delegate = delegate;
	}

	public Object get(String key) {
		OpenType<?> type = delegate.getCompositeType().getType(key);
		Object value = delegate.get(key);
		return OpenTypeWrapper.wrap(type, value);
	}

	public Map<String, ?> getProperties() {
		Set<String> keys = delegate.getCompositeType().keySet();
		Map<String, Object> map = new LinkedHashMap<String, Object>(keys.size());
		for (String key : keys) {
			Object value = get(key);
			map.put(key, get(key));
		}
		return map;
	}

	public boolean containsKey(String key) {
		return delegate.containsKey(key);
	}

	public int hashCode() {
		return delegate.hashCode();
	}

	public boolean equals(CompositeDataWrapper other) {
		return delegate.equals(other.delegate);
	}

	public boolean equals(Object other) {
		if (other == null)
			return false;
		else if (other instanceof CompositeDataWrapper)
			return equals((CompositeDataWrapper) other);
		else
			return false;
	}

	public String toString() {
		return getProperties().toString();
	}
	
	public boolean equals(CompositeData other) {
		return delegate.equals(other);
	}

	public boolean equals(Map<?,?> other) {
		return getProperties().equals(other);
	}

}
