package com.javax0.djcproxy;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class Compiler {
	public Class<?> compile(String sourceCode) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			NoSuchMethodException, SecurityException, IllegalArgumentException,
			InvocationTargetException, IOException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		String source = "public class Test { public void hello(){ System.out.println(\"hello\");}}";
		List<JavaSourceFromString> sources = new LinkedList<>();
		sources.add(new JavaSourceFromString("Test", source));
		StringWriter sw = new StringWriter();
		InMemoryOutputJavaFileManager fm = new InMemoryOutputJavaFileManager(
				compiler.getStandardFileManager(null, null, null));
		JavaCompiler.CompilationTask task = compiler.getTask(sw, fm, null,
				null, null, sources);

		Boolean b = task.call();
		System.out.println("Compilation result is " + b);
		System.out.println("Compilation output is " + sw.toString());

		ClassLoader myClassLoader = new ByteClassLoader(new URL[0], this
				.getClass().getClassLoader(), fm.getClassFile().getByteArray());
		
		Class<?> klass = myClassLoader.loadClass("Test");
		Object object = klass.newInstance();
		Method method = klass.getMethod("hello", new Class[0]);
		method.invoke(object, new Object[0]);
		return null;
	}

	public static void main(String[] args) {
		Compiler c = new Compiler();
		try {
			c.compile("");
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | NoSuchMethodException
				| SecurityException | IllegalArgumentException | IOException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
