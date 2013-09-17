package com.javax0.djcproxy.filters;

import java.lang.reflect.Method;

import com.javax0.djcproxy.CallbackFilter;

/**
 * Callback filter that accepts a method if both filterA and filterB accept the
 * method.
 * 
 * @author Peter Verhas
 * 
 */
public class And implements CallbackFilter {

	private final CallbackFilter filterA;
	private final CallbackFilter filterB;

	public And(CallbackFilter filterA, CallbackFilter filterB) {
		this.filterA = filterA;
		this.filterB = filterB;
	}

	@Override
	public boolean accept(Method method) {
		return filterA.accept(method) && filterB.accept(method);
	}

}
