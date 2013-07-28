package com.javax0.djcproxy.compiler;

import java.io.StringWriter;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class Compiler {

	private ClassLoader classLoader = Compiler.class.getClassLoader();

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public Class<?> compile(String sourceCode, String className)
			throws Exception {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		List<JavaSourceFromString> sources = new LinkedList<>();
		sources.add(new JavaSourceFromString(className, sourceCode));

		StringWriter sw = new StringWriter();
		MemoryJavaFileManager fm = new MemoryJavaFileManager(
				compiler.getStandardFileManager(null, null, null));
		JavaCompiler.CompilationTask task = compiler.getTask(sw, fm, null,
				null, null, sources);

		Boolean b = task.call();
		System.out.println("Compilation result is " + b);
		System.out.println("Compilation output is " + sw.toString());

		ByteClassLoader byteClassLoader = new ByteClassLoader(new URL[0],
				classLoader, fm.getClassFile().getByteArray());

		Class<?> klass = byteClassLoader.loadClass("Test");
		byteClassLoader.close();
		return klass;
	}

}
