package com.javax0.djcproxy.filters;

import com.javax0.djcproxy.CallbackFilter;

public class Filter {

	private Filter() throws IllegalAccessException {
		throw new IllegalAccessException(
				"Utility class should not be instantiated");
	}

	private static final CallbackFilter NON_OBJECT = new NonObject();
	private static final CallbackFilter ALL = new All();
	private static final CallbackFilter NONE = new None();
	private static final CallbackFilter OBJECTS = new Objects();

	public static CallbackFilter all() {
		return ALL;
	}

	public static CallbackFilter and(CallbackFilter a, CallbackFilter b) {
		return new And(a, b);
	}

	public static CallbackFilter intercept(String... names) {
		return new Name(names);
	}

	public static CallbackFilter none() {
		return NONE;
	}

	public static CallbackFilter nonObject() {
		return NON_OBJECT;
	}

	public static CallbackFilter not(CallbackFilter a) {
		return new Not(a);
	}

	public static CallbackFilter objects() {
		return OBJECTS;
	}

	public static CallbackFilter or(CallbackFilter a, CallbackFilter b) {
		return new Or(a, b);
	}

}
