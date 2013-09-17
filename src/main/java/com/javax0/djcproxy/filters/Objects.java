package com.javax0.djcproxy.filters;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.javax0.djcproxy.CallbackFilter;

/**
 * Callback filter that accepts the methods which are declared in one of the
 * classes that are specified in the constructor.
 * 
 * @author Peter Verhas
 * 
 */
public class Objects implements CallbackFilter {
	private final List<Class<?>> classes;

	public Objects(Class<?>... classes) {
		this.classes = Arrays.asList(classes);
	}

	@Override
	public boolean accept(Method method) {
		return classes.contains(method.getDeclaringClass());
	}

}
