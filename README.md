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

ProxyFactory< A > factory = new ProxyFactory<>();
 
A s = factory.create(a, new Interceptor());

```



Some of the proxy methods call the original method on the original class, while other methods intercept the call calling the method `intercept()` of an interceptor object (see details later).



A proxy object is an object that is an instance of a class that extends the class of the original object and can be used in the place of the original object, however instead calling the original methods of the original object the proxy object calls an interceptor method. The interceptor method can implement any functionality and can even call the original method.

To get a proxy object the caller can create a `ProxyFactory` and use the factory to generate proxy objects.

```
ProxyFactory<A> factory = new ProxyFactory<>();
A s = factory.create(a, new Interceptor());
```

The first argument to the factory method `create()` is the original object, the second is an interceptor. The interceptor object implements the interface `MethodInterceptor` that has a single method, `intercept()`. This method is called whenever any of the original objects method is executed. The `MethodInterceptor` interface is very simple:

```
public interface MethodInterceptor {
	Object intercept(Object obj, Method method, Object[] args) throws Exception;
}
```



In the following example the test method extends the class `A`. The class interceptor is a SAM (single abstract method) that can be defined as a closure in Java 8. The interceptor is invoked by the proxy object and the interceptor method can call the original method zero or more times and perform other tasks as well at the discretion of the interceptor.

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

The sample above is from the unit test that documents the usage of the 

