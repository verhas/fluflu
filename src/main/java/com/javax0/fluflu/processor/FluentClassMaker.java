package com.javax0.fluflu.processor;

import java.io.IOException;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import com.javax0.aptools.FromThe;
import com.javax0.aptools.InThe;
import com.javax0.aptools.The;
import com.javax0.fluflu.AddTo;
import com.javax0.fluflu.AssignTo;

class FluentClassMaker extends ClassMaker {
  private String              clonerMethodName;
  final private static String fluentClassHeaderTemplate      = Template.load("fluentClassHeaderTemplate.java");
  final private static String fluentMethodTemplate           = Template.load("fluentMethodTemplate.java");
  final private static String fluentMethodTemplateWithCloner = Template.load("fluentMethodTemplateWithCloner.java");
  final private static String startMethodTemplate            = Template.load("startMethodTemplate.java");

  FluentClassMaker(String packageName, String className, String core, String toBeFluentized, String clonerMethodName)
      throws IOException {
    super(packageName, className, core, toBeFluentized);
    this.clonerMethodName = clonerMethodName;
  }

  /**
   * Generate the last few lines of the fluent class.
   * 
   * @return the java source code as string
   */
  String generateFluentClassFooter() {
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
  String generateFluentClassHeader(String startState, String startMethod) {
    return getActualHeader(fluentClassHeaderTemplate);
  }

  /**
   * Generate the methods for the fluent class.
   * 
   * @param classElement
   * 
   * @return the java source code
   */
  StringBuilder generateFluentClassMethods(Element classElement) {
    StringBuilder body = new StringBuilder();
    List<ExecutableElement> methodElements = FromThe.element(classElement).getMethods();
    for (ExecutableElement methodElement : methodElements) {
      if (The.method(methodElement).isAbstract()) {
        body.append(generateFluentMethod(methodElement));
      }
    }
    return body;
  }

  /**
   * Generate a method in the class that extends the abstract to be fluentized
   * class and which method stores the passed arguments to the private fields
   * denoted by the annotations on the method arguments.
   * 
   * @param methodElement
   * @return
   */
  private String generateFluentMethod(ExecutableElement methodElement) {
    String returnType = FromThe.method(methodElement).getReturnType();
    String methodName = FromThe.method(methodElement).getName();
    String arglist = FromThe.method(methodElement).createArgList();
    StringBuilder setterBody = new StringBuilder("\n");

    List<? extends AnnotationMirror>[] parameterAnnotations = FromThe.method(methodElement).getParameterAnnotations();
    int i = 0;
    for (List<? extends AnnotationMirror> annotations : parameterAnnotations) {
      String par = FromThe.method(methodElement).getArgumentName(i);
      i++;
      for (AnnotationMirror annotation : annotations) {

        String fieldName = FromThe.annotation(annotation).getStringValue();
        if (annotation.getAnnotationType().toString().equals(AssignTo.class.getCanonicalName())) {
          setterBody.append("\t\t").append("core.").append(fieldName).append(" = ").append(par).append(";\n");
        } else if (annotation.getAnnotationType().toString().equals(AddTo.class.getCanonicalName())) {
          setterBody.append("\t\t").append("core.").append(fieldName).append(".add(").append(par).append(");\n");
        }
      }
    }
    return InThe.string(clonerMethodName == null ? fluentMethodTemplate : fluentMethodTemplateWithCloner).replace(//
        "Core", className,//
        "returnType", returnType,//
        "methodName", methodName,//
        "arglist", arglist,//
        "setterBody", setterBody.toString(),//
        "clonerMethodName", clonerMethodName//
        );
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
  String generateStartMethod(String startState, String startMethod) {
    return InThe.string(startMethodTemplate).replace(//
        "startState", startState,//
        "startMethod", startMethod,//
        "className", className);
  }

}
