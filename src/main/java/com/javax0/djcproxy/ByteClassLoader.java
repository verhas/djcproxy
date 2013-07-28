package com.javax0.djcproxy;

import java.net.URL;
import java.net.URLClassLoader;

public class ByteClassLoader extends URLClassLoader {
    private final byte[] classFile;

    public ByteClassLoader(URL[] urls, ClassLoader parent, final byte[] classFile) {
      super(urls, parent);
      this.classFile = classFile;
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
      if (classFile != null) {
        return defineClass(name, classFile, 0, classFile.length); 
      }
      return super.findClass(name);
    }

  }
