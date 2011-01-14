/*
 * $Id$
 * Copyright (c) 2010 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.type;

import java.util.*;

import javax.management.openmbean.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * Wrapper for {@link TabularData} that unwraps {@link OpenType} values and
 * provides map-like access in Groovy and a readable String representation.
 * <p>
 * Columns are identified by names of which at least one is part of the index
 * (primary key of a row). TabularData allows this index to consist of an
 * arbitrary set of columns and hence addresses a row by an object array.
 * TabularDataWrapper extends this behaviour by allowing indexing by a single
 * value in the common case that the index consists of a single name. In
 * addition, when there is only one non-index column, it too will be treated as
 * a single value rather than an array or a map. Thus, in the common case that
 * TabularData is used to represent a simple key-simple value map,
 * TabularDataWrapper will behave as such.
 * </p>
 * <p>
 * Rows can be indexed by <code>Object</code> (only when single index column),
 * <code>Object[]</code> or <code>Map<String,?></code>.
 * </p>
 * 
 * @author Andreas Nyberg
 */
class TabularDataWrapper implements TableModel {

	final TabularData delegate;
	final List<String> columnNames;
	final String[] indexNames;
	final String[] nonIndexNames;
	final OpenType<?>[] nonIndexTypes;

	public TabularDataWrapper(TabularData delegate) {
		this.delegate = delegate;
		TabularType tt = delegate.getTabularType();
		CompositeType rowType = tt.getRowType();
		List<String> indexNamesList = tt.getIndexNames();
		Set<String> nonIndexNamesSet = new LinkedHashSet<String>(
				rowType.keySet());
		nonIndexNamesSet.removeAll(indexNamesList);
		indexNames = indexNamesList.toArray(new String[indexNamesList.size()]);
		nonIndexNames = nonIndexNamesSet.toArray(new String[nonIndexNamesSet
				.size()]);
		columnNames = new ArrayList<String>();
		columnNames.addAll(indexNamesList);
		columnNames.addAll(nonIndexNamesSet);
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

	private Object[] calculateIndex(Map<String, ?> value) {
		Object[] index = new Object[indexNames.length];
		for (int i = 0; i < indexNames.length; i++) {
			if (value.containsKey(indexNames[i]))
				index[i] = value.get(indexNames[i]);
			else
				return null;
		}
		return index;
	}

	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	private boolean containsKey(Object[] key) {
		assert indexNames.length == key.length;
		return delegate.containsKey(key);
	}

	private boolean containsKey(Object key) {
		assert indexNames.length == 1;
		return containsKey(new Object[] { key });
	}

	private boolean containsKey(Map<String, ?> key) {
		return containsKey(calculateIndex(key));
	}

	public Object getAt(Object key) {
		assert indexNames.length == 1;
		assert key != null;
		return getAt(new Object[] { key });
	}

	public Object getAt(List<?> key) {
		assert key != null;
		return getAt(key.toArray());
	}

	public Object getAt(Object[] key) {
		assert key != null;
		assert indexNames.length == key.length;
		CompositeData cd = delegate.get(key);
		if (nonIndexNames.length == 1)
			return cd.get(nonIndexNames[0]);
		else
			return new CompositeDataWrapper(cd);
	}

	public Object get(String key) {
		System.err.println("get Object " + key);
		assert indexNames.length == 1;
		return getAt(new Object[] { key });
	}

	public Object get(Map<String, ?> key) {
		System.err.println("get Map " + key);
		Object[] index = calculateIndex(key);
		if (index == null)
			return null;
		Object value = getAt(index);
		if (value == null)
			return null;
		if (key.size() > indexNames.length) {
			System.err.println("TabularDataWrapper.getAt(" + key
					+ ") checking superfluos keys"); // TODO logging
			for (String name : nonIndexNames) {
				if (key.containsKey(name)) {
					Object keyValue = key.get(name);
					Object colValue = (value instanceof CompositeDataWrapper) ? ((CompositeDataWrapper) value)
							.get(name) : value;
					boolean equal = (keyValue == null) ? (colValue == null)
							: keyValue.equals(colValue);
					if (!equal) {
						System.err.println("Disqualifying name=" + name
								+ ", keyValue=" + keyValue + ", colValue="
								+ colValue); // TODO logging
						return null;
					}
				}
			}
		}
		return value;
	}

	@Override
	public int getRowCount() {
		return delegate.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.size();
	}

	@Override
	public String getColumnName(int columnIndex) {
		return columnNames.get(columnIndex);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return delegate.getTabularType().getRowType()
				.getType(columnNames.get(columnIndex)).getClass();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Collection<CompositeData> cds = (Collection<CompositeData>) delegate.values();
		assert rowIndex < cds.size();
		Iterator<CompositeData> cdi = cds.iterator();
		CompositeData cd = null;
		for (int i = 0; i <= rowIndex; i++)
			cd = cdi.next();
		String key = columnNames.get(columnIndex);
		return cd.get(key);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub
	}

	public boolean equals(Object other) {
		if (other == null)
			return false;
		else if (other instanceof TabularDataWrapper)
			return equals((TabularDataWrapper) other);
		else if (other instanceof TabularData)
			return equals((TabularData) other);
		else if (other instanceof Map)
			return equals((Map<?, ?>) other);
		else
			return false;
	}

	public boolean equals(TabularDataWrapper other) {
		return delegate.equals(other.delegate);
	}

	public boolean equals(TabularData other) {
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
