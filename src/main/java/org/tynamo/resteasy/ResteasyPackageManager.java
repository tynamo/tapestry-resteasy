package org.tynamo.resteasy;

import org.apache.tapestry5.ioc.annotations.UsesConfiguration;

import java.util.Collection;

/**
 * Contains a set of contributed package names from which to load REST resources
 * <p/>
 * The service's configuration is the names of Java packages to search for REST resources.
 */
@UsesConfiguration(String.class)
public interface ResteasyPackageManager
{
	/**
	 * Returns packages from which read REST resource classes
	 */
	Collection<String> getPackageNames();
}

