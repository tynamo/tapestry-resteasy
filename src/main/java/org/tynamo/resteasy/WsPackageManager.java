package org.tynamo.resteasy;

import org.apache.tapestry5.ioc.annotations.UsesConfiguration;

import java.util.Collection;

/**
 * Contains a set of contributed package names from which to load rest services
 * <p/>
 * The service's configuration is the names of Java packages to search for rest services.
 */
@UsesConfiguration(String.class)
public interface WsPackageManager
{
	/**
	 * Returns packages from which read rest services classes
	 */
	Collection<String> getPackageNames();
}

