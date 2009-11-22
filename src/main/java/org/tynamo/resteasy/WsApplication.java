package org.tynamo.resteasy;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;

public class WsApplication extends Application
{
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();


	public WsApplication(Collection<Object> singletons)
	{
		this.singletons = new HashSet<Object>(singletons);
	}

	@Override
	public Set<Class<?>> getClasses()
	{
		return empty;
	}

	@Override
	public Set<Object> getSingletons()
	{
		return singletons;
	}
}
