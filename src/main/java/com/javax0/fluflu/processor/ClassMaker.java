package com.javax0.fluflu.processor;

import java.io.IOException;
import java.util.Date;

import com.javax0.aptools.InThe;

abstract class ClassMaker {
  final private String   packageName;
  final private String   toBeFluentized;
  final private String   core;
  final protected String className;

  protected String getActualHeader(String s) {
    String filled = InThe.string(s).replace(//
        "className", className,//
        "Core", core,//
        "toBeFluentized", toBeFluentized, //
        "timestamp", new Date().toString());

    return replacePackageName(packageName, filled);
  }

  private String replacePackageName(String packageName, String s) {
    if (packageName == null) {
      s = s.replaceAll("package\\s+packageName;\n", "");
    } else {
      s = InThe.string(s).replace("packageName", packageName);
    }
    return s;
  }

  /**
   * Create a new fluent api implementing class maker to create a fluent api
   * helper class named `className`.
   * 
   * @param packageName
   *          is the package in which the classes are generated.
   * @param className
   *          the name of the class to generate
   * @param core
   *          is the name of the builder class. This is either the original
   *          class that contains the annotation or the class that extends the
   *          original class if the class is abstract.
   * @throws IOException
   */
  ClassMaker(String packageName, String className, String core, String toBeFluentized) throws IOException {
    this.className = className;
    this.packageName = packageName;
    this.core = core;
    this.toBeFluentized = toBeFluentized;
  }

}
