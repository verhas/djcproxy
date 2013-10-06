package com.javax0.djcproxy;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

import test.QA;

import com.javax0.djcproxy.exceptions.FinalCanNotBeExtendedException;
import com.javax0.djcproxy.exceptions.ProxyClassCompilerError;
import com.javax0.djcproxy.filters.Filter;

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
		public Object intercept(Object obj, Method method, Object[] args,
				MethodProxy mproxy) throws Exception {
			if (method.getName().equals("toString")) {
				return "interceptedToString";
			}
			return 0;
		}

	}

	@Test
	public void given_ObjectA_when_CreatingSource_then_SomeStringIsReturned()
			throws Exception {

		A a = new A();
		ProxyFactory<A> factory = new ProxyFactory<>();
		A s = factory.create(a, new Interceptor());
		String generatedSource = factory.getGeneratedSource();
		Assert.assertNotNull(generatedSource);
		String className = factory.getGeneratedClassName();
		Assert.assertNotNull(className);
	}

	@Test
	public void given_ObjectA_when_CreatingSource_then_GettingInterceptorResult()
			throws Exception {

		A a = new A();
		ProxyFactory<A> factory = new ProxyFactory<>();
		A s = factory.create(a, new Interceptor());
		Assert.assertEquals("interceptedToString", s.toString());
		Assert.assertEquals(0, s.method());
	}

	@Test(expected = IllegalArgumentException.class)
	public void given_ObjectA_when_SettingNullCallback_then_ThrowsIllegalArgumentException()
			throws Exception {

		A a = new A();
		ProxyFactory<A> factory = new ProxyFactory<>();
		factory.setCallbackFilter(null);
	}

	@Test
	public void given_ObjectQAFromDifferentPackage_when_CreatingSource_then_GettingInterceptorResult()
			throws Exception {

		QA a = new QA();
		ProxyFactory<QA> factory = new ProxyFactory<>();
		QA s = factory.create(a, new Interceptor());
		// careful: this calls toString, which is intercepted and returns
		// Integer that will fail
		// System.out.println(s);
		Assert.assertEquals(0, s.method());
	}

	@Test
	public void given_ObjectA_when_CreatingSourceWithFilter_then_GettingOriginalResult()
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
	public void given_ObjectBWithDefaultConstructor_when_CreatingSource_then_GettingInterceptorResult()
			throws Exception {

		B a = new B();
		ProxyFactory<B> factory = new ProxyFactory<>();
		B s = factory.create(a, new Interceptor());
		Assert.assertEquals(0, s.method());
	}

	@Test
	public void given_ObjectBWithDefaultConstructorAndNonObjectFilter_when_CreatingSource_then_GettingInterceptorResult()
			throws Exception {

		B a = new B();
		ProxyFactory<B> factory = new ProxyFactory<>();
		factory.setCallbackFilter(Filter.nonObject());
		B s = factory.create(a, new Interceptor());
		Assert.assertEquals(0, s.method());
		Assert.assertNotEquals("interceptedToString", s.toString());
	}

	@Test
	public void given_ObjectBWithDefaultConstructor_when_CreatingSourceWithFilter_then_GettingOriginalResult()
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
	public void given_ObjectCWithParametrizedConstructor_when_CreatingSource_then_GettingInterceptorResult()
			throws Exception {

		C a = new C(1);
		ProxyFactory<C> factory = new ProxyFactory<>();
		C s = factory.create(a, new Interceptor());
		System.out.println(factory.getGeneratedSource());

		// careful: this calls toString, which is intercepted and returns
		// Integer that will fail
		// System.out.println(s);
		Assert.assertEquals(0, s.method());
	}

	@Test
	public void given_ObjectCWithParametrizedConstructor_when_CreatingSourceWithFilter_then_GettingOriginalResult()
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

	private static class ToString implements MethodInterceptor {

		@Override
		public Object intercept(Object obj, Method method, Object[] args,
				MethodProxy mproxy) throws Exception {
			return "ToString interceptor";
		}
	}

	@Test
	public void given_ObjectObjectFromJavaLang_when_CreatingProxy_then_ToStringIsIntercepted()
			throws Exception {
		Object o = new Object();
		ProxyFactory<Object> factory = new ProxyFactory<>();
		factory.setClassLoader(this.getClass().getClassLoader());
		Object pxy = factory.create(o, new ToString());
		Assert.assertEquals("ToString interceptor", pxy.toString());
	}

	@Test(expected = FinalCanNotBeExtendedException.class)
	public void given_FinalObjectFromJavaLang_when_CreatingProxy_then_ThrowsException()
			throws Exception {
		Integer o = new Integer(1);
		ProxyFactory<Integer> factory = new ProxyFactory<>();
		factory.setClassLoader(this.getClass().getClassLoader());
		Object pxy = factory.create(o, new ToString());
		Assert.assertEquals("ToString interceptor", pxy.toString());
	}

	private static class PartialInterceptor implements MethodInterceptor {

		@Override
		public Object intercept(Object obj, Method method, Object[] args,
				MethodProxy mproxy) throws Exception {
			return "x";
		}
	}

	public static class D {
		public String a() {
			return "a";
		}

		public String b() {
			return "b";
		}

		public String c() {
			return "c";
		}
	}

	@Test
	public void given_ObjectWithPartialInterceptor_when_CreatingProxy_then_OnlySpecifiedMethodsAreIntercepted()
			throws Exception {
		D d = new D();
		ProxyFactory<D> factory = new ProxyFactory<>();
		factory.setCallbackFilter(Filter.intercept("a", "b"));
		D pxy = factory.create(d, new PartialInterceptor());
		Assert.assertEquals("x", pxy.a());
		Assert.assertEquals("x", pxy.b());
		Assert.assertEquals("c", pxy.c());
	}

	private static class E {

	}

	@Test(expected = ProxyClassCompilerError.class)
	public void given_NonAccessibleObject_when_CreatingProxy_then_CompilerException()
			throws Exception {
		E e = new E();
		ProxyFactory<E> factory = new ProxyFactory<>();
		@SuppressWarnings("unused")
		E pxy = factory.create(e, new PartialInterceptor());
	}

	private static class PassThroughInterceptor implements MethodInterceptor {
		@Override
		public Object intercept(Object obj, Method method, Object[] args,
				MethodProxy mproxy) throws Throwable {
			return "x" + method.invoke(obj, args) + "y";
		}
	}

	private static class PassThroughInterceptorUsingMethodProxy implements
			MethodInterceptor {
		@Override
		public Object intercept(Object obj, Method method, Object[] args,
				MethodProxy mproxy) throws Throwable {
			return "x" + mproxy.invoke(obj, args) + "y";
		}
	}

	public static class F {

		public String q() {
			return "a";
		}
	}

	@Test
	public void given_Object_when_CreatingProxy_then_InterceptorCallsTheOriginalMethod()
			throws Exception {
		F f = new F();
		ProxyFactory<F> factory = new ProxyFactory<>();
		F pxy = factory.create(f, new PassThroughInterceptor());
		Assert.assertEquals("xay", pxy.q());
	}

	@Test
	public void given_Object_when_CreatingProxy_then_InterceptorCallsTheOriginalMethodViaMethodProxy()
			throws Exception {
		F f = new F();
		ProxyFactory<F> factory = new ProxyFactory<>();
		F pxy = factory.create(f, new PassThroughInterceptorUsingMethodProxy());
		Assert.assertEquals("xay", pxy.q());
	}

}
