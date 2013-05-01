package com.javax0.aptools;

import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;

public class ElementTool {
	private Element element;

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

	public List<ExecutableElement> getMethods() {
		List<ExecutableElement> methodElements = new LinkedList<>();
		for (Element methodElement : element.getEnclosedElements()) {
			if (methodElement.getKind().equals(ElementKind.METHOD)) {
				methodElements.add((ExecutableElement) methodElement);
			}
		}
		return methodElements;
	}

	public String getClassName() {
		return getKindName(ElementKind.CLASS);
	}

	public boolean isAbstract() {
		return element.getModifiers().contains(Modifier.ABSTRACT);
	}

	public String getPackageName() {
		return getKindName(ElementKind.PACKAGE);
	}

	public AnnotationMirror getTheAnnotation(String annotationFullyQualifiedName) {
		for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
			if (annotationMirror.getAnnotationType().toString()
					.equals(annotationFullyQualifiedName)) {
				return annotationMirror;
			}
		}
		return null;
	}

	public boolean hasAnnotation(String annotationFullyQualifiedName) {
		return getTheAnnotation(annotationFullyQualifiedName) != null;
	}

}
