package com.javax0.djcproxy.filters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.junit.Test;

import com.javax0.djcproxy.CallbackFilter;

public class FiltersTest {
	private static final Method nullMethod = null;

	@Test
	public void testAllFilter() {
		assertTrue(Filter.all().accept(nullMethod));
	}

	@Test
	public void testNoneFilter() {
		assertFalse(Filter.none().accept(nullMethod));
	}

	@Test
	public void testAndFilter() {
		assertFalse(Filter.and(Filter.all(), Filter.none()).accept(nullMethod));
		assertFalse(Filter.and(Filter.none(), Filter.all()).accept(nullMethod));
		assertFalse(Filter.and(Filter.none(), Filter.none()).accept(nullMethod));
		assertTrue(Filter.and(Filter.all(), Filter.all()).accept(nullMethod));
	}

	@Test
	public void testOrFilter() {
		assertTrue(Filter.or(Filter.all(), Filter.none()).accept(nullMethod));
		assertTrue(Filter.or(Filter.none(), Filter.all()).accept(nullMethod));
		assertFalse(Filter.or(Filter.none(), Filter.none()).accept(nullMethod));
		assertTrue(Filter.or(Filter.all(), Filter.all()).accept(nullMethod));
	}

	@Test
	public void testNotFilter() {
		assertTrue(Filter.not(Filter.none()).accept(nullMethod));
		assertFalse(Filter.not(Filter.all()).accept(nullMethod));
	}

	public static class A {
		public void a() {
		}
	}

	public static class B {
		public void b() {
		}
	}

	public static class C {
		public void c() {
		}
	}

	@Test
	public void testClassesFilter() throws Exception {
		Method a = A.class.getDeclaredMethod("a", (Class<?>[]) null);
		Method b = B.class.getDeclaredMethod("b", (Class<?>[]) null);
		Method c = C.class.getDeclaredMethod("c", (Class<?>[]) null);
		CallbackFilter mi = Filter.classes(A.class, C.class);
		assertTrue(mi.accept(a));
		assertFalse(mi.accept(b));
		assertTrue(mi.accept(c));
	}

	@Test
	public void testObjectAndNonObjectFilter() throws Exception {
		Method a = A.class.getDeclaredMethod("a", (Class<?>[]) null);
		Method toString = Object.class.getDeclaredMethod("toString",
				(Class<?>[]) null);
		assertTrue(Filter.object().accept(toString));
		assertFalse(Filter.object().accept(a));
		
		assertFalse(Filter.nonObject().accept(toString));
		assertTrue(Filter.nonObject().accept(a));
	}
	
	@Test
	public void testNamesFilter() throws Exception {
		Method a = A.class.getDeclaredMethod("a", (Class<?>[]) null);
		Method b = B.class.getDeclaredMethod("b", (Class<?>[]) null);
		Method c = C.class.getDeclaredMethod("c", (Class<?>[]) null);
		CallbackFilter mi = Filter.intercept("a", "c");
		assertTrue(mi.accept(a));
		assertFalse(mi.accept(b));
		assertTrue(mi.accept(c));
	}
}
