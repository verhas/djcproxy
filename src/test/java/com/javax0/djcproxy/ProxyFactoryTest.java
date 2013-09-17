package com.javax0.djcproxy;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ProxyFactoryTest {

	protected static class A {
		public A() {
		}

		public int method() {
			return 1;
		}
	}

	private class Interceptor implements MethodInterceptor {

		@Override
		public Object intercept(Object obj, Method method, Object[] args)
				throws Exception {
			return 0;
		}

	}

	@Test
	public void given_Object_when_CreatingSource_then_GettingInterceptorResult()
			throws Exception {

		A a = new A();
		ProxyFactory<A> factory = new ProxyFactory<>();
		A s = factory.create(a, new Interceptor());
		// careful: this calls toString, which is intercepted and returns
		// Integer that will fail
		// System.out.println(s);
		Assert.assertEquals(0, s.method());
	}

	@Test
	public void given_Object_when_CreatingSourceWithFilter_then_GettingOriginalResult()
			throws Exception {

		A a = new A();
		ProxyFactory<A> factory = new ProxyFactory<>();
		factory.setCallbackFilter(new CallbackFilter() {

			@Override
			public boolean accept(Method method) {
				return false;
			}
		});
		A s = factory.create(a, new Interceptor());
		Assert.assertEquals(1, s.method());
	}

	protected static class B {
		public int method() {
			return 1;
		}
	}

	@Test
	public void given_ObjectWithDefaultConstructor_when_CreatingSource_then_GettingInterceptorResult()
			throws Exception {

		B a = new B();
		ProxyFactory<B> factory = new ProxyFactory<>();
		B s = factory.create(a, new Interceptor());
		// careful: this calls toString, which is intercepted and returns
		// Integer that will fail
		// System.out.println(s);
		Assert.assertEquals(0, s.method());
	}

	@Test
	public void given_ObjectWithDefaultConstructor_when_CreatingSourceWithFilter_then_GettingOriginalResult()
			throws Exception {

		B a = new B();
		ProxyFactory<B> factory = new ProxyFactory<>();
		factory.setCallbackFilter(new CallbackFilter() {

			@Override
			public boolean accept(Method method) {
				return false;
			}
		});
		B s = factory.create(a, new Interceptor());
		Assert.assertEquals(1, s.method());
	}

	protected static class C {
		public C(int i) {
		}

		public int method() {
			return 1;
		}
	}

	@Test
	public void given_ObjectWithParametrizedConstructor_when_CreatingSource_then_GettingInterceptorResult()
			throws Exception {

		C a = new C(1);
		ProxyFactory<C> factory = new ProxyFactory<>();
		C s = factory.create(a, new Interceptor());
		// careful: this calls toString, which is intercepted and returns
		// Integer that will fail
		// System.out.println(s);
		Assert.assertEquals(0, s.method());
	}

	@Test
	public void given_ObjectWithParametrizedConstructor_when_CreatingSourceWithFilter_then_GettingOriginalResult()
			throws Exception {

		C a = new C(1);
		ProxyFactory<C> factory = new ProxyFactory<>();
		factory.setCallbackFilter(new CallbackFilter() {

			@Override
			public boolean accept(Method method) {
				return false;
			}
		});
		C s = factory.create(a, new Interceptor());
		Assert.assertEquals(1, s.method());
	}

	private class ToString implements MethodInterceptor {

		@Override
		public Object intercept(Object obj, Method method, Object[] args)
				throws Exception {
			return "ToString interceptor";
		}
	}

	@Test
	public void given_ObjectFromJavaLang_when_CreatingProxy_ToStringIsIntercepted()
			throws Exception {
		Object o = new Object();
		ProxyFactory<Object> factory = new ProxyFactory<>();
		Object pxy = factory.create(o, new ToString());
		Assert.assertEquals("ToString interceptor", pxy.toString());
	}
}
