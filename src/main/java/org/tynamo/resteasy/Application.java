package org.tynamo.resteasy;

import java.util.HashSet;
import java.util.Set;
import java.util.Collection;

public class Application extends jakarta.ws.rs.core.Application
{
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();


	public Application(Collection<Object> singletons)
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
