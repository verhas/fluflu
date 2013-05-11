package com.javax0.fluflu.processor;

import java.io.IOException;

import com.javax0.aptools.FromThe;
import com.javax0.aptools.InThe;

class StateClassMaker extends ClassMaker {

  final private static String stateClassHeaderTemplate = Template.load("stateClassHeaderTemplate.java");
  final private static String methodTemplate           = Template.load("methodTemplate.java");
  final private static String endMethodTemplate        = Template.load("endMethodTemplate.java");

  StateClassMaker(String packageName, String className, String core, String toBeFluentized)
      throws IOException {
    super(packageName, className, core, toBeFluentized);
  }

  /**
   * Get the footer text of a state class
   * 
   * @return the java source code as string
   */
  String generateStateClassFooter() {
    return "\n}";
  }

  /**
   * Get the state class header from the template `stateClassHeaderTemplate` and
   * replace the place holders `methodName`, `arglist`, `paramlist`, `toState`.
   * 
   * @return the java source code as string
   */
  String generateStateClassHeader() {
    return getActualHeader(stateClassHeaderTemplate);
  }

  /**
   * Generate the state class method that represents the transition.
   * 
   * @param edge
   *          from one transition to another
   * 
   * @return the java source code as string
   */
  String generateStateClassMethod(TransitionEdge edge) {
    final String arglist = FromThe.method(edge.method).createArgList();
    final String paramlist = FromThe.method(edge.method).createParamList();
    final String javaDoc = FromThe.method(edge.method).getJavadoc();
    return replaceMethodParams(edge.end ? endMethodTemplate : methodTemplate, edge, arglist, paramlist, javaDoc);
  }

  private String replaceMethodParams(String s, TransitionEdge edge, String arglist, String paramlist, String javaDoc) {
    final String implementedName = edge.method.getSimpleName().toString();
    final String generatedName = "".equals(edge.name) ? implementedName : edge.name;
    final String returnCommandLiteral = "void".equals(edge.targetState) ? "" : "return";

    return InThe.string(s).replace(//
        "methodName", generatedName,//
        "implementedName", implementedName,//
        "arglist", arglist,//
        "paramlist", paramlist,//
        "toState", edge.targetState, //
        "return", returnCommandLiteral,//
        "javaDoc", javaDoc);
  }

}
