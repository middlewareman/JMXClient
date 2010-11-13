package com.middlewareman.mbean.type;

import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.InvalidOpenTypeException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;

public class OpenTypeWrapper {

	public static Object wrap(OpenType<?> openType, Object object) {
		if (object == null)
			return null;
		else if (object instanceof CompositeData)
			return new CompositeDataWrapper((CompositeData) object);
		else if (object instanceof TabularData)
			return new TabularDataWrapper((TabularData) object);
		else if (openType instanceof SimpleType)
			try {
				return Class.forName(openType.getClassName()).cast(object);
			} catch (ClassNotFoundException e) {
				throw new InvalidOpenTypeException(e.getMessage());
			}
		else if (openType instanceof ArrayType)
			return null; // TODO ((ArrayType)openType)object;
		else
			return object;
	}

	public static Object wrap(Object object) {
		if (object == null)
			return null;
		else if (object instanceof CompositeData)
			return new CompositeDataWrapper((CompositeData) object);
		else if (object instanceof TabularData)
			return new TabularDataWrapper((TabularData) object);
		else
			return object;
	}

	public static Object unwrap(Object object) {
		if (object == null)
			return null;
		else if (object instanceof CompositeDataWrapper)
			return ((CompositeDataWrapper) object).delegate;	// TODO dirty
		else if (object instanceof TabularDataWrapper)
			return ((TabularDataWrapper) object).delegate;		// TODO dirty
		else
			return object;
	}

}
