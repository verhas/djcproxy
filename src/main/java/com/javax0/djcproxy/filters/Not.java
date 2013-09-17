package com.javax0.djcproxy.filters;

import java.lang.reflect.Method;

import com.javax0.djcproxy.CallbackFilter;

/**
 * Callback filter that accepts a method if the underlying filter does not
 * accept the method.
 * 
 * @author Peter Verhas
 * 
 */
public class Not implements CallbackFilter {

	private final CallbackFilter filter;

	public Not(CallbackFilter filter) {
		this.filter = filter;
	}

	@Override
	public boolean accept(Method method) {
		return !filter.accept(method);
	}

}
