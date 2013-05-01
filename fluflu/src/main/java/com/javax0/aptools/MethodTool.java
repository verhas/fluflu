package com.javax0.aptools;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

/**
 * Tool class to manage method.
 * 
 * @author Peter Verhas
 * 
 */
public class MethodTool {
  private final ExecutableElement methodElement;

  protected MethodTool(ExecutableElement methodElement) {
    this.methodElement = methodElement;
  }

  public String getJavadoc() {
    ProcessingEnvironment processingEnv = Environment.get();
    String javaDoc = processingEnv.getElementUtils().getDocComment(methodElement);
    if (javaDoc == null) {
      javaDoc = "";
    }
    return javaDoc;
  }

  /**
   * Get the number of the parameters of the method.
   */
  public int getTheNumberOfParameters() {
    return methodElement.getParameters().size();
  }

  /**
   * Get an array of lists containing the annotations of the arguments.
   */
  public List<? extends AnnotationMirror>[] getParameterAnnotations() {
    @SuppressWarnings("unchecked")
    List<? extends AnnotationMirror>[] annotationMirrorss = new List[getTheNumberOfParameters()];
    int i = 0;
    for (VariableElement parameterElement : methodElement.getParameters()) {
      annotationMirrorss[i] = parameterElement.getAnnotationMirrors();
      i++;
    }
    return annotationMirrorss;
  }

  /**
   * True if the method is abstract.
   */
  public boolean isAbstract() {
    return methodElement.getModifiers().contains(Modifier.ABSTRACT);
  }

  private String[] argumentNames = null;

  /**
   * Get an argument name that can be used as the {@code i}-th argument.
   * 
   * @param i
   *          zero based index of the argument
   * @return
   */
  public String getArgumentName(int i) {
    if (argumentNames == null) {
      argumentNames = new String[getTheNumberOfParameters()];
      int j = 0;
      for (VariableElement parameterElement : methodElement.getParameters()) {
        argumentNames[j] = parameterElement.getSimpleName().toString();
        j++;
      }
    }
    return argumentNames[i];
  }

  /**
   * Return a string that can be used in a Java code as the argument list for
   * the method.
   * 
   * @return
   */
  public String createParamList() {
    StringBuilder arglist = new StringBuilder();
    String sep = "";

    for (int i = 0; i < getTheNumberOfParameters(); i++) {
      arglist.append(sep).append(getArgumentName(i));
      sep = ", ";
    }
    return arglist.toString();
  }

  public String getReturnType() {
    return methodElement.getReturnType().toString();
  }

  public String getName() {
    return methodElement.getSimpleName().toString();
  }

  private final String JAVA_LANG_PREFIX = "java.lang.";

  private void removeJavaLang(StringBuilder declaration) {

    if (declaration.indexOf(JAVA_LANG_PREFIX) == 0) {
      declaration.delete(0, JAVA_LANG_PREFIX.length());
    }
  }

  private void convertArrayToVararg(StringBuilder declaration) {
    declaration.delete(declaration.length() - 2, declaration.length()).append(" ... ");
  }

  private boolean thisIsTheLastArgument(int i) {
    return i == getTheNumberOfParameters() - 1;
  }

  /**
   * Create and return the string that can be used in the generated Java source
   * code as argument list (argument types and argument names separated by
   * commas).
   */
  public String createArgList() {
    StringBuilder arglist = new StringBuilder();
    String sep = "";
    int i = 0;
    for (VariableElement parameterElement : methodElement.getParameters()) {
      final StringBuilder declaration = new StringBuilder(parameterElement.asType().toString());
      removeJavaLang(declaration);
      if (methodElement.isVarArgs() && thisIsTheLastArgument(i)) {
        convertArrayToVararg(declaration);
      }
      arglist.append(sep).append(declaration).append(" ").append(getArgumentName(i));
      sep = ", ";
      i++;
    }
    return arglist.toString();
  }
}
