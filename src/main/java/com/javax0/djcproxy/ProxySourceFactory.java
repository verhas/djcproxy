package com.javax0.djcproxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import com.javax0.djcproxy.exceptions.FinalCanNotBeExtendedException;
import com.javax0.djcproxy.utilities.Generics;
import com.javax0.jscglib.JSC;
import static com.javax0.jscglib.JSCBuilder.*;

class ProxySourceFactory<Proxy> {
	private final CallbackFilter callbackFilter;

	public ProxySourceFactory(CallbackFilter callbackFilter) {
		this.callbackFilter = callbackFilter;
	}

	private JSC builder;
	private Class<?> klass;

	private String generatedClassName;

	public String getGeneratedClassName() {
		return generatedClassName;
	}

	public String create(Class<?> originalClass)
			throws FinalCanNotBeExtendedException {

		klass = originalClass;
		assertClassIsNotFinal();
		generatedClassName = "PROXY$CLASS$" + klass.getSimpleName();
		builder = klass(generatedClassName)
				.inPackage(klass.getPackage())
				.modifier(
						klass.getModifiers()
								& ~(Modifier.STATIC | Modifier.PROTECTED))
				.parent(klass)
				.interfaces(ProxySetter.class)
				.add(field(Object.class, PROXY_OBJECT_FIELD_NAME).initNull())
				.add(field(MethodInterceptor.class, INTERCEPTOR_FIELD_NAME)
						.initNull())
				.add(method("void", "set" + PROXY_OBJECT_FIELD_NAME)
						.modifier(Modifier.PUBLIC)
						.arguments(
								argument(Object.class, PROXY_OBJECT_FIELD_NAME))
						.command(
								"this." + PROXY_OBJECT_FIELD_NAME + " = "
										+ PROXY_OBJECT_FIELD_NAME))
				.add(method("void", "set" + INTERCEPTOR_FIELD_NAME)
						.modifier(Modifier.PUBLIC)
						.arguments(
								argument(MethodInterceptor.class,
										INTERCEPTOR_FIELD_NAME))
						.command(
								"this." + INTERCEPTOR_FIELD_NAME + " = "
										+ INTERCEPTOR_FIELD_NAME))
				.constructor();
		for (Method method : originalClass.getMethods()) {
			appendMethodSource(method);
		}
		return builder.toString();
	}

	private void assertClassIsNotFinal() throws FinalCanNotBeExtendedException {

		if ((klass.getModifiers() & Modifier.FINAL) > 0) {
			throw new FinalCanNotBeExtendedException("The class '"
					+ klass.getCanonicalName()
					+ "' is final. Proxy can not be created for final classes");
		}
	}

	private static final String PROXY_OBJECT_FIELD_NAME = "PROXY$OBJECT";
	private static final String INTERCEPTOR_FIELD_NAME = "PROXY$INTERCEPTOR";

	private void appendMethodSource(Method method) {
		if ((method.getModifiers() & Modifier.FINAL) == 0) {
			boolean intercept = callbackFilter == null
					|| callbackFilter.accept(method);
			createInterceptorMethod(method, intercept);
		}
	}

	private void createInterceptorMethod(Method method, boolean intercept) {
		final String returnType = Generics.typeToString(method
				.getGenericReturnType());
		final String name = method.getName();
		List<String> argTypeList = new LinkedList<>();
		for (Type type : method.getGenericParameterTypes()) {
			argTypeList.add(Generics.typeToString(type));
		}
		JSC[] arguments = new JSC[argTypeList.size()];

		String argnames = "";
		String sep = "";
		int i = 0;
		for (String type : argTypeList) {
			arguments[i] = argument(type, "p" + i);
			argnames += sep + "p" + i;
			sep = ",";
			i++;
		}

		Class<?>[] parameters = method.getParameterTypes();

		String types = "";
		i = 0;
		sep = "";
		for (Class<?> parameter : parameters) {
			i++;
			types += sep + parameter.getCanonicalName() + ".class";
			sep = ",";
		}
		StringBuilder sb = new StringBuilder();
		if (intercept) {
			sb.append("\ntry{\n");
			if (!"void".equals(returnType)) {
				sb.append("return ("
						+ method.getReturnType().getCanonicalName() + ")");
			}
			sb.append(INTERCEPTOR_FIELD_NAME + ".intercept(\n"
					+ PROXY_OBJECT_FIELD_NAME + ", ");
			sb.append(PROXY_OBJECT_FIELD_NAME + ".getClass().getMethod(\""
					+ method.getName() + "\", ");
			sb.append("new Class[]{" + types + "}),\n new Object[]{" + argnames
					+ "});\n");
			sb.append("}catch(Exception e){\nthrow new RuntimeException(e);\n}\n");
		} else {
			if (!"void".equals(returnType)) {
				sb.append("return ");
			}
			sb.append(PROXY_OBJECT_FIELD_NAME).append(".")
					.append(method.getName()).append("(").append(argnames)
					.append(");");
		}
		builder.add(method(returnType, name)
				.annotation("@Override")
				.modifier(
						method.getModifiers()
								& ~(Modifier.NATIVE | Modifier.ABSTRACT))
				.arguments(arguments).commandBlock(sb.toString()));
	}

}
