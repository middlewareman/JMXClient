package com.middlewareman.mbean;

import java.io.Closeable;
import java.io.IOException;

import javax.management.MBeanServerConnection;

public interface MBeanServerConnectionFactory extends Closeable {

	MBeanServerConnection getMBeanServerConnection() throws IOException;
	
}
