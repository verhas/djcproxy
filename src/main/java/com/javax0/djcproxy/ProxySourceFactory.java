package com.javax0.djcproxy;

import java.lang.reflect.Constructor;
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

	private void calculateClassName(Class<?> klass) {
		generatedClassName = "PROXY$CLASS$" + klass.getSimpleName();
	}

	private String generatedPackageName;

	public String getGeneratedPackageName() {
		return generatedPackageName;
	}

	private void calculatePackageName(Package originalPackage) {
		String originalPackageName = originalPackage.getName();
		final String[] forbiddenPackages = new String[] { "java.", "javax." };
		for (String prefix : forbiddenPackages) {
			if (originalPackageName.startsWith(prefix)) {
				generatedPackageName = "p."+ originalPackageName;
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
			builder.constructor(constructor(builder).arguments(
					getArguments(types)).command(
					"super(" + getCommaSeparatedArgumentLists(types.length)
							+ ")"));
		}
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

	private void createInterceptorMethod(Method method, boolean intercept) {
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
				.arguments(arguments).commandBlock(sb.toString()));
	}

}
