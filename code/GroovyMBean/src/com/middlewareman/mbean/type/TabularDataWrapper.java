package com.middlewareman.mbean.type;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularType;

/**
 * Simplifying wrapper for TabularData that behaves as a map. If indexNames is
 * single String, the key of this map is a single String, otherwise an array of
 * Strings. If the non-index names (keys for the row minus indexNames) is a
 * single value, the map value is single, otherwise a map. Rows can be indexed
 * by String/String[] or by a Map.
 * 
 * @author Andreas Nyberg
 */
class TabularDataWrapper {

	final TabularData delegate;
	final String[] indexNames;
	final String[] nonIndexNames;
	final OpenType<?>[] nonIndexTypes;

	public TabularDataWrapper(TabularData delegate) {
		this.delegate = delegate;
		TabularType tt = delegate.getTabularType();
		CompositeType rowType = tt.getRowType();
		List<String> indexNamesList = tt.getIndexNames();
		Set<String> nonIndexNamesSet = new TreeSet<String>(rowType.keySet());
		nonIndexNamesSet.removeAll(indexNamesList);
		indexNames = indexNamesList.toArray(new String[indexNamesList.size()]);
		nonIndexNames = nonIndexNamesSet.toArray(new String[nonIndexNamesSet
				.size()]);
		nonIndexTypes = new OpenType[nonIndexNames.length];
		for (int i = 0; i < nonIndexNames.length; i++)
			nonIndexTypes[i] = rowType.getType(nonIndexNames[i]);
	}

	public Map<?, ?> getProperties() {
		Set<List<String>> rowKeyLists = (Set<List<String>>) delegate.keySet();
		if (indexNames.length == 1) {
			if (nonIndexNames.length == 1) {
				Map map = new LinkedHashMap<String, Object>(delegate.size());
				for (List<String> rowKeyList : rowKeyLists) {
					assert rowKeyList.size() == 1;
					String key = rowKeyList.get(0);
					CompositeData cd = delegate.get(new Object[] { key });
					Object wrapped = OpenTypeWrapper.wrap(nonIndexTypes[0],
							cd.get(nonIndexNames[0]));
					map.put(key, wrapped);
				}
				return map;
			} else { // nonIndexNames > 1
				Map map = new LinkedHashMap<String, Map<String, Object>>(
						delegate.size());
				for (List<String> rowKeyList : rowKeyLists) {
					assert rowKeyList.size() == 1;
					String key = rowKeyList.get(0);
					CompositeData cd = delegate.get(new Object[] { key });
					Map values = new LinkedHashMap<String, Object>(
							nonIndexNames.length);
					for (int i = 0; i < nonIndexNames.length; i++) {
						Object wrapped = OpenTypeWrapper.wrap(nonIndexTypes[i],
								cd.get(nonIndexNames[i]));
						values.put(nonIndexNames[i], wrapped);
					}
					map.put(key, values);
				}
				return map;
			}
		} else { // indexNames.length > 1
			if (nonIndexNames.length == 1) {
				Map map = new LinkedHashMap<String[], Object>(delegate.size());
				for (List<String> rowKeyList : rowKeyLists) {
					assert rowKeyList.size() > 1;
					String[] keys = (String[]) rowKeyList.toArray();
					CompositeData cd = delegate.get(keys);
					Object wrapped = OpenTypeWrapper.wrap(nonIndexTypes[0],
							cd.get(nonIndexNames[0]));
					map.put(keys, wrapped);
				}
				return map;
			} else { // nonIndexNames > 1
				Map map = new LinkedHashMap<String[], Map<String, Object>>(
						delegate.size());
				for (List<String> rowKeyList : rowKeyLists) {
					assert rowKeyList.size() > 1;
					String[] keys = (String[]) rowKeyList.toArray();
					CompositeData cd = delegate.get(keys);
					Map values = new LinkedHashMap<String, Object>(
							nonIndexNames.length);
					for (int i = 0; i < nonIndexNames.length; i++) {
						Object wrapped = OpenTypeWrapper.wrap(nonIndexTypes[i],
								cd.get(nonIndexNames[i]));
						values.put(nonIndexNames[i], wrapped);
					}
					map.put(keys, values);
				}
				return map;
			}
		}
	}

	public Object[] calculateIndex(Map<String, ?> value) {
		Object[] index = new Object[indexNames.length];
		for (int i = 0; i < indexNames.length; i++) {
			if (value.containsKey(indexNames[i]))
				index[i] = value.get(indexNames[i]);
			else
				return null;
		}
		return index;
	}

	public int size() {
		return delegate.size();
	}

	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	public boolean containsKey(Object[] key) {
		assert indexNames.length == key.length;
		return delegate.containsKey(key);
	}

	public boolean containsKey(Object key) {
		assert indexNames.length == 1;
		return containsKey(new Object[] { key });
	}

	public boolean containsKey(Map<String, ?> key) {
		return containsKey(calculateIndex(key));
	}

	public Object get(Object[] key) {
		assert indexNames.length == key.length;
		CompositeData cd = delegate.get(key);
		if (nonIndexNames.length == 1)
			return cd.get(nonIndexNames[0]);
		else
			return new CompositeDataWrapper(cd);
	}

	public Object get(String key) {
		assert indexNames.length == 1;
		return get(new Object[] { key });
	}

	// @Override public boolean containsValue(CompositeData value) {
	// @Override public void put(CompositeData value) {
	// @Override public CompositeData remove(Object[] key) {
	// @Override public void putAll(CompositeData[] values) {
	// @Override public void clear() {
	// @Override public Set<?> keySet() {
	// @Override public Collection<?> values() {

	public String toString() {
		return getProperties().toString();
	}

	public boolean equals(TabularData other) {
		return delegate.equals(other);
	}

	public boolean equals(Map other) {
		return getProperties().equals(other);
	}
}
