/*
 * $Id$
 * Copyright © 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.util;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.*;

import com.middlewareman.groovy.StackTraceCleaner;
import com.middlewareman.mbean.MBeanHome;
import com.middlewareman.mbean.type.*;

public class MBeanProperties {

	private static final Logger logger = Logger.getLogger(MBeanProperties.class
			.getName());

	public static Map<String, ?> get(MBeanHome home,
			ObjectName objectName, SimpleAttributeFilter attributeFilter)
			throws InstanceNotFoundException, IntrospectionException,
			ReflectionException, IOException, AttributeNotFoundException,
			MBeanException {
		if (attributeFilter.isBulk())
			return getBulk(home, objectName, attributeFilter);
		else
			return getSingles(home, objectName, attributeFilter);
	}

	public static Map<String, ?> getSingles(MBeanHome home,
			ObjectName objectName, SimpleAttributeFilter attributeFilter)
			throws InstanceNotFoundException, IntrospectionException,
			ReflectionException, IOException, AttributeNotFoundException,
			MBeanException {
		MBeanAttributeInfo[] ais = home.getInfo(objectName).getAttributes();
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		if (attributeFilter.getOnException().equals(OnException.THROW)) {
			for (MBeanAttributeInfo attribute : ais) {
				if (attributeFilter.acceptAttribute(attribute)) {
					String name = attribute.getName();
					String newName = attributeFilter.isDecapitalise() ? decapitalise(name)
							: name;
					// TODO any other criteria for exceptions to pass through?
					Object value = home.getAttribute(objectName, name);
					if (attributeFilter.acceptAttribute(attribute, value))
						map.put(newName, value);
				}
			}
		} else {
			for (MBeanAttributeInfo attribute : ais) {
				if (attributeFilter.acceptAttribute(attribute)) {
					String name = attribute.getName();
					String newName = attributeFilter.isDecapitalise() ? decapitalise(name)
							: name;
					// TODO any other criteria for exceptions to pass through?
					try {
						Object value = home.getAttribute(objectName, name);
						if (attributeFilter.acceptAttribute(attribute, value))
							map.put(newName, value);
					} catch (IOException e) {
						throw e;
					} catch (InstanceNotFoundException e) {
						throw e;
						// TODO AttributeNotFoundException?
					} catch (Exception e) {
						switch (attributeFilter.getOnException()) {
						case OMIT:
							if (logger.isLoggable(Level.FINER)) {
								StackTraceCleaner.getDefaultInstance()
										.deepClean(e);
								logger.log(Level.FINER, "omitting "
										+ objectName + ": " + name, e);
							}
							break;
						case NULL:
							map.put(newName, null);
							if (logger.isLoggable(Level.FINER)) {
								StackTraceCleaner.getDefaultInstance()
										.deepClean(e);
								logger.log(Level.FINER, "returning null for "
										+ objectName + ": " + name, e);
							}
							break;
						case RETURN:
							StackTraceCleaner.getDefaultInstance().deepClean(e);
							map.put(newName, e);
							if (logger.isLoggable(Level.FINER)) {
								logger.log(Level.FINER,
										"returning exception for " + objectName
												+ ": " + name, e);
							}
							break;
						default:
							assert false : attributeFilter.getOnException();
						}
					}
				}
			}
		}
		return map;
	}

	public static Map<String, ?> getBulk(MBeanHome home,
			ObjectName objectName, SimpleAttributeFilter attributeFilter)
			throws InstanceNotFoundException, IntrospectionException,
			ReflectionException, IOException, AttributeNotFoundException,
			MBeanException {
		MBeanAttributeInfo[] ais = home.getInfo(objectName).getAttributes();
		Map<String, MBeanAttributeInfo> name2ai = new LinkedHashMap<String, MBeanAttributeInfo>(
				ais.length);
		for (MBeanAttributeInfo ai : ais) {
			if (attributeFilter.acceptAttribute(ai))
				name2ai.put(ai.getName(), ai);
		}

		String[] names = name2ai.keySet().toArray(new String[name2ai.size()]);
		Map<String, Object> values;
		try {
			values = home.getAttributes(objectName, names);
		} catch (IOException e) {
			throw e;
		} catch (InstanceNotFoundException e) {
			throw e;
		} catch (Exception e) {
			if (logger.isLoggable(Level.FINE)) {
				StackTraceCleaner.getDefaultInstance().deepClean(e);
				logger.log(Level.FINE,
						"reverting to single after bulk failed: " + objectName
								+ ": " + Arrays.toString(names), e);
			}
			return getSingles(home, objectName, attributeFilter);
		}

		if (names.length != values.size()) {
			if (!attributeFilter.getOnException().equals(OnException.OMIT)) {
				if (logger.isLoggable(Level.FINER)) {
					Set<String> omitted = new LinkedHashSet<String>(
							Arrays.asList(names));
					omitted.removeAll(values.keySet());
					logger.log(Level.FINER, "reverting to single as keys "
							+ omitted + " would have been omitted from "
							+ objectName);
				}
				return getSingles(home, objectName, attributeFilter);
			}
			if (logger.isLoggable(Level.FINE)) {
				Set<String> omitted = new LinkedHashSet<String>(
						Arrays.asList(names));
				omitted.removeAll(values.keySet());
				logger.log(Level.FINE, "keys " + omitted + " omitted from "
						+ objectName);
			}
		}

		Map<String, Object> map = new LinkedHashMap<String, Object>(
				values.size());
		for (Map.Entry<String, Object> pair : values.entrySet()) {
			String name = pair.getKey();
			Object value = pair.getValue();
			if (attributeFilter.acceptAttribute(name2ai.get(name), value)) {
				if (attributeFilter.isDecapitalise())
					name = decapitalise(name);
				map.put(name, value);
			}
		}
		return map;
	}

	private static String decapitalise(String name) {
		return java.beans.Introspector.decapitalize(name);
	}
}
