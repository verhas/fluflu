package com.javax0.aptools;

import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;

/**
 * This tool can be used to manage an annotation element.
 * 
 * @author Peter Verhas
 * 
 */
public class ElementTool {
  private final Element element;

  protected ElementTool(Element element) {
    this.element = element;
  }

  private String getKindName(ElementKind kind) {
    Element element = this.element;
    while (element != null && element.getKind() != kind) {
      element = element.getEnclosingElement();
    }
    if (kind.equals(ElementKind.PACKAGE)) {
      return ((PackageElement) element).getQualifiedName().toString();
    } else {
      return element.getSimpleName().toString();
    }
  }

  /**
   * Get the list of the methods from the element.
   * 
   * @return list of the method elements in form of ExecutableElements
   */
  public List<ExecutableElement> getMethods() {
    List<ExecutableElement> methodElements = new LinkedList<>();
    for (Element methodElement : element.getEnclosedElements()) {
      if (methodElement.getKind().equals(ElementKind.METHOD)) {
        methodElements.add((ExecutableElement) methodElement);
      }
    }
    return methodElements;
  }

  /**
   * Get the name of the class that this element represents.
   */
  public String getClassName() {
    return getKindName(ElementKind.CLASS);
  }

  /**
   * True if this element is abstract.
   */
  public boolean isAbstract() {
    return element.getModifiers().contains(Modifier.ABSTRACT);
  }

  /**
   * Get the name of the package that this element (class) is in.
   */
  public String getPackageName() {
    return getKindName(ElementKind.PACKAGE);
  }

  /**
   * Get the named annotation from this element.
   * 
   * @param annotationFullyQualifiedName
   *          the name of the annotation containing the package and the class
   *          name of the annotation interface.
   */
  public AnnotationMirror getTheAnnotation(String annotationFullyQualifiedName) {
    for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
      if (annotationMirror.getAnnotationType().toString().equals(annotationFullyQualifiedName)) {
        return annotationMirror;
      }
    }
    return null;
  }

  /**
   * True if the element has the annotation of the given name.
   * 
   * @param annotationFullyQualifiedName
   *          the name of the annotation containing the package and the class
   *          name of the annotation interface.
   */
  public boolean hasAnnotation(String annotationFullyQualifiedName) {
    return getTheAnnotation(annotationFullyQualifiedName) != null;
  }

}
