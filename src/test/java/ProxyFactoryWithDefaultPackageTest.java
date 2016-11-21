import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.Test;

import com.javax0.djcproxy.MethodInterceptor;
import com.javax0.djcproxy.MethodProxy;
import com.javax0.djcproxy.ProxyFactory;


public class ProxyFactoryWithDefaultPackageTest {

	public static class DefaultPackageClass {
		String foo() {
			return "foo";
		}
	}

	private class Interceptor implements MethodInterceptor {
		@Override
		public Object intercept(Object obj, Method method, Object[] args, MethodProxy mproxy) throws Throwable {
			return method.invoke(args);
		}
	}

	@Test
	public void given_ObjectWithDefaultPackage_then_compileWithoutExceptions() throws Exception {
		DefaultPackageClass realObject = new DefaultPackageClass();

		ProxyFactory<DefaultPackageClass> factory = new ProxyFactory<>();
		DefaultPackageClass proxy = factory.create(realObject, new Interceptor());

		assertEquals("foo", proxy.foo());
	}
}
