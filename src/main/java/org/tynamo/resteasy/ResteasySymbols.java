package org.tynamo.resteasy;

public class ResteasySymbols
{
	public static final String MAPPING_PREFIX = "resteasy.servlet.mapping.prefix";
	public static final String MAPPING_PREFIX_JSAPI = "resteasy.servlet.mapping.prefix.jsapi";

	/**
	 * If "true", then the InternalConstants.TAPESTRY_APP_PACKAGE_PARAM + ".rest" package will be added to the
	 * ResteasyPackageManager so that it will be scanned for annotated REST resource classes.
	 */
	public static final String AUTOSCAN_REST_PACKAGE = "tynamo.resteasy.autodiscovery";

}
