package org.tynamo.resteasy;

import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.ioc.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ResteasyDeployment;

import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;

public class ResteasyTapestryFilter extends TapestryFilter
{

	@Override
	protected void init(Registry registry) throws ServletException
	{
		super.init(registry);

		final ServletContext context = getFilterConfig().getServletContext();
		final org.jboss.resteasy.spi.Registry resteasyRegistry = (org.jboss.resteasy.spi.Registry) context.getAttribute(org.jboss.resteasy.spi.Registry.class.getName());
		final ResteasyProviderFactory providerFactory = (ResteasyProviderFactory) context.getAttribute(ResteasyProviderFactory.class.getName());

		final Application application = registry.getService(Application.class);
		ResteasyDeployment.processApplication(application, resteasyRegistry, providerFactory);

	}
}
