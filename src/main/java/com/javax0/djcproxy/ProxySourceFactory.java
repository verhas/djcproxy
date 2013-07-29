package com.javax0.djcproxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import com.javax0.djcproxy.exceptions.FinalCanNotBeExtendedException;
import com.javax0.djcproxy.utilities.Generics;

class ProxySourceFactory<Proxy> {
	private final CallbackFilter callbackFilter;

	public ProxySourceFactory(CallbackFilter callbackFilter) {
		this.callbackFilter = callbackFilter;
	}

	private StringBuilder sourceBuilder;
	private Class<?> klass;

	private String generatedClassName;
	
	public String getGeneratedClassName() {
		return generatedClassName;
	}

	public String create(Proxy originalObject)
			throws FinalCanNotBeExtendedException {
		sourceBuilder = new StringBuilder();
		klass = originalObject.getClass();
		assertClassIsNotFinal();
		appendClassStartSource();
		for (Method method : originalObject.getClass().getMethods()) {
			appendMethodSource(method);
		}
		appendClassEndSource();
		return sourceBuilder.toString();
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

	private void appendClassStartSource() {
		sourceBuilder.append(klass.getPackage().toString());
		sourceBuilder.append(";\n");
		if ((klass.getModifiers() & Modifier.PUBLIC) > 0) {
			sourceBuilder.append("public ");
		}
		sourceBuilder.append("class ");
		generatedClassName = "PROXY$CLASS$" + klass.getSimpleName();
		sourceBuilder.append(generatedClassName);
		sourceBuilder.append(" ");
		sourceBuilder.append("extends ");
		sourceBuilder.append(klass.getName().replaceAll("\\$", "."));
		sourceBuilder.append(" ");
		sourceBuilder.append("implements ");
		sourceBuilder.append(ProxySetter.class.getCanonicalName());
		sourceBuilder.append("{\n");
		sourceBuilder.append("private Object " + PROXY_OBJECT_FIELD_NAME
				+ " = null;\n");
		sourceBuilder.append("private com.javax0.djcproxy.MethodInterceptor "
				+ INTERCEPTOR_FIELD_NAME + " = null;\n");
		sourceBuilder.append("public void set" + PROXY_OBJECT_FIELD_NAME
				+ "(Object " + PROXY_OBJECT_FIELD_NAME + "){ this."
				+ PROXY_OBJECT_FIELD_NAME + " = " + PROXY_OBJECT_FIELD_NAME
				+ "; }\n");
		sourceBuilder.append("public void set" + INTERCEPTOR_FIELD_NAME
				+ "(com.javax0.djcproxy.MethodInterceptor "
				+ INTERCEPTOR_FIELD_NAME + "){ this." + INTERCEPTOR_FIELD_NAME
				+ " = " + INTERCEPTOR_FIELD_NAME + "; }\n");
	}

	private void appendMethodSource(Method method) {
		if ((method.getModifiers() & Modifier.FINAL) == 0
				&& (callbackFilter == null || callbackFilter.accept(method))) {
			createInterceptorMethod(method);
		}

	}

	private void createInterceptorMethod(Method method) {
		final String returnType = Generics.typeToString(method
				.getGenericReturnType());
		final String name = method.getName();
		List<String> argTypeList = new LinkedList<>();
		for (Type type : method.getGenericParameterTypes()) {
			argTypeList.add(Generics.typeToString(type));
		}
		sourceBuilder.append("@Override ");
		if ((method.getModifiers() & Modifier.PROTECTED) > 0) {
			sourceBuilder.append("protected ");
		}
		if ((method.getModifiers() & Modifier.PUBLIC) > 0) {
			sourceBuilder.append("public ");
		}
		sourceBuilder.append(returnType);
		sourceBuilder.append(" ");
		sourceBuilder.append(name);
		sourceBuilder.append("(");
		String argnames = "";
		String sep = "";
		int i = 0;
		for (String type : argTypeList) {
			i++;
			sourceBuilder.append(sep);
			sourceBuilder.append(type);
			sourceBuilder.append(" ");
			sourceBuilder.append("p" + i);
			argnames = sep + "p" + i;
			sep = ",";
		}
		sourceBuilder.append("){\n");
		Class<?>[] parameters = method.getParameterTypes();

		String types = "";
		i = 0;
		sep = "";
		for (Class<?> parameter : parameters) {
			i++;
			types += sep + parameter.getCanonicalName() + ".class";
			sep = ",";
		}
		sourceBuilder.append("\ntry{\n");
		if (!"void".equals(returnType)) {
			sourceBuilder.append("return ("
					+ method.getReturnType().getCanonicalName() + ")");
		}
		sourceBuilder.append(INTERCEPTOR_FIELD_NAME + ".intercept(\n"
				+ PROXY_OBJECT_FIELD_NAME + ", ");
		sourceBuilder.append(PROXY_OBJECT_FIELD_NAME
				+ ".getClass().getMethod(\"" + method.getName() + "\", ");
		sourceBuilder.append("new Class[]{" + types + "}),\n new Object[]{"
				+ argnames + "});\n");
		sourceBuilder
				.append("}catch(Exception e){\nthrow new RuntimeException();\n}\n");

		createMethodEnd();
	}

	private void createMethodEnd() {
		sourceBuilder.append("}\n");
	}

	private void appendClassEndSource() {
		sourceBuilder.append("}\n");
	}

}
