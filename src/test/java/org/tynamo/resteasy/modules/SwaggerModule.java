package org.tynamo.resteasy.modules;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.ApplicationDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.services.BaseURLSource;
import org.tynamo.resteasy.ResteasySymbols;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import io.swagger.config.ScannerFactory;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.AcceptHeaderApiListingResource;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.v3.jaxrs2.SwaggerSerializers;

public class SwaggerModule
{

	@Contribute(SymbolProvider.class)
	@ApplicationDefaults
	public static void provideSymbols(MappedConfiguration<String, Object> configuration)
	{
		configuration.add(ResteasySymbols.CORS_ENABLED, true);
	}

	@Contribute(javax.ws.rs.core.Application.class)
	public static void contributeApplication(Configuration<Object> singletons)
	{
		singletons.addInstance(ApiListingResource.class);
		singletons.addInstance(SwaggerSerializers.class);
		singletons.addInstance(AcceptHeaderApiListingResource.class);
	}

	@Contribute(javax.ws.rs.core.Application.class)
	public static void jacksonJsonProviderSetup(Configuration<Object> singletons)
	{

		final ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
		/**
		 * "publishedDate": 1384267338786,
		 * vs
		 * "publishedDate": "2013-11-12T14:42:18.786+0000",
		 */
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		JacksonJaxbJsonProvider jacksonJsonProvider = new JacksonJaxbJsonProvider();
		jacksonJsonProvider.setMapper(mapper);

		singletons.add(jacksonJsonProvider);
	}

	@Startup
	public static void swagger(javax.ws.rs.core.Application application,
	                           BaseURLSource baseURLSource,
	                           @Symbol(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM) String basePackage,
	                           @Symbol(ResteasySymbols.MAPPING_PREFIX) String restPath,
	                           @Symbol(SymbolConstants.APPLICATION_VERSION) String version)
	{
		application.getSingletons(); // EAGER LOADING!!

		BeanConfig config = new BeanConfig();
		config.setResourcePackage(basePackage);
		config.setVersion(version);
		config.setBasePath("http://localhost:8080" + restPath);
//		config.setBasePath(baseURLSource.getBaseURL(false) + restPath);
		config.setTitle("Tapestry5-RESTEasy-Swagger Sample");
/*
		config.setDescription("");
		config.setContact("");
		config.setLicense("Apache 2.0 License");
		config.setLicenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html");
*/
		config.setScan(true);
		// kaosko 2020-04-20, doesn't seem this is required anymore?
		// config.set set setApiReader(DefaultJaxrsApiReader.class.getCanonicalName()); // Add the reader, which scans the resources and
		// extracts the resource information

		ScannerFactory.setScanner(config);
	}
}
