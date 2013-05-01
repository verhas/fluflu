package com.javax0.aptools;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public abstract class AbstractToolFactory {
	public static ElementTool element(Element element) {
		return new ElementTool(element);
	}

	public static AnnotationTool annotation(AnnotationMirror annotationMirror) {
		return new AnnotationTool(annotationMirror);
	}

	public static MethodTool method(ExecutableElement methodElement) {
		return new MethodTool(methodElement);
	}

	public static StringTool string(String s) {
		return new StringTool(s);
	}
}
