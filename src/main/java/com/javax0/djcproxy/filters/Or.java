package com.javax0.djcproxy.filters;

import java.lang.reflect.Method;

import com.javax0.djcproxy.CallbackFilter;

/**
 * Callback filter that accepts a method of either filterA or filterB accept the
 * method.
 * 
 * @author Peter Verhas
 * 
 */
public class Or implements CallbackFilter {

	private final CallbackFilter filterA;
	private final CallbackFilter filterB;

	public Or(CallbackFilter filterA, CallbackFilter filterB) {
		this.filterA = filterA;
		this.filterB = filterB;
	}

	@Override
	public boolean accept(Method method) {
		return filterA.accept(method) || filterB.accept(method);
	}

}
