package com.javax0.djcproxy;

import static com.javax0.jscglib.JSCBuilder.argument;
import static com.javax0.jscglib.JSCBuilder.constructor;
import static com.javax0.jscglib.JSCBuilder.field;
import static com.javax0.jscglib.JSCBuilder.klass;
import static com.javax0.jscglib.JSCBuilder.method;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import com.javax0.djcproxy.exceptions.FinalCanNotBeExtendedException;
import com.javax0.djcproxy.utilities.Generics;
import com.javax0.jscglib.JSC;

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

	private void calculateClassName(Class<?> klass) {
		generatedClassName = "PROXY$CLASS$" + klass.getSimpleName();
	}

	private String generatedPackageName;

	public String getGeneratedPackageName() {
		return generatedPackageName;
	}

	private void calculatePackageName(Package originalPackage) {
		if (originalPackage == null) {
			return;
		}

		String originalPackageName = originalPackage.getName();
		final String[] forbiddenPackages = new String[] { "java.", "javax." };
		for (String prefix : forbiddenPackages) {
			if (originalPackageName.startsWith(prefix)) {
				generatedPackageName = "p." + originalPackageName;
				return;
			}
		}
		generatedPackageName = originalPackageName;
	}

	public String create(Class<?> originalClass)
			throws FinalCanNotBeExtendedException {

		klass = originalClass;
		assertClassIsNotFinal();
		calculateClassName(klass);
		calculatePackageName(klass.getPackage());
		builder = klass(generatedClassName)
				.inPackage(generatedPackageName)
				.modifier(
						klass.getModifiers()
								& ~(Modifier.STATIC | Modifier.PROTECTED))
				.parent(klass)
				.interfaces(ProxySetter.class)
				.add(field(originalClass, PROXY_OBJECT_FIELD_NAME).initNull())
				.add(field(MethodInterceptor.class, INTERCEPTOR_FIELD_NAME)
						.initNull())
				.add(method("void", "set" + PROXY_OBJECT_FIELD_NAME)
						.modifier(Modifier.PUBLIC)
						.arguments(
								argument(Object.class, PROXY_OBJECT_FIELD_NAME))
						.command(
								"this." + PROXY_OBJECT_FIELD_NAME + " = ("
										+ originalClass.getCanonicalName()
										+ ")" + PROXY_OBJECT_FIELD_NAME))
				.add(method("void", "set" + INTERCEPTOR_FIELD_NAME)
						.modifier(Modifier.PUBLIC)
						.arguments(
								argument(MethodInterceptor.class,
										INTERCEPTOR_FIELD_NAME))
						.command(
								"this." + INTERCEPTOR_FIELD_NAME + " = "
										+ INTERCEPTOR_FIELD_NAME));
		for (Constructor<?> constructor : klass.getConstructors()) {
			Type[] types = constructor.getGenericParameterTypes();
			builder.add(constructor(builder)
					.arguments(getArguments(types))
					.command(
							"super("
									+ getCommaSeparatedArgumentLists(types.length)
									+ ")"));
		}
		for (Method method : originalClass.getMethods()) {
			if (!isBridge(method)) {
				appendMethodSource(method);
			}
		}
		return builder.toString();
	}

	private boolean isBridge(Method method) {
		return Modifier.isVolatile(method.getModifiers());
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
			if (intercept) {
				createProxyClassField(method);
			}
			createProxyMethod(method, intercept);
		}
	}

	private JSC[] getArguments(Type[] types) {
		List<String> argTypeList = new LinkedList<>();
		for (Type type : types) {
			argTypeList.add(Generics.typeToString(type));
		}
		JSC[] arguments = new JSC[argTypeList.size()];

		int i = 0;
		for (String type : argTypeList) {
			arguments[i] = argument(type, "p" + i);
			i++;
		}
		return arguments;
	}

	private String getCommaSeparatedArgumentLists(int size) {
		String argnames = "";
		String sep = "";
		for (int i = 0; i < size; i++) {
			argnames += sep + "p" + i;
			sep = ",";
		}
		return argnames;
	}

	private String getCommaSeparatedArgumentTypeLists(Class<?>[] parameters) {
		String types = "";
		String sep = "";
		for (Class<?> parameter : parameters) {
			types += sep + parameter.getCanonicalName() + ".class";
			sep = ",";
		}
		return types;
	}

	private void appendReturnOptionally(StringBuilder sb, String returnType) {
		if (!"void".equals(returnType)) {
			sb.append("return ");
		}
	}

	private static String calculateMethodProxyFieldName(Method method) {
		StringBuilder parameterTypes = new StringBuilder();
		for(Class<?> parameterClazz: method.getParameterTypes()) {
			parameterTypes
					.append('_')
					.append(parameterClazz.getName().replace('.', '_'));
		}
		return method.getName() + parameterTypes.toString() + "_MethodProxyInstance";
	}

	private void createProxyClassField(Method method) {
		JSC field = field(calculateMethodProxyFieldName(method))
				.returnType(MethodProxy.class).modifier(Modifier.PRIVATE)
				.initValue("null");
		builder.add(field);
	}

	/**
	 * For every intercepted method there is a class and an instance of the
	 * class that implements the interface {@link MethodProxy}. Using this
	 * method proxy it is possible to call the original method without
	 * reflection being invoked. This is the same concept implemented in cglib,
	 * though the interface and the naming is not 100% compatible.
	 */
	private String createMethodProxy(Method method) {
		JSC invoke = method("invoke")
				.modifier(Modifier.PUBLIC)
				.arguments(argument(Object.class, "obj"),
						argument((new Object[0]).getClass(), "args"))
				.exceptions("Throwable").returnType(Object.class);
		StringBuilder sb = new StringBuilder();
		sb.append("((").append(method.getDeclaringClass().getCanonicalName())
				.append(")obj).").append(method.getName()).append("(");
		Class<?>[] parameterTypes = method.getParameterTypes();
		int paramNumber = parameterTypes.length;
		for (int index = 0; index < paramNumber; index++) {
			if (index > 0) {
				sb.append(",");
			}
			if (!parameterTypes[index].equals(Object.class)) {
				sb.append("(").append(parameterTypes[index].getCanonicalName())
						.append(")");
			}
			sb.append("args[" + index + "]");
		}
		sb.append(")");
		if (method.getReturnType().getCanonicalName().equals("void")) {
			sb.append("; return null");
		} else {
			sb.insert(0, "return ");
		}
		invoke.command(sb.toString());
		return invoke.toString();
	}

	/**
	 * Since the proxied object is not the super object of the proxy object the
	 * method call has to be implemented in the proxy in all cases. The only
	 * difference is that
	 * <ul>
	 * <li>has to be passed to the set interceptor if this method is to be
	 * intercepted
	 * <li>the original method on the proxied object has to be called directly
	 * from this method if the method is not to be intercepted.
	 * </ul>
	 * 
	 * @param method
	 *            the method that is to be intercepted or invoked directly
	 * @param intercept
	 *            true if the method has to be intercepted.
	 */
	private void createProxyMethod(Method method, boolean intercept) {
		final String returnType = Generics.typeToString(method
				.getGenericReturnType());
		final String name = method.getName();
		Class<?>[] parameters = method.getParameterTypes();
		JSC[] arguments = getArguments(method.getGenericParameterTypes());
		String argnames = getCommaSeparatedArgumentLists(parameters.length);

		String types = getCommaSeparatedArgumentTypeLists(parameters);

		StringBuilder sb = new StringBuilder();
		if (intercept) {
			sb.append("\ntry{\n");
			String methodProxyFieldName = calculateMethodProxyFieldName(method);
			sb.append("if( null == ").append(methodProxyFieldName).append("){")
					.append(methodProxyFieldName)
					.append(" = new com.javax0.djcproxy.MethodProxy() {")
					.append(createMethodProxy(method)).append("};}");
			if (!"void".equals(returnType)) {
				sb.append("return ("
						+ method.getReturnType().getCanonicalName() + ")");
			}
			sb.append(INTERCEPTOR_FIELD_NAME + ".intercept(\n"
					+ PROXY_OBJECT_FIELD_NAME + ", ");
			sb.append(PROXY_OBJECT_FIELD_NAME + ".getClass().getMethod(\""
					+ method.getName() + "\", ");
			sb.append("new Class[]{" + types + "}),\n new Object[]{" + argnames
					+ "},");
			sb.append(calculateMethodProxyFieldName(method));
			sb.append(");\n");
			sb.append("}catch(Throwable e){\nthrow new RuntimeException(e);\n}\n");
		} else {
			appendReturnOptionally(sb, returnType);
			sb.append(PROXY_OBJECT_FIELD_NAME).append(".")
					.append(method.getName()).append("(").append(argnames)
					.append(");");
		}
		builder.add(method(returnType, name)
				.annotation("@Override")
				.modifier(
						method.getModifiers()
								& ~(Modifier.NATIVE | Modifier.ABSTRACT))
				.arguments(arguments).commandBlock(sb.toString())
				.exceptions(method.getExceptionTypes()));
	}

}
