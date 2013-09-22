package com.javax0.djcproxy.filters;

import java.lang.reflect.Method;
import static org.junit.Assert.*;
import org.junit.Test;

public class FiltersTest {
	private static final Method nullMethod = null;

	@Test
	public void testAll() {
		assertTrue(Filter.all().accept(nullMethod));
	}

	@Test
	public void testNone() {
		assertFalse(Filter.none().accept(nullMethod));
	}

	@Test
	public void testAnd() {
		assertFalse(Filter.and(Filter.all(), Filter.none()).accept(nullMethod));
		assertFalse(Filter.and(Filter.none(), Filter.all()).accept(nullMethod));
		assertFalse(Filter.and(Filter.none(), Filter.none()).accept(nullMethod));
		assertTrue(Filter.and(Filter.all(), Filter.all()).accept(nullMethod));
	}

	@Test
	public void testOr() {
		assertTrue(Filter.or(Filter.all(), Filter.none()).accept(nullMethod));
		assertTrue(Filter.or(Filter.none(), Filter.all()).accept(nullMethod));
		assertFalse(Filter.or(Filter.none(), Filter.none()).accept(nullMethod));
		assertTrue(Filter.or(Filter.all(), Filter.all()).accept(nullMethod));
	}

	@Test
	public void testNot() {
		assertTrue(Filter.not(Filter.none()).accept(nullMethod));
		assertFalse(Filter.not(Filter.all()).accept(nullMethod));
	}

}
