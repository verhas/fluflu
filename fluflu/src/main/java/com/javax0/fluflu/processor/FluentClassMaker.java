package com.javax0.fluflu.processor;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import com.javax0.aptools.FromThe;
import com.javax0.aptools.InThe;
import com.javax0.aptools.The;
import com.javax0.fluflu.AddTo;
import com.javax0.fluflu.AssignTo;

public class FluentClassMaker {
  final String className;
  final String packageName;
  final String core;
  final String toBeFluentized;

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
  public FluentClassMaker(String packageName, String className, String core, String toBeFluentized) throws IOException {
    this.className = className;
    this.packageName = packageName;
    this.core = core;
    this.toBeFluentized = toBeFluentized;
  }

  final private static String stateClassHeaderTemplate  = Template.load("stateClassHeaderTemplate.java");
  final private static String methodTemplate            = Template.load("methodTemplate.java");
  final private static String endMethodTemplate         = Template.load("endMethodTemplate.java");
  final private static String fluentClassHeaderTemplate = Template.load("fluentClassHeaderTemplate.java");
  final private static String startMethodTemplate       = Template.load("startMethodTemplate.java");
  final private static String fluentMethodTemplate      = Template.load("fluentMethodTemplate.java");

  private String replacePackageName(String packageName, String s) {
    if (packageName == null) {
      s = s.replaceAll("package\\s+packageName;\n", "");
    } else {
      s = InThe.string(s).replace("packageName", packageName);
    }
    return s;
  }

  private String getActualHeader(String s) {
    String filled = InThe.string(s).replace(//
        "className", className,//
        "Core", core,//
        "toBeFluentized", toBeFluentized, //
        "timestamp", new Date().toString());

    return replacePackageName(packageName, filled);
  }

  private String replaceMethodParams(String s, TransitionEdge edge, String arglist, String paramlist) {
    return InThe.string(s).replace(//
        "methodName", edge.method.getSimpleName().toString(),//
        "arglist", arglist,//
        "paramlist", paramlist,//
        "toState", edge.targetState);
  }

  /**
   * Get the state class header from the template `stateClassHeaderTemplate` and
   * replace the place holders `methodName`, `arglist`, `paramlist`, `toState`.
   * 
   * @return the java source code as string
   */
  public String generateStateClassHeader() {
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
  public String generateStateClassMethod(TransitionEdge edge) {
    String arglist = FromThe.method(edge.method).createArgList();
    String paramlist = FromThe.method(edge.method).createParamList();
    return replaceMethodParams(edge.targetState == null ? endMethodTemplate : methodTemplate, edge, arglist, paramlist);
  }

  /**
   * Get the footer text of a state class
   * 
   * @return the java source code as string
   */
  public String generateStateClassFooter() {
    return "\n}";
  }

  /**
   * Generate the header of the fluent class.
   * 
   * @param startState
   *          the state where the fluent api starts
   * @param startMethod
   *          the method that is the start method and can be used as a state
   *          class factory
   * @return the java source code as string
   */
  public String generateFluentClassHeader(String startState, String startMethod) {
    return getActualHeader(fluentClassHeaderTemplate);
  }

  /**
   * Generate the start method source code.
   * 
   * @param startState
   *          the state where the fluent api starts
   * @param startMethod
   *          the method that is the start method and can be used as a state
   *          class factory
   * @return the java source code as string
   */
  public String generateStartMethod(String startState, String startMethod) {
    return InThe.string(startMethodTemplate).replace(//
        "startState", startState,//
        "startMethod", startMethod,//
        "className", className);
  }

  /**
   * Generate the last few lines of the fluent class.
   * 
   * @return the java source code as string
   */
  public String generateFluentClassFooter() {
    return "\n}";
  }

  private String generateFluentMethod(ExecutableElement methodElement) {
    String returnType = FromThe.method(methodElement).getReturnType();
    String methodName = FromThe.method(methodElement).getName();
    String arglist = FromThe.method(methodElement).createArgList();
    StringBuilder setterBody = new StringBuilder();

    List<? extends AnnotationMirror>[] parameterAnnotations = FromThe.method(methodElement).getParameterAnnotations();
    int i = 0;
    for (List<? extends AnnotationMirror> annotations : parameterAnnotations) {
      String par = FromThe.method(methodElement).getArgumentName(i);
      i++;
      for (AnnotationMirror annotation : annotations) {

        String fieldName = FromThe.annotation(annotation).getStringValue();
        if (annotation.getAnnotationType().toString().equals(AssignTo.class.getCanonicalName())) {
          setterBody.append("    ").append("core.").append(fieldName).append(" = ").append(par).append(";\n");
        } else if (annotation.getAnnotationType().toString().equals(AddTo.class.getCanonicalName())) {
          setterBody.append("    ").append("core.").append(fieldName).append(".add(").append(par).append(");\n");
        }
        System.out.println("Annotation class= " + annotation.getAnnotationType());
      }
      System.out.println("setter body " + setterBody.toString());
    }
    return InThe.string(fluentMethodTemplate).replace(//
        "Core", className,//
        "returnType", returnType,//
        "methodName", methodName,//
        "arglist", arglist,//
        "setterBody", setterBody.toString()//
        );
  }

  /**
   * Generate the methods for the fluent class.
   * 
   * @param classElement
   * 
   * @return the java source code
   */
  public StringBuilder generateFluentClassMethods(Element classElement) {
    StringBuilder body = new StringBuilder();
    List<ExecutableElement> methodElements = FromThe.element(classElement).getMethods();
    for (ExecutableElement methodElement : methodElements) {
      if (The.method(methodElement).isAbstract()) {
        body.append(generateFluentMethod(methodElement));
      }
    }
    return body;
  }

}
