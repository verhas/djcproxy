package com.javax0.djcproxy.filters;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.javax0.djcproxy.CallbackFilter;

/**
 * Callback filter that accepts the method for which the names are specified in
 * the constructor.
 * 
 * @author verhasp
 * 
 */
public class Name implements CallbackFilter {
	private final List<String> names;

	public Name(String... names) {
		this.names = Arrays.asList(names);
	}

	@Override
	public boolean accept(Method method) {
		return names.contains(method.getName());
	}

}
