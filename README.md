Dynamic Java Proxy Generator
============================

Using this library you can create proxy objects to an already existing object during run time. With the library you can create aspects oriented features during run-time, like

* measuring the time the program spends in certain methods
* forbid the invocation some of the methods on a certain object (creating an immutable version of the object)
* log the execution of the methods
* many other things that a proxy class can do

The use of this library is very similar to the use of cglib with the following differences:

* Using this library you can create a proxy to an already existing object, while cglib helps you to create a new object along with the extending proxy object.
* This library creates Java source and does not do JVM byte manipulation.

What is a proxy object
----------------------

If `y` is a proxy object `x` then `y` can be used at any place where you would use `x`. However when you call the method `m()` defined in `x` on `y`  (calling `y.m()`) then it will execute some code that does what the proxy is should do and may or may not call the original `x.m()` method.

For example if the proxy object `y` is an "immutator" (making immutable version of `x`), and the method `m()` may modify the state of the original object `x` then calling `y.m()` may throw an exception. If `n()` is only requesting some value from `x` and does not change the state of `x` then `y.n()` transparently calls `x.n()` and returns what the original method returns.

For the object `y` to be a proxy of `x` it is necessary that the class of `y` extends the class of `x`. Thus

* `ProxyClass extends OriginalClass`

Proxy objects are created using the `ProxyFactory` :

```
ProxyFactory<A> factory = new ProxyFactory<>();
A s = factory.create(a, new Interceptor());
```

The interceptor object implements the interface `MethodInterceptor` that has a single method, `intercept()`. This method is called whenever any of the original objects method is executed. The `MethodInterceptor` interface is very simple:

```
public interface MethodInterceptor {
	Object intercept(Object obj, Method method, Object[] args) throws Exception;
}
```

Teh special functionality of the proxy lies in the interceptor. It is yur place to implement the functionality you want the proxy to do. The method `intercept()` you implement can do whatever you want the proxy functionality to be.

The frist argument of the method is the original object. The second argument is the method, a reflection object that you can use to invoke the method on the original object. The third argument is the array of arguments passed to the method. You can pass these to the original object method unmodified or modified at your will when you apply the reflection invocation.

To see a whole example here is an excerpt from the unit test of djcproxy:

```
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
			if (method.getName().equals("toString")) {
				return "interceptedToString";
			}
			return 0;
		}

	}

	@Test
	public void given_Object_when_CreatingSource_then_GettingInterceptorResult()
			throws Exception {

		A a = new A();
		ProxyFactory<A> factory = new ProxyFactory<>();
		A s = factory.create(a, new Interceptor());
		Assert.assertEquals("interceptedToString", s.toString());
		Assert.assertEquals(0, s.method());
	}
```

The original class is `A`. When the proxy `s` is created it has the same type in the declaration and the same methods can be invoked on it. The interceptor checks the name of the method and if this is `toString()` then it returns a string, otherwise it returns `0`. When we call these methods on the object `s` the return value is not the one that the implementation of `A` would imply but rather the one that is returned by the interceptor.

